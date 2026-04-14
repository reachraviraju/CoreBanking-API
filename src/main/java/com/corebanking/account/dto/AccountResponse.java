package com.corebanking.account.dto;

import com.corebanking.account.entity.AccountStatus;
import com.corebanking.account.entity.AccountType;

import java.math.BigDecimal;
import java.time.Instant;

public record AccountResponse(
		Long id,
		String accountNumber,
		AccountType accountType,
		AccountStatus status,
		BigDecimal balance,
		String currency,
		Long userId,
		Instant createdAt
) {
}
