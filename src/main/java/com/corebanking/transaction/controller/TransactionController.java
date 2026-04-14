package com.corebanking.transaction.controller;

import com.corebanking.transaction.dto.MoneyMovementRequest;
import com.corebanking.transaction.dto.TransactionResponse;
import com.corebanking.transaction.dto.TransferRequest;
import com.corebanking.transaction.dto.TransferResult;
import com.corebanking.transaction.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts/{accountId}")
@RequiredArgsConstructor
public class TransactionController {

	private final TransactionService transactionService;

	@GetMapping("/transactions")
	public ResponseEntity<List<TransactionResponse>> list(@PathVariable Long accountId) {
		return ResponseEntity.ok(transactionService.listForAccount(accountId));
	}

	@PostMapping("/deposit")
	public ResponseEntity<TransactionResponse> deposit(
			@PathVariable Long accountId,
			@Valid @RequestBody MoneyMovementRequest request) {
		TransactionResponse body = transactionService.deposit(accountId, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(body);
	}

	@PostMapping("/withdraw")
	public ResponseEntity<TransactionResponse> withdraw(
			@PathVariable Long accountId,
			@Valid @RequestBody MoneyMovementRequest request) {
		TransactionResponse body = transactionService.withdraw(accountId, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(body);
	}

	@PostMapping("/transfer")
	public ResponseEntity<TransferResult> transfer(
			@PathVariable Long accountId,
			@Valid @RequestBody TransferRequest request) {
		TransferResult body = transactionService.transfer(accountId, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(body);
	}
}
