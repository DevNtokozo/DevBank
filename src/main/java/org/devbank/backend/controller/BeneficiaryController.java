package org.devbank.backend.controller;

import org.devbank.backend.dto.BeneficiaryRequest;
import org.devbank.backend.dto.BeneficiaryResponse;
import org.devbank.backend.service.BeneficiaryService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/beneficiaries")
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;

    public BeneficiaryController(
            BeneficiaryService beneficiaryService) {

        this.beneficiaryService = beneficiaryService;
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<BeneficiaryResponse>>
    getBeneficiaries(
            @PathVariable Long accountId,
            HttpSession session) {

        Object userId = session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(
                beneficiaryService.getBeneficiaries(
                        accountId,
                        (Long) userId
                )
        );
    }

    @PostMapping("/account/{accountId}")
    public ResponseEntity<BeneficiaryResponse>
    addBeneficiary(
            @PathVariable Long accountId,
            @Valid @RequestBody BeneficiaryRequest request,
            HttpSession session) {

        Object userId = session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(
                beneficiaryService.addBeneficiary(
                        accountId,
                        request,
                        (Long) userId
                )
        );
    }

    @DeleteMapping("/{beneficiaryId}")
    public ResponseEntity<String> deleteBeneficiary(
            @PathVariable Long beneficiaryId,
            HttpSession session) {

        Object userId = session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        beneficiaryService.deleteBeneficiary(
                beneficiaryId,
                (Long) userId
        );

        return ResponseEntity.ok(
                "Beneficiary deleted successfully"
        );
    }
}