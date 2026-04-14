package com.corebanking.user.controller;

import com.corebanking.user.dto.UserRegistrationRequest;
import com.corebanking.user.dto.UserResponse;
import com.corebanking.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PostMapping
	public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRegistrationRequest request) {
		UserResponse body = userService.register(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(body);
	}
}
