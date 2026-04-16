package com.corebanking.common.exception;

import com.corebanking.transaction.exception.AccountNotOperationalException;
import com.corebanking.transaction.exception.InsufficientFundsException;
import com.corebanking.user.exception.DuplicateEmailException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(DuplicateEmailException.class)
	public ResponseEntity<Map<String, Object>> handleDuplicateEmail(DuplicateEmailException ex) {
		return error(HttpStatus.CONFLICT, ex.getMessage());
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
		return error(HttpStatus.NOT_FOUND, ex.getMessage());
	}

	@ExceptionHandler(InsufficientFundsException.class)
	public ResponseEntity<Map<String, Object>> handleInsufficientFunds(InsufficientFundsException ex) {
		return error(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	@ExceptionHandler(AccountNotOperationalException.class)
	public ResponseEntity<Map<String, Object>> handleAccountNotOperational(AccountNotOperationalException ex) {
		return error(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
		return error(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
		return error(HttpStatus.UNAUTHORIZED, "Invalid email or password");
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
		String message = ex.getMessage() != null && !ex.getMessage().isBlank()
				? ex.getMessage()
				: "Forbidden";
		return error(HttpStatus.FORBIDDEN, message);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
		Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
				.collect(Collectors.toMap(
						err -> err.getField(),
						err -> err.getDefaultMessage() != null ? err.getDefaultMessage() : "Invalid",
						(a, b) -> a,
						LinkedHashMap::new
				));
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("message", "Validation failed");
		body.put("errors", fieldErrors);
		body.put("timestamp", Instant.now().toString());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
	}

	private static ResponseEntity<Map<String, Object>> error(HttpStatus status, String message) {
		return ResponseEntity.status(status).body(simpleError(message));
	}

	private static Map<String, Object> simpleError(String message) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("message", message);
		body.put("timestamp", Instant.now().toString());
		return body;
	}
}
