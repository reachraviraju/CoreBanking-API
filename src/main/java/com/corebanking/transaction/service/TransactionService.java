package com.corebanking.transaction.service;

import com.corebanking.account.entity.Account;
import com.corebanking.account.entity.AccountStatus;
import com.corebanking.account.repository.AccountRepository;
import com.corebanking.common.exception.ResourceNotFoundException;
import com.corebanking.transaction.dto.MoneyMovementRequest;
import com.corebanking.transaction.dto.TransactionResponse;
import com.corebanking.transaction.dto.TransferRequest;
import com.corebanking.transaction.dto.TransferResult;
import com.corebanking.transaction.entity.Transaction;
import com.corebanking.transaction.entity.TransactionType;
import com.corebanking.notification.service.NotificationService;
import com.corebanking.security.AccessGuard;
import com.corebanking.transaction.exception.AccountNotOperationalException;
import com.corebanking.transaction.exception.InsufficientFundsException;
import com.corebanking.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

	private final AccountRepository accountRepository;
	private final TransactionRepository transactionRepository;
	private final NotificationService notificationService;
	private final AccessGuard accessGuard;

	@Transactional
	public TransactionResponse deposit(Long accountId, MoneyMovementRequest request) {
		Account account = getOwnedActiveAccountForUpdate(accountId);
		BigDecimal amount = normalizeAndValidateAmount(request.amount());
		String narration = trimToNull(request.narration());

		account.setBalance(account.getBalance().add(amount));
		Transaction saved = transactionRepository.save(buildTransaction(
				account,
				null,
				TransactionType.DEPOSIT,
				amount,
				narration
		));

		notificationService.createInApp(
				account.getUser(),
				"Deposit successful",
				String.format(
						"%s %s credited to account %s. Reference: %s",
						account.getCurrency(),
						amount,
						maskAccountNumber(account.getAccountNumber()),
						saved.getReference()
				)
		);

		return toResponse(saved);
	}

	@Transactional
	public TransactionResponse withdraw(Long accountId, MoneyMovementRequest request) {
		Account account = getOwnedActiveAccountForUpdate(accountId);
		BigDecimal amount = normalizeAndValidateAmount(request.amount());
		String narration = trimToNull(request.narration());

		if (account.getBalance().compareTo(amount) < 0) {
			throw new InsufficientFundsException("Insufficient balance for withdrawal");
		}

		account.setBalance(account.getBalance().subtract(amount));
		Transaction saved = transactionRepository.save(buildTransaction(
				account,
				null,
				TransactionType.WITHDRAWAL,
				amount,
				narration
		));

		notificationService.createInApp(
				account.getUser(),
				"Withdrawal successful",
				String.format(
						"%s %s debited from account %s. Reference: %s",
						account.getCurrency(),
						amount,
						maskAccountNumber(account.getAccountNumber()),
						saved.getReference()
				)
		);

		return toResponse(saved);
	}

	@Transactional(readOnly = true)
	public List<TransactionResponse> listForAccount(Long accountId) {
		accessGuard.requireAccountOwner(accountId);
		return transactionRepository.findByAccount_IdOrderByCreatedAtDesc(accountId)
				.stream()
				.map(TransactionService::toResponse)
				.toList();
	}

	@Transactional
	public TransferResult transfer(Long sourceAccountId, TransferRequest request) {
		accessGuard.requireAccountOwner(sourceAccountId);
		String targetNumber = request.targetAccountNumber().trim();
		String narration = trimToNull(request.narration());

		Account targetPreview = accountRepository.findByAccountNumber(targetNumber)
				.orElseThrow(() -> new ResourceNotFoundException("Target account not found: " + targetNumber));

		if (targetPreview.getId().equals(sourceAccountId)) {
			throw new AccountNotOperationalException("Cannot transfer to the same account");
		}

		long lowId = Math.min(sourceAccountId, targetPreview.getId());
		long highId = Math.max(sourceAccountId, targetPreview.getId());

		// Lock rows in a stable order to avoid deadlocks for concurrent transfers.
		Account first = accountRepository.findByIdForUpdate(lowId)
				.orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + lowId));
		Account second = accountRepository.findByIdForUpdate(highId)
				.orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + highId));

		Account source = first.getId().equals(sourceAccountId) ? first : second;
		Account destination = first.getId().equals(sourceAccountId) ? second : first;

		requireActive(source);
		requireActive(destination);

		if (!source.getCurrency().equals(destination.getCurrency())) {
			throw new AccountNotOperationalException("Currency mismatch between accounts");
		}

		BigDecimal amount = normalizeAndValidateAmount(request.amount());
		if (source.getBalance().compareTo(amount) < 0) {
			throw new InsufficientFundsException("Insufficient balance for transfer");
		}

		source.setBalance(source.getBalance().subtract(amount));
		destination.setBalance(destination.getBalance().add(amount));

		Transaction savedOut = transactionRepository.save(buildTransaction(
				source,
				destination,
				TransactionType.TRANSFER_OUT,
				amount,
				narration
		));
		Transaction savedIn = transactionRepository.save(buildTransaction(
				destination,
				source,
				TransactionType.TRANSFER_IN,
				amount,
				narration
		));

		notificationService.createInApp(
				source.getUser(),
				"Transfer sent",
				String.format(
						"%s %s sent to account %s. Reference: %s",
						source.getCurrency(),
						amount,
						maskAccountNumber(destination.getAccountNumber()),
						savedOut.getReference()
				)
		);
		notificationService.createInApp(
				destination.getUser(),
				"Transfer received",
				String.format(
						"%s %s received from account %s. Reference: %s",
						destination.getCurrency(),
						amount,
						maskAccountNumber(source.getAccountNumber()),
						savedIn.getReference()
				)
		);

		return new TransferResult(toResponse(savedOut), toResponse(savedIn));
	}

	private Account getOwnedActiveAccountForUpdate(Long accountId) {
		accessGuard.requireAccountOwner(accountId);
		Account account = accountRepository.findByIdForUpdate(accountId)
				.orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));
		requireActive(account);
		return account;
	}

	private static Transaction buildTransaction(
			Account account,
			Account counterparty,
			TransactionType type,
			BigDecimal amount,
			String narration
	) {
		Transaction tx = new Transaction();
		tx.setAccount(account);
		tx.setCounterpartyAccount(counterparty);
		tx.setTransactionType(type);
		tx.setAmount(amount);
		tx.setNarration(narration);
		return tx;
	}

	private static void requireActive(Account account) {
		if (account.getStatus() != AccountStatus.ACTIVE) {
			throw new AccountNotOperationalException(
					"Account " + account.getAccountNumber() + " is not active for postings");
		}
	}

	private static BigDecimal normalizeAndValidateAmount(BigDecimal amount) {
		BigDecimal normalized = normalizeAmount(amount);
		validateAmount(normalized);
		return normalized;
	}

	private static BigDecimal normalizeAmount(BigDecimal amount) {
		return amount.setScale(2, RoundingMode.HALF_UP);
	}

	private static void validateAmount(BigDecimal amount) {
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Amount must be greater than zero");
		}
	}

	private static String trimToNull(String narration) {
		if (narration == null || narration.isBlank()) {
			return null;
		}
		return narration.trim();
	}

	private static String maskAccountNumber(String accountNumber) {
		if (accountNumber == null || accountNumber.length() < 4) {
			return "****";
		}
		return "****" + accountNumber.substring(accountNumber.length() - 4);
	}

	private static TransactionResponse toResponse(Transaction tx) {
		Long counterpartyId = tx.getCounterpartyAccount() != null ? tx.getCounterpartyAccount().getId() : null;
		return new TransactionResponse(
				tx.getId(),
				tx.getReference(),
				tx.getTransactionType(),
				tx.getAmount(),
				tx.getNarration(),
				tx.getAccount().getId(),
				counterpartyId,
				tx.getCreatedAt()
		);
	}
}
