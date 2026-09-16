package org.devbank.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponse {

    private Long id;
    private String fromAccount;
    private String toAccount;
    private String transactionType;
    private BigDecimal amount;
    private String reference;
    private String status;
    private LocalDateTime createdAt;

    public TransactionResponse() {
    }

    public TransactionResponse(
            Long id,
            String fromAccount,
            String toAccount,
            String transactionType,
            BigDecimal amount,
            String reference,
            String status,
            LocalDateTime createdAt) {

        this.id = id;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.transactionType = transactionType;
        this.amount = amount;
        this.reference = reference;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getFromAccount() {
        return fromAccount;
    }

    public String getToAccount() {
        return toAccount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getReference() {
        return reference;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}