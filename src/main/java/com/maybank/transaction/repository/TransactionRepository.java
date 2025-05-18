package com.maybank.transaction.repository;

import com.maybank.transaction.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByCustomerId(String customerId, Pageable pageable);
    Page<Transaction> findByAccountNumberIn(List<String> accountNumbers, Pageable pageable);
    Page<Transaction> findByDescriptionContainingIgnoreCase(String description, Pageable pageable);
}
