package com.corebanking.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegistrationRequest(
		@NotBlank @Email @Size(max = 255) String email,
		@NotBlank @Size(min = 8, max = 128) String password,
		@NotBlank @Size(max = 120) String fullName,
		@Size(max = 32) String phone
) {
}
