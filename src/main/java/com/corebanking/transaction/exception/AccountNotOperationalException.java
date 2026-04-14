package com.corebanking.transaction.exception;

public class AccountNotOperationalException extends RuntimeException {

	public AccountNotOperationalException(String message) {
		super(message);
	}
}
