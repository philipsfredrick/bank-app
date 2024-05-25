package com.nonso.bankapp.service;

import com.nonso.bankapp.entities.Admin;
import com.nonso.bankapp.exception.BankAppException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Service
@RequiredArgsConstructor
public class CredentialService {

    public Admin getAdminAccount() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            return (Admin) authentication.getPrincipal();
        } catch (Exception e) {
            throw new BankAppException("could not authenticate request", INTERNAL_SERVER_ERROR);
        }
    }
}
