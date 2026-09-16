package org.devbank.backend.service;

import org.devbank.backend.dto.AccountResponse;
import org.devbank.backend.dto.BalanceResponse;
import org.devbank.backend.entity.Account;
import org.devbank.backend.entity.User;
import org.devbank.backend.exception.ForbiddenException;
import org.devbank.backend.exception.NotFoundException;
import org.devbank.backend.repository.AccountRepository;
import org.devbank.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AccountService accountService;

    private User user;
    private User anotherUser;
    private Account account;

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

        account = new Account();

        account.setId(1L);
        account.setUser(user);
        account.setAccountNumber("1000000001");
        account.setAccountType("CHEQUE");
        account.setBalance(new BigDecimal("25000.00"));
        account.setStatus("ACTIVE");
    }

    @Test
    void shouldGetAllAccountsForUser() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(accountRepository.findByUser(user))
                .thenReturn(List.of(account));

        List<AccountResponse> result =
                accountService.getAccountsForUser(1L);

        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals(
                "1000000001",
                result.get(0).getAccountNumber()
        );

        assertEquals(
                new BigDecimal("25000.00"),
                result.get(0).getBalance()
        );

        verify(userRepository).findById(1L);
        verify(accountRepository).findByUser(user);
    }

    @Test
    void shouldRejectUnknownUser() {

        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> accountService.getAccountsForUser(999L)
        );

        verify(userRepository).findById(999L);
        verify(accountRepository, never()).findByUser(any());
    }

    @Test
    void shouldGetSpecificAccount() {

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        AccountResponse result =
                accountService.getAccount(1L, 1L);

        assertNotNull(result);

        assertEquals(
                1L,
                result.getId()
        );

        assertEquals(
                "1000000001",
                result.getAccountNumber()
        );

        assertEquals(
                "CHEQUE",
                result.getAccountType()
        );

        assertEquals(
                new BigDecimal("25000.00"),
                result.getBalance()
        );

        assertEquals(
                "ACTIVE",
                result.getStatus()
        );

        verify(accountRepository).findById(1L);
    }

    @Test
    void shouldRejectAccessToAnotherUsersAccount() {

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        assertThrows(
                ForbiddenException.class,
                () -> accountService.getAccount(1L, 2L)
        );

        verify(accountRepository).findById(1L);
    }

    @Test
    void shouldRejectUnknownAccount() {

        when(accountRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> accountService.getAccount(999L, 1L)
        );

        verify(accountRepository).findById(999L);
    }

    @Test
    void shouldGetAccountBalance() {

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        BalanceResponse result =
                accountService.getBalance(1L, 1L);

        assertNotNull(result);

        assertEquals(
                "1000000001",
                result.getAccountNumber()
        );

        assertEquals(
                new BigDecimal("25000.00"),
                result.getBalance()
        );

        assertEquals(
                "ZAR",
                result.getCurrency()
        );

        verify(accountRepository).findById(1L);
    }

    @Test
    void shouldRejectBalanceAccessToAnotherUsersAccount() {

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        assertThrows(
                ForbiddenException.class,
                () -> accountService.getBalance(1L, 2L)
        );

        verify(accountRepository).findById(1L);
    }

    @Test
    void shouldRejectUnknownAccountWhenGettingBalance() {

        when(accountRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> accountService.getBalance(999L, 1L)
        );

        verify(accountRepository).findById(999L);
    }
}