package com.maybank.transaction.service;

import com.maybank.transaction.entity.Transaction;
import com.maybank.transaction.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ConcurrentModificationException;
import java.util.List;

@Slf4j
@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;


    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public Page<Transaction> searchByCustomerId(String customerId, Pageable pageable) {
        return transactionRepository.findByCustomerId(customerId, pageable);
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public Page<Transaction> searchByAccounts(List<String> accountNumbers, Pageable pageable) {
        return transactionRepository.findByAccountNumberIn(accountNumbers, pageable);
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public Page<Transaction> searchByDescription(String description, Pageable pageable) {
        return transactionRepository.findByDescriptionContainingIgnoreCase(description, pageable);
    }


    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public Page<Transaction> searchAll(Pageable pageable) {
        return transactionRepository.findAll(pageable);
    }

    @Transactional
    public Transaction updateDescription(Long id, String newDescription) {
        log.debug("Updating description for transaction ID: {}", id);

        return transactionRepository.findById(id)
                .map(transaction -> {
                    transaction.setDescription(newDescription);
                    try {
                        return transactionRepository.save(transaction);
                    } catch (ObjectOptimisticLockingFailureException ex) {
                        log.warn("Concurrent modification detected for transaction {}", id);
                        throw new ConcurrentModificationException(
                                "Transaction was modified by another user. Please refresh and try again.");
                    }
                })
                .orElse(null);
    }
}
