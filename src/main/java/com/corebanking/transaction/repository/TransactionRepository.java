package com.corebanking.transaction.repository;

import com.corebanking.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	Optional<Transaction> findByReference(String reference);

	List<Transaction> findByAccount_IdOrderByCreatedAtDesc(Long accountId);
}
