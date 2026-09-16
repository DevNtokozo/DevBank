package org.devbank.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class BeneficiaryRequest {

    @NotBlank(message = "Beneficiary name is required")
    private String name;

    @NotBlank(message = "Beneficiary account number is required")
    private String accountNumber;

    @NotBlank(message = "Bank name is required")
    private String bankName;

    public BeneficiaryRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}