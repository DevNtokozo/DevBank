package org.devbank.backend.controller;

import jakarta.servlet.http.HttpSession;
import org.devbank.backend.dto.AccountResponse;
import org.devbank.backend.dto.BalanceResponse;
import org.devbank.backend.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Get all accounts belonging to the currently logged-in user.
     *
     * GET /api/v1/accounts
     */
    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAccounts(
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        return ResponseEntity.ok(
                accountService.getAccountsForUser(userId)
        );
    }

    /**
     * Get a specific account belonging to the currently logged-in user.
     *
     * GET /api/v1/accounts/{accountId}
     */
    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> getAccount(
            @PathVariable Long accountId,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        return ResponseEntity.ok(
                accountService.getAccount(
                        accountId,
                        userId
                )
        );
    }

    /**
     * Get the balance of a specific account.
     *
     * GET /api/v1/accounts/{accountId}/balance
     */
    @GetMapping("/{accountId}/balance")
    public ResponseEntity<BalanceResponse> getBalance(
            @PathVariable Long accountId,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        return ResponseEntity.ok(
                accountService.getBalance(
                        accountId,
                        userId
                )
        );
    }
}