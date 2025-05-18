package com.maybank.transaction.controller;

import com.maybank.transaction.dto.AccountSearchDTO;
import com.maybank.transaction.dto.DescriptionSearchDTO;
import com.maybank.transaction.dto.PaginationDTO;
import com.maybank.transaction.entity.Transaction;
import com.maybank.transaction.service.TransactionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@Validated
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/search/customer/{customerId}")
    public Page<Transaction> searchByCustomerId(@NotBlank @PathVariable String customerId, @Valid @RequestBody PaginationDTO paginationDTO) {
        return transactionService.searchByCustomerId(customerId, paginationDTO.toPageable());
    }

    @PostMapping("/search/accounts")
    public Page<Transaction> searchByAccounts(@Valid @RequestBody AccountSearchDTO accountSearchDTO) {
        return transactionService.searchByAccounts(accountSearchDTO.getAccountNumbers(), accountSearchDTO.toPageable());
    }

    @PostMapping("/search/description")
    public Page<Transaction> searchByDescription(@Valid @RequestBody DescriptionSearchDTO descriptionSearchDTO) {
        return transactionService.searchByDescription(descriptionSearchDTO.getDescription(), descriptionSearchDTO.toPageable());
    }

    @PostMapping("/searchAll")
    public Page<Transaction> searchAll(@Valid @RequestBody PaginationDTO paginationDTO) {
        return transactionService.searchAll(paginationDTO.toPageable());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateDescription(@NotNull @PathVariable Long id, @NotBlank @RequestBody String newDescription) {
        Transaction transaction = transactionService.updateDescription(id, newDescription);
        if (transaction == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(transaction);
    }
}