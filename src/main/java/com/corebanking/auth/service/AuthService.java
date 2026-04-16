package com.corebanking.auth.service;

import com.corebanking.auth.dto.LoginRequest;
import com.corebanking.auth.dto.LoginResponse;
import com.corebanking.common.exception.ResourceNotFoundException;
import com.corebanking.security.JwtProperties;
import com.corebanking.security.JwtService;
import com.corebanking.security.UserPrincipal;
import com.corebanking.user.dto.UserResponse;
import com.corebanking.user.entity.User;
import com.corebanking.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final JwtProperties jwtProperties;
	private final UserRepository userRepository;

	@Transactional(readOnly = true)
	public LoginResponse login(LoginRequest request) {
		String email = request.email().trim().toLowerCase();
		UsernamePasswordAuthenticationToken credentials =
				new UsernamePasswordAuthenticationToken(email, request.password());
		Authentication authentication = authenticationManager.authenticate(credentials);

		UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
		User user = userRepository.findById(principal.getUserId())
				.orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));

		String token = jwtService.generateToken(user);
		UserResponse userResponse = toUserResponse(user);

		return new LoginResponse(token, "Bearer", jwtProperties.expirationMs(), userResponse);
	}

	private static UserResponse toUserResponse(User user) {
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
