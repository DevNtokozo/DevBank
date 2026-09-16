package org.devbank.backend.dto;

import java.math.BigDecimal;

public class TransferResponse {

    private Long transactionId;
    private String status;
    private String fromAccount;
    private String toAccount;
    private BigDecimal amount;
    private String reference;

    public TransferResponse() {
    }

    public TransferResponse(
            Long transactionId,
            String status,
            String fromAccount,
            String toAccount,
            BigDecimal amount,
            String reference) {

        this.transactionId = transactionId;
        this.status = status;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.reference = reference;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public String getStatus() {
        return status;
    }

    public String getFromAccount() {
        return fromAccount;
    }

    public String getToAccount() {
        return toAccount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getReference() {
        return reference;
    }
}