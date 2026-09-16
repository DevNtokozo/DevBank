package org.devbank.backend.controller;

import org.devbank.backend.dto.TransactionResponse;
import org.devbank.backend.service.TransactionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(
            TransactionService transactionService) {

        this.transactionService = transactionService;
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionResponse>>
    getAccountTransactions(
            @PathVariable Long accountId,
            HttpSession session) {

        Object userId = session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(
                transactionService.getAccountTransactions(
                        accountId,
                        (Long) userId
                )
        );
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> getTransaction(
            @PathVariable Long transactionId,
            HttpSession session) {

        Object userId = session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(
                transactionService.getTransaction(
                        transactionId,
                        (Long) userId
                )
        );
    }
}