package org.devbank.backend.controller;

import org.devbank.backend.dto.TransferRequest;
import org.devbank.backend.dto.TransferResponse;
import org.devbank.backend.service.TransferService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transfers")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    public ResponseEntity<TransferResponse> transfer(
            @Valid @RequestBody TransferRequest request,
            HttpSession session) {

        Object userId = session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        TransferResponse response =
                transferService.transfer(
                        request,
                        (Long) userId
                );

        return ResponseEntity.ok(response);
    }
}