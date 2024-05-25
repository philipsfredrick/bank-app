package com.nonso.bankapp.controller;

import com.nonso.bankapp.dto.request.CreateAccountRequest;
import com.nonso.bankapp.dto.request.CreateWalletRequest;
import com.nonso.bankapp.dto.response.AccountResource;
import com.nonso.bankapp.dto.response.WalletResource;
import com.nonso.bankapp.service.AccountResourceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountResourceService accountResourceService;

    @PostMapping(value = "", headers = {"Authorization"},
            consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<AccountResource> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        return new ResponseEntity<>(accountResourceService.createCustomerAccount(request), CREATED);
    }

    @PostMapping(value = "/{accountId}", headers = {"Authorization"},
    consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<AccountResource> addNewWallet(@PathVariable("accountId") Long accountId,
                                                        @Valid @RequestBody List<CreateWalletRequest> request) {
        return new ResponseEntity<>(accountResourceService.addNewWallet(accountId, request), CREATED);
    }

    @GetMapping(value = "/{accountId}", headers = {"Authorization"}, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<AccountResource> retrieveAccountDetails(@PathVariable("accountId") Long accountId) {
        return new ResponseEntity<>(accountResourceService.retrieveAccountDetails(accountId), OK);
    }

    @GetMapping(value = "/{accountId}/balances", headers = {"Authorization"}, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<WalletResource>> retrieveAccountWallets(@PathVariable("accountId") Long accountId) {
        return new ResponseEntity<>(accountResourceService.retrieveAccountWallets(accountId), OK);
    }
}
