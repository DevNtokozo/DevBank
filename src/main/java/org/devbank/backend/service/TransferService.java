package org.devbank.backend.service;

import org.devbank.backend.dto.TransferRequest;
import org.devbank.backend.dto.TransferResponse;
import org.devbank.backend.entity.Account;
import org.devbank.backend.entity.Transaction;
import org.devbank.backend.repository.AccountRepository;
import org.devbank.backend.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.devbank.backend.exception.BadRequestException;
import org.devbank.backend.exception.ForbiddenException;
import org.devbank.backend.exception.NotFoundException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TransferService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransferService(
            AccountRepository accountRepository,
            TransactionRepository transactionRepository) {

        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public TransferResponse transfer(
            TransferRequest request,
            Long userId) {

        Account fromAccount = accountRepository
                .findById(request.getFromAccountId())
                .orElseThrow(() ->
                        new NotFoundException("Source account not found"));

        if (!fromAccount.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Access denied");
        }

        if (!"ACTIVE".equals(fromAccount.getStatus())) {
            throw new BadRequestException("Source account is inactive");
        }

        Account toAccount = accountRepository
                .findByAccountNumber(request.getToAccountNumber())
                .orElseThrow(() ->
                        new NotFoundException("Destination account not found"));

        if (!"ACTIVE".equals(toAccount.getStatus())) {
            throw new BadRequestException(
                    "Destination account is inactive"
            );
        }

        if (fromAccount.getId().equals(toAccount.getId())) {
            throw new BadRequestException(
                    "Cannot transfer money to the same account"
            );
        }

        BigDecimal amount = request.getAmount();

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException(
                    "Transfer amount must be greater than zero"
            );
        }

        if (amount.scale() > 2) {
            throw new BadRequestException(
                    "Transfer amount cannot have more than two decimal places"
            );
        }

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new BadRequestException(
                    "Insufficient funds"
            );
        }

        fromAccount.setBalance(
                fromAccount.getBalance().subtract(amount)
        );

        toAccount.setBalance(
                toAccount.getBalance().add(amount)
        );

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        Transaction transaction = new Transaction();

        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setTransactionType("TRANSFER");
        transaction.setAmount(amount);
        transaction.setReference(request.getReference());
        transaction.setStatus("COMPLETED");
        transaction.setCreatedAt(LocalDateTime.now());

        Transaction savedTransaction =
                transactionRepository.save(transaction);

        return new TransferResponse(
                savedTransaction.getId(),
                savedTransaction.getStatus(),
                fromAccount.getAccountNumber(),
                toAccount.getAccountNumber(),
                amount,
                savedTransaction.getReference()
        );
    }
}