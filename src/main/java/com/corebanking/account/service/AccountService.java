package com.corebanking.account.service;

import com.corebanking.account.dto.AccountResponse;
import com.corebanking.account.dto.CreateAccountRequest;
import com.corebanking.account.entity.Account;
import com.corebanking.account.entity.AccountStatus;
import com.corebanking.account.repository.AccountRepository;
import com.corebanking.common.exception.ResourceNotFoundException;
import com.corebanking.security.AccessGuard;
import com.corebanking.user.entity.User;
import com.corebanking.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

	private final AccountRepository accountRepository;
	private final UserRepository userRepository;
	private final AccessGuard accessGuard;

	@Transactional
	public AccountResponse create(Long userId, CreateAccountRequest request) {
		accessGuard.requireSameUser(userId);
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

		Account account = new Account();
		account.setAccountNumber(generateAccountNumber());
		account.setAccountType(request.accountType());
		account.setStatus(AccountStatus.ACTIVE);
		account.setCurrency(resolveCurrency(request.currency()));
		account.setUser(user);

		Account saved = accountRepository.save(account);
		return toResponse(saved);
	}

	@Transactional(readOnly = true)
	public List<AccountResponse> listByUserId(Long userId) {
		accessGuard.requireSameUser(userId);
		if (!userRepository.existsById(userId)) {
			throw new ResourceNotFoundException("User not found with id: " + userId);
		}
		return accountRepository.findByUser_Id(userId).stream()
				.map(AccountService::toResponse)
				.toList();
	}

	private static String generateAccountNumber() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	private static String resolveCurrency(String currency) {
		if (currency == null || currency.isBlank()) {
			return "INR";
		}
		return currency.trim().toUpperCase();
	}

	private static AccountResponse toResponse(Account account) {
		return new AccountResponse(
				account.getId(),
				account.getAccountNumber(),
				account.getAccountType(),
				account.getStatus(),
				account.getBalance(),
				account.getCurrency(),
				account.getUser().getId(),
				account.getCreatedAt()
		);
	}
}
