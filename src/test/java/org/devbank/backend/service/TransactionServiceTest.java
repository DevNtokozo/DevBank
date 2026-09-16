package org.devbank.backend.service;

import org.devbank.backend.dto.TransactionResponse;
import org.devbank.backend.entity.Account;
import org.devbank.backend.entity.Transaction;
import org.devbank.backend.entity.User;
import org.devbank.backend.exception.ForbiddenException;
import org.devbank.backend.exception.NotFoundException;
import org.devbank.backend.repository.AccountRepository;
import org.devbank.backend.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransactionService transactionService;

    private User user;
    private User anotherUser;

    private Account fromAccount;
    private Account toAccount;

    private Transaction transaction;

    @BeforeEach
    void setUp() {

        user = new User(
                "testuser",
                "Test@123",
                "CUSTOMER",
                "ACTIVE"
        );

        user.setId(1L);

        anotherUser = new User(
                "anotheruser",
                "Test@123",
                "CUSTOMER",
                "ACTIVE"
        );

        anotherUser.setId(2L);

        fromAccount = new Account();

        fromAccount.setId(1L);
        fromAccount.setUser(user);
        fromAccount.setAccountNumber("1000000001");
        fromAccount.setAccountType("CHEQUE");
        fromAccount.setBalance(new BigDecimal("25000.00"));
        fromAccount.setStatus("ACTIVE");

        toAccount = new Account();

        toAccount.setId(2L);
        toAccount.setUser(anotherUser);
        toAccount.setAccountNumber("1000000003");
        toAccount.setAccountType("CHEQUE");
        toAccount.setBalance(new BigDecimal("15000.00"));
        toAccount.setStatus("ACTIVE");

        transaction = new Transaction();

        transaction.setId(1L);
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setTransactionType("TRANSFER");
        transaction.setAmount(new BigDecimal("500.00"));
        transaction.setReference("TEST-TRANSFER-001");
        transaction.setStatus("COMPLETED");
        transaction.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void shouldGetAccountTransactions() {

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(fromAccount));

        when(transactionRepository
                .findByFromAccountIdOrToAccountId(1L, 1L))
                .thenReturn(List.of(transaction));

        List<TransactionResponse> result =
                transactionService.getAccountTransactions(
                        1L,
                        1L
                );

        assertNotNull(result);
        assertEquals(1, result.size());

        TransactionResponse response = result.get(0);

        assertEquals(
                1L,
                response.getId()
        );

        assertEquals(
                "1000000001",
                response.getFromAccount()
        );

        assertEquals(
                "1000000003",
                response.getToAccount()
        );

        assertEquals(
                "TRANSFER",
                response.getTransactionType()
        );

        assertEquals(
                new BigDecimal("500.00"),
                response.getAmount()
        );

        assertEquals(
                "TEST-TRANSFER-001",
                response.getReference()
        );

        assertEquals(
                "COMPLETED",
                response.getStatus()
        );

        assertNotNull(response.getCreatedAt());

        verify(accountRepository).findById(1L);

        verify(transactionRepository)
                .findByFromAccountIdOrToAccountId(1L, 1L);
    }

    @Test
    void shouldRejectUnknownAccount() {

        when(accountRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> transactionService.getAccountTransactions(
                        999L,
                        1L
                )
        );

        verify(accountRepository).findById(999L);

        verify(
                transactionRepository,
                never()
        ).findByFromAccountIdOrToAccountId(
                anyLong(),
                anyLong()
        );
    }

    @Test
    void shouldRejectAccessToAnotherUsersAccount() {

        when(accountRepository.findById(2L))
                .thenReturn(Optional.of(toAccount));

        assertThrows(
                ForbiddenException.class,
                () -> transactionService.getAccountTransactions(
                        2L,
                        1L
                )
        );

        verify(accountRepository).findById(2L);

        verify(
                transactionRepository,
                never()
        ).findByFromAccountIdOrToAccountId(
                anyLong(),
                anyLong()
        );
    }

    @Test
    void shouldGetTransactionById() {

        when(transactionRepository.findById(1L))
                .thenReturn(Optional.of(transaction));

        TransactionResponse result =
                transactionService.getTransaction(
                        1L,
                        1L
                );

        assertNotNull(result);

        assertEquals(
                1L,
                result.getId()
        );

        assertEquals(
                "1000000001",
                result.getFromAccount()
        );

        assertEquals(
                "1000000003",
                result.getToAccount()
        );

        assertEquals(
                "TRANSFER",
                result.getTransactionType()
        );

        assertEquals(
                new BigDecimal("500.00"),
                result.getAmount()
        );

        assertEquals(
                "TEST-TRANSFER-001",
                result.getReference()
        );

        assertEquals(
                "COMPLETED",
                result.getStatus()
        );

        assertNotNull(result.getCreatedAt());

        verify(transactionRepository).findById(1L);
    }

    @Test
    void shouldRejectUnknownTransaction() {

        when(transactionRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> transactionService.getTransaction(
                        999L,
                        1L
                )
        );

        verify(transactionRepository).findById(999L);
    }

    @Test
    void shouldRejectTransactionAccessForUnauthorizedUser() {

        when(transactionRepository.findById(1L))
                .thenReturn(Optional.of(transaction));

        // User 3 owns neither the source nor destination account.
        Long unauthorizedUserId = 3L;

        assertThrows(
                ForbiddenException.class,
                () -> transactionService.getTransaction(
                        1L,
                        unauthorizedUserId
                )
        );

        verify(transactionRepository).findById(1L);
    }
}