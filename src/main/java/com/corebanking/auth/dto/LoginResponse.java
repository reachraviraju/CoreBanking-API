package com.corebanking.auth.dto;

import com.corebanking.user.dto.UserResponse;

public record LoginResponse(
		String accessToken,
		String tokenType,
		long expiresInMillis,
		UserResponse user
) {
}
