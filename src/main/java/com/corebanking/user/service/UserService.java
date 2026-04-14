package com.corebanking.user.service;

import com.corebanking.user.dto.UserRegistrationRequest;
import com.corebanking.user.dto.UserResponse;
import com.corebanking.user.entity.User;
import com.corebanking.user.exception.DuplicateEmailException;
import com.corebanking.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public UserResponse register(UserRegistrationRequest request) {
		String normalizedEmail = request.email().trim().toLowerCase();
		if (userRepository.existsByEmail(normalizedEmail)) {
			throw new DuplicateEmailException(normalizedEmail);
		}

		User user = new User();
		user.setEmail(normalizedEmail);
		user.setPasswordHash(passwordEncoder.encode(request.password()));
		user.setFullName(request.fullName().trim());
		user.setPhone(request.phone() != null && !request.phone().isBlank() ? request.phone().trim() : null);
		user.setEnabled(true);

		User saved = userRepository.save(user);
		return toResponse(saved);
	}

	private static UserResponse toResponse(User user) {
		return new UserResponse(
				user.getId(),
				user.getEmail(),
				user.getFullName(),
				user.getPhone(),
				user.isEnabled(),
				user.getCreatedAt()
		);
	}
}
