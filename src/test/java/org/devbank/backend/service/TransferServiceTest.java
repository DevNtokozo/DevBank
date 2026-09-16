package org.devbank.backend.service;

import org.devbank.backend.dto.TransferRequest;
import org.devbank.backend.dto.TransferResponse;
import org.devbank.backend.entity.Account;
import org.devbank.backend.entity.Transaction;
import org.devbank.backend.entity.User;
import org.devbank.backend.exception.BadRequestException;
import org.devbank.backend.repository.AccountRepository;
import org.devbank.backend.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransferService transferService;

    @Test
    void shouldCompleteSuccessfulTransfer() {

        User user = new User(
                "testuser",
                "Test@123",
                "CUSTOMER",
                "ACTIVE"
        );

        user.setId(1L);

        Account source = new Account();

        source.setId(1L);
        source.setUser(user);
        source.setAccountNumber("1000000001");
        source.setAccountType("CHEQUE");
        source.setBalance(
                new BigDecimal("25000.00")
        );
        source.setStatus("ACTIVE");

        Account destination = new Account();

        destination.setId(3L);
        destination.setAccountNumber("1000000003");
        destination.setAccountType("CHEQUE");
        destination.setBalance(
                new BigDecimal("15000.00")
        );
        destination.setStatus("ACTIVE");

        TransferRequest request =
                new TransferRequest();

        request.setFromAccountId(1L);
        request.setToAccountNumber("1000000003");
        request.setAmount(
                new BigDecimal("1000.00")
        );
        request.setReference(
                "Test Transfer"
        );

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(source));

        when(accountRepository.findByAccountNumber(
                "1000000003"
        )).thenReturn(Optional.of(destination));

        Transaction savedTransaction =
                new Transaction();

        savedTransaction.setId(1L);
        savedTransaction.setFromAccount(source);
        savedTransaction.setToAccount(destination);
        savedTransaction.setTransactionType(
                "TRANSFER"
        );
        savedTransaction.setAmount(
                new BigDecimal("1000.00")
        );
        savedTransaction.setReference(
                "Test Transfer"
        );
        savedTransaction.setStatus(
                "COMPLETED"
        );

        when(transactionRepository.save(
                any(Transaction.class)
        )).thenReturn(savedTransaction);

        TransferResponse response =
                transferService.transfer(
                        request,
                        1L
                );

        assertNotNull(response);

        assertEquals(
                "COMPLETED",
                response.getStatus()
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
                new BigDecimal("1000.00"),
                response.getAmount()
        );

        assertEquals(
                "Test Transfer",
                response.getReference()
        );

        assertEquals(
                new BigDecimal("24000.00"),
                source.getBalance()
        );

        assertEquals(
                new BigDecimal("16000.00"),
                destination.getBalance()
        );

        verify(accountRepository)
                .save(source);

        verify(accountRepository)
                .save(destination);

        verify(transactionRepository)
                .save(any(Transaction.class));
    }

    @Test
    void shouldRejectInsufficientFunds() {

        User user = new User(
                "testuser",
                "Test@123",
                "CUSTOMER",
                "ACTIVE"
        );

        user.setId(1L);

        Account source = new Account();

        source.setId(1L);
        source.setUser(user);
        source.setAccountNumber("1000000001");
        source.setAccountType("CHEQUE");
        source.setBalance(
                new BigDecimal("500.00")
        );
        source.setStatus("ACTIVE");

        Account destination = new Account();

        destination.setId(3L);
        destination.setAccountNumber("1000000003");
        destination.setAccountType("CHEQUE");
        destination.setBalance(
                new BigDecimal("15000.00")
        );
        destination.setStatus("ACTIVE");

        TransferRequest request =
                new TransferRequest();

        request.setFromAccountId(1L);
        request.setToAccountNumber(
                "1000000003"
        );
        request.setAmount(
                new BigDecimal("1000.00")
        );
        request.setReference(
                "Insufficient Funds Test"
        );

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(source));

        when(accountRepository.findByAccountNumber(
                "1000000003"
        )).thenReturn(Optional.of(destination));

        assertThrows(
                BadRequestException.class,
                () -> transferService.transfer(
                        request,
                        1L
                )
        );

        assertEquals(
                new BigDecimal("500.00"),
                source.getBalance()
        );

        assertEquals(
                new BigDecimal("15000.00"),
                destination.getBalance()
        );

        verify(accountRepository, never())
                .save(any(Account.class));

        verify(transactionRepository, never())
                .save(any(Transaction.class));
    }

    @Test
    void shouldRejectSameAccountTransfer() {

        User user = new User(
                "testuser",
                "Test@123",
                "CUSTOMER",
                "ACTIVE"
        );

        user.setId(1L);

        Account account = new Account();

        account.setId(1L);
        account.setUser(user);
        account.setAccountNumber(
                "1000000001"
        );
        account.setAccountType(
                "CHEQUE"
        );
        account.setBalance(
                new BigDecimal("25000.00")
        );
        account.setStatus("ACTIVE");

        TransferRequest request =
                new TransferRequest();

        request.setFromAccountId(1L);
        request.setToAccountNumber(
                "1000000001"
        );
        request.setAmount(
                new BigDecimal("1000.00")
        );
        request.setReference(
                "Same Account Test"
        );

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        when(accountRepository.findByAccountNumber(
                "1000000001"
        )).thenReturn(Optional.of(account));

        assertThrows(
                BadRequestException.class,
                () -> transferService.transfer(
                        request,
                        1L
                )
        );

        assertEquals(
                new BigDecimal("25000.00"),
                account.getBalance()
        );

        verify(accountRepository, never())
                .save(any(Account.class));

        verify(transactionRepository, never())
                .save(any(Transaction.class));
    }
}