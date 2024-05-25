package com.nonso.bankapp.service;

import com.nonso.bankapp.dto.ErrorCode;
import com.nonso.bankapp.dto.request.CreateAccountRequest;
import com.nonso.bankapp.entities.Account;
import com.nonso.bankapp.entities.Admin;
import com.nonso.bankapp.exception.AccountServiceException;
import com.nonso.bankapp.repository.AccountRepository;
import com.nonso.bankapp.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.nonso.bankapp.dto.ErrorCode.ACCOUNT_DOES_NOT_EXIST;
import static com.nonso.bankapp.dto.ErrorCode.EMAIL_ALREADY_IN_USE;
import static java.lang.String.format;
import static org.springframework.http.HttpStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final CredentialService credentialService;


    public Account createAccount(CreateAccountRequest accountRequest) {
        try {
            // check if account exists
            validateRequest(accountRequest);
            // create account and save in repository
            return accountRepository.save(
                new Account(accountRequest.getEmail(), accountRequest.getFirstName(), accountRequest.getLastName())
            );
        } catch (Exception e) {
            log.error(format("An error occurred while creating Account. Possible reasons: %s",
                    e.getLocalizedMessage()));
            if (e instanceof AccountServiceException) {
                throw e;
            }
            throw new AccountServiceException(
                    "Account could not be created. Please contact support", INTERNAL_SERVER_ERROR);
        }
    }


    private void validateRequest(CreateAccountRequest request) {
        accountRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new AccountServiceException("email already in use, please update" +
            "and try again", BAD_REQUEST, EMAIL_ALREADY_IN_USE));
    }

    public Account retrieveAccount(Long accountId) {
        return accountRepository.findById(accountId).orElseThrow(
                () -> new AccountServiceException("Invalid account id", NOT_FOUND, ACCOUNT_DOES_NOT_EXIST));
    }
}
