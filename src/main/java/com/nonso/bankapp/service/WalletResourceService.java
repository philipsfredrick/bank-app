package com.nonso.bankapp.service;

import com.nonso.bankapp.converter.WalletToResourceConverter;
import com.nonso.bankapp.dto.response.WalletResource;
import com.nonso.bankapp.entities.Wallet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletResourceService {

    public final WalletService walletService;

    public final WalletToResourceConverter walletToResourceConverter;

    public WalletResource retrieveWalletAccount(Long walletId, Long accountId) {
        Wallet wallet = walletService.retrieveWallet(walletId, accountId);
        return walletToResourceConverter.convert(wallet);
    }

    public WalletResource retrieveWalletAccount(Long walletId) {
        Wallet wallet = walletService.retrieveWallet(walletId);
        return walletToResourceConverter.convert(wallet);
    }
}
