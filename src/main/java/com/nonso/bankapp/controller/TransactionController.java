package com.nonso.bankapp.controller;

import com.nonso.bankapp.dto.request.TransferRequest;
import com.nonso.bankapp.dto.response.PaginatedTransactionDetailResource;
import com.nonso.bankapp.dto.response.TransactionResource;
import com.nonso.bankapp.service.TransactionResourceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionResourceService transactionResourceService;

    @PostMapping(value = "", headers = {"Authorization"},
    consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionResource> transact(@Valid @RequestBody TransferRequest request) {
        return new ResponseEntity<>(transactionResourceService.transact(request), CREATED);
    }

    @GetMapping(value = "/wallets/{walletId}", headers = {"Authorization"}, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<PaginatedTransactionDetailResource> retrieveWalletTransactions(
            @PathVariable("walletId") Long walletId,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "5") Integer size) {
        return new ResponseEntity<>(transactionResourceService.retrieveWalletTransactions(walletId,page, size), OK);
    }

    @GetMapping(value = "/accounts/{accountId}", headers = {"Authorization"}, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<PaginatedTransactionDetailResource> retrieveAccountTransactions(
            @PathVariable("accountId") Long accountId,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "5") Integer size) {
        return new ResponseEntity<>(transactionResourceService.retrieveAccountTransactions(accountId, page, size), OK);
    }
}
