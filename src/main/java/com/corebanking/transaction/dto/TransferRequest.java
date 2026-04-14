package com.corebanking.transaction.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record TransferRequest(
		@NotBlank @Size(max = 34) String targetAccountNumber,
		@NotNull @DecimalMin(value = "0.01", inclusive = true) BigDecimal amount,
		@Size(max = 512) String narration
) {
}
