package org.devbank.backend.service;

import org.devbank.backend.dto.AccountResponse;
import org.devbank.backend.dto.BalanceResponse;
import org.devbank.backend.entity.Account;
import org.devbank.backend.entity.User;
import org.devbank.backend.exception.ForbiddenException;
import org.devbank.backend.exception.NotFoundException;
import org.devbank.backend.repository.AccountRepository;
import org.devbank.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountService(
            AccountRepository accountRepository,
            UserRepository userRepository) {

        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    public List<AccountResponse> getAccountsForUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new NotFoundException("User not found"));

        return accountRepository.findByUser(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public AccountResponse getAccount(
            Long accountId,
            Long userId) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() ->
                        new NotFoundException("Account not found"));

        if (!account.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Access denied");
        }

        return toResponse(account);
    }

    public BalanceResponse getBalance(
            Long accountId,
            Long userId) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() ->
                        new NotFoundException("Account not found"));

        if (!account.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Access denied");
        }

        return new BalanceResponse(
                account.getAccountNumber(),
                account.getBalance(),
                "ZAR"
        );
    }

    private AccountResponse toResponse(Account account) {

        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getAccountType(),
                account.getBalance(),
                account.getStatus()
        );
    }
}