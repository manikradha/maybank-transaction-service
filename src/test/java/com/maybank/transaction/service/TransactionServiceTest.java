package com.maybank.transaction.service;

import com.maybank.transaction.entity.Transaction;
import com.maybank.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSearchByCustomerId() {
        String customerId = "12345";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Transaction> mockPage = new PageImpl<>(List.of(new Transaction()));

        when(transactionRepository.findByCustomerId(customerId, pageable)).thenReturn(mockPage);

        Page<Transaction> result = transactionService.searchByCustomerId(customerId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(transactionRepository, times(1)).findByCustomerId(customerId, pageable);
    }

    @Test
    void testSearchByAccounts() {
        List<String> accountNumbers = Arrays.asList("ACC123", "ACC456");
        Pageable pageable = PageRequest.of(0, 10);
        Page<Transaction> mockPage = new PageImpl<>(List.of(new Transaction()));

        when(transactionRepository.findByAccountNumberIn(accountNumbers, pageable)).thenReturn(mockPage);

        Page<Transaction> result = transactionService.searchByAccounts(accountNumbers, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(transactionRepository, times(1)).findByAccountNumberIn(accountNumbers, pageable);
    }

    @Test
    void testSearchByDescription() {
        String description = "test";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Transaction> mockPage = new PageImpl<>(List.of(new Transaction()));

        when(transactionRepository.findByDescriptionContainingIgnoreCase(description, pageable)).thenReturn(mockPage);

        Page<Transaction> result = transactionService.searchByDescription(description, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(transactionRepository, times(1)).findByDescriptionContainingIgnoreCase(description, pageable);
    }

    @Test
    void testSearchAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Transaction> mockPage = new PageImpl<>(List.of(new Transaction()));

        when(transactionRepository.findAll(pageable)).thenReturn(mockPage);

        Page<Transaction> result = transactionService.searchAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(transactionRepository, times(1)).findAll(pageable);
    }

    @Test
    void testUpdateDescription_Success() {
        Long id = 1L;
        String newDescription = "Updated Description";
        Transaction transaction = new Transaction();
        transaction.setId(id);

        when(transactionRepository.findById(id)).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        Transaction result = transactionService.updateDescription(id, newDescription);

        assertNotNull(result);
        assertEquals(newDescription, result.getDescription());
        verify(transactionRepository, times(1)).findById(id);
        verify(transactionRepository, times(1)).save(transaction);
    }

    @Test
    void testUpdateDescription_ConcurrentModification() {
        Long id = 1L;
        String newDescription = "Updated Description";
        Transaction transaction = new Transaction();
        transaction.setId(id);

        when(transactionRepository.findById(id)).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(transaction)).thenThrow(new ConcurrentModificationException());

        assertThrows(ConcurrentModificationException.class, () -> transactionService.updateDescription(id, newDescription));

        verify(transactionRepository, times(1)).findById(id);
        verify(transactionRepository, times(1)).save(transaction);
    }

    @Test
    void testUpdateDescription_NotFound() {
        Long id = 1L;
        String newDescription = "Updated Description";

        when(transactionRepository.findById(id)).thenReturn(Optional.empty());

        Transaction result = transactionService.updateDescription(id, newDescription);

        assertNull(result);
        verify(transactionRepository, times(1)).findById(id);
        verify(transactionRepository, never()).save(any());
    }
}
