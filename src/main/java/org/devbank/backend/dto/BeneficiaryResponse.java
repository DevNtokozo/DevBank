package org.devbank.backend.dto;

import java.time.LocalDateTime;

public class BeneficiaryResponse {

    private Long id;
    private String name;
    private String accountNumber;
    private String bankName;
    private String status;
    private LocalDateTime createdAt;

    public BeneficiaryResponse() {
    }

    public BeneficiaryResponse(
            Long id,
            String name,
            String accountNumber,
            String bankName,
            String status,
            LocalDateTime createdAt) {

        this.id = id;
        this.name = name;
        this.accountNumber = accountNumber;
        this.bankName = bankName;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}