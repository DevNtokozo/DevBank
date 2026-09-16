package org.devbank.backend.service;

import org.devbank.backend.dto.BeneficiaryRequest;
import org.devbank.backend.dto.BeneficiaryResponse;
import org.devbank.backend.entity.Account;
import org.devbank.backend.entity.Beneficiary;
import org.devbank.backend.exception.BadRequestException;
import org.devbank.backend.exception.ForbiddenException;
import org.devbank.backend.exception.NotFoundException;
import org.devbank.backend.repository.AccountRepository;
import org.devbank.backend.repository.BeneficiaryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BeneficiaryService {

    private final BeneficiaryRepository beneficiaryRepository;
    private final AccountRepository accountRepository;

    public BeneficiaryService(
            BeneficiaryRepository beneficiaryRepository,
            AccountRepository accountRepository) {

        this.beneficiaryRepository = beneficiaryRepository;
        this.accountRepository = accountRepository;
    }

    public List<BeneficiaryResponse> getBeneficiaries(
            Long accountId,
            Long userId) {

        Account account = getOwnedAccount(accountId, userId);

        return beneficiaryRepository
                .findByAccountId(account.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public BeneficiaryResponse addBeneficiary(
            Long accountId,
            BeneficiaryRequest request,
            Long userId) {

        Account account = getOwnedAccount(accountId, userId);

        if (beneficiaryRepository
                .existsByAccountIdAndAccountNumber(
                        accountId,
                        request.getAccountNumber())) {

            throw new BadRequestException(
                    "Beneficiary already exists"
            );
        }

        Account destinationAccount =
                accountRepository
                        .findByAccountNumber(
                                request.getAccountNumber()
                        )
                        .orElseThrow(() ->
                                new NotFoundException(
                                        "Beneficiary account not found"
                                ));

        if (!"ACTIVE".equals(destinationAccount.getStatus())) {

            throw new BadRequestException(
                    "Beneficiary account is inactive"
            );
        }

        if (account.getId().equals(destinationAccount.getId())) {

            throw new BadRequestException(
                    "Cannot add your own account as a beneficiary"
            );
        }

        Beneficiary beneficiary = new Beneficiary();

        beneficiary.setAccount(account);
        beneficiary.setName(request.getName());
        beneficiary.setAccountNumber(
                request.getAccountNumber()
        );
        beneficiary.setBankName(request.getBankName());
        beneficiary.setStatus("ACTIVE");
        beneficiary.setCreatedAt(LocalDateTime.now());

        Beneficiary saved =
                beneficiaryRepository.save(beneficiary);

        return toResponse(saved);
    }

    public void deleteBeneficiary(
            Long beneficiaryId,
            Long userId) {

        Beneficiary beneficiary =
                beneficiaryRepository
                        .findById(beneficiaryId)
                        .orElseThrow(() ->
                                new NotFoundException(
                                        "Beneficiary not found"
                                ));

        if (!beneficiary.getAccount()
                .getUser()
                .getId()
                .equals(userId)) {

            throw new ForbiddenException(
                    "Access denied"
            );
        }

        beneficiaryRepository.delete(beneficiary);
    }

    private Account getOwnedAccount(
            Long accountId,
            Long userId) {

        Account account =
                accountRepository
                        .findById(accountId)
                        .orElseThrow(() ->
                                new NotFoundException(
                                        "Account not found"
                                ));

        if (!account.getUser()
                .getId()
                .equals(userId)) {

            throw new ForbiddenException(
                    "Access denied"
            );
        }

        return account;
    }

    private BeneficiaryResponse toResponse(
            Beneficiary beneficiary) {

        return new BeneficiaryResponse(
                beneficiary.getId(),
                beneficiary.getName(),
                beneficiary.getAccountNumber(),
                beneficiary.getBankName(),
                beneficiary.getStatus(),
                beneficiary.getCreatedAt()
        );
    }
}