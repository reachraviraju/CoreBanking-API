package com.corebanking.account.dto;

import com.corebanking.account.entity.AccountType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateAccountRequest(
		@NotNull AccountType accountType,
		@Size(min = 3, max = 3)
		@Pattern(regexp = "[A-Za-z]{3}", message = "currency must be a 3-letter ISO code")
		String currency
) {
}
