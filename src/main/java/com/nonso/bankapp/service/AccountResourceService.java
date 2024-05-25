package com.nonso.bankapp.service;

import com.nonso.bankapp.converter.AccountToResourceConverter;
import com.nonso.bankapp.dto.request.CreateAccountRequest;
import com.nonso.bankapp.dto.request.CreateWalletRequest;
import com.nonso.bankapp.dto.response.AccountResource;
import com.nonso.bankapp.dto.response.WalletResource;
import com.nonso.bankapp.entities.Account;
import com.nonso.bankapp.entities.Wallet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountResourceService {

    private final AccountService accountService;

    private final WalletService walletService;

    private final AccountToResourceConverter accountToResourceConverter;

    @Transactional
    public AccountResource createCustomerAccount(CreateAccountRequest request) {
        Account account = accountService.createAccount(request);
        List<Wallet> wallets = walletService.createAccountWallets(account, request.getBankAccounts());

        return accountToResourceConverter.convert(account, wallets);
    }

    @Transactional
    public AccountResource addNewWallet(Long accountId, List<CreateWalletRequest> request) {
        Account account = accountService.retrieveAccount(accountId);
        List<Wallet> wallets = walletService.createAccountWallets(account, request);

        return accountToResourceConverter.convert(account, wallets);
    }

    public AccountResource retrieveAccountDetails(Long accountId) {
        Account account = accountService.retrieveAccount(accountId);
        List<Wallet> wallets = walletService.retrieveAccountWallets(account);

        return accountToResourceConverter.convert(account, wallets);
    }

    public List<WalletResource> retrieveAccountWallets(Long customerId) {
        return retrieveAccountDetails(customerId).getBankAccounts();
    }
}
