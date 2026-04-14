package com.corebanking.common.exception;

import com.corebanking.transaction.exception.AccountNotOperationalException;
import com.corebanking.transaction.exception.InsufficientFundsException;
import com.corebanking.user.exception.DuplicateEmailException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.http.ResponseEntity;
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
		return ResponseEntity.status(HttpStatus.CONFLICT).body(simpleError(ex.getMessage()));
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(simpleError(ex.getMessage()));
	}

	@ExceptionHandler(InsufficientFundsException.class)
	public ResponseEntity<Map<String, Object>> handleInsufficientFunds(InsufficientFundsException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(simpleError(ex.getMessage()));
	}

	@ExceptionHandler(AccountNotOperationalException.class)
	public ResponseEntity<Map<String, Object>> handleAccountNotOperational(AccountNotOperationalException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(simpleError(ex.getMessage()));
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(simpleError("Invalid email or password"));
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
		String message = ex.getMessage() != null && !ex.getMessage().isBlank()
				? ex.getMessage()
				: "Forbidden";
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(simpleError(message));
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

	private static Map<String, Object> simpleError(String message) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("message", message);
		body.put("timestamp", Instant.now().toString());
		return body;
	}
}
