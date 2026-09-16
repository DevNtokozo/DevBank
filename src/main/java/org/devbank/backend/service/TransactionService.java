package org.devbank.backend.service;

import org.devbank.backend.dto.TransactionResponse;
import org.devbank.backend.entity.Account;
import org.devbank.backend.entity.Transaction;
import org.devbank.backend.repository.AccountRepository;
import org.devbank.backend.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.devbank.backend.exception.ForbiddenException;
import org.devbank.backend.exception.NotFoundException;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionService(
            TransactionRepository transactionRepository,
            AccountRepository accountRepository) {

        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    public List<TransactionResponse> getAccountTransactions(
            Long accountId,
            Long userId) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() ->
                        new NotFoundException("Account not found"));

        if (!account.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Access denied");
        }

        return transactionRepository
                .findByFromAccountIdOrToAccountId(
                        accountId,
                        accountId
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public TransactionResponse getTransaction(
            Long transactionId,
            Long userId) {

        Transaction transaction = transactionRepository
                .findById(transactionId)
                .orElseThrow(() ->
                        new NotFoundException("Transaction not found"));

        boolean ownsFromAccount =
                transaction.getFromAccount() != null
                        && transaction.getFromAccount()
                        .getUser()
                        .getId()
                        .equals(userId);

        boolean ownsToAccount =
                transaction.getToAccount() != null
                        && transaction.getToAccount()
                        .getUser()
                        .getId()
                        .equals(userId);

        if (!ownsFromAccount && !ownsToAccount) {
            throw new ForbiddenException("Access denied");
        }

        return toResponse(transaction);
    }

    private TransactionResponse toResponse(
            Transaction transaction) {

        return new TransactionResponse(
                transaction.getId(),
                transaction.getFromAccount()
                        .getAccountNumber(),
                transaction.getToAccount()
                        .getAccountNumber(),
                transaction.getTransactionType(),
                transaction.getAmount(),
                transaction.getReference(),
                transaction.getStatus(),
                transaction.getCreatedAt()
        );
    }
}