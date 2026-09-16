package org.devbank.backend.dto;

import java.math.BigDecimal;

public class AccountResponse {

    private Long id;
    private String accountNumber;
    private String accountType;
    private BigDecimal balance;
    private String status;

    public AccountResponse() {
    }

    public AccountResponse(
            Long id,
            String accountNumber,
            String accountType,
            BigDecimal balance,
            String status) {

        this.id = id;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getStatus() {
        return status;
    }
}