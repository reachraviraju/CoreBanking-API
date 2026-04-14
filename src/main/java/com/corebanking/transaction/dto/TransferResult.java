package com.corebanking.transaction.dto;

public record TransferResult(TransactionResponse debitLeg, TransactionResponse creditLeg) {
}
