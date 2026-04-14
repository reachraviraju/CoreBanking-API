package com.corebanking.user.dto;

import java.time.Instant;

public record UserResponse(
		Long id,
		String email,
		String fullName,
		String phone,
		boolean enabled,
		Instant createdAt
) {
}
