package com.corebanking.transaction.dto;

import com.corebanking.transaction.entity.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionResponse(
		Long id,
		String reference,
		TransactionType transactionType,
		BigDecimal amount,
		String narration,
		Long accountId,
		Long counterpartyAccountId,
		Instant createdAt
) {
}
