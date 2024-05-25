package com.nonso.bankapp.converter;

import com.nonso.bankapp.dto.response.WalletResource;
import com.nonso.bankapp.entities.Wallet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;
import java.util.List;

import static java.math.RoundingMode.HALF_UP;

@Component
@RequiredArgsConstructor
public class WalletToResourceConverter {

    public WalletResource convert(Wallet wallet) {
        return WalletResource.builder()
                .id(wallet.getId())
                .currencyCode(wallet.getCurrencyCode())
                .walletNumber(wallet.getWalletNumber())
                .balance(wallet.getBalance().setScale(2, HALF_UP))
                .build();
    }
}
