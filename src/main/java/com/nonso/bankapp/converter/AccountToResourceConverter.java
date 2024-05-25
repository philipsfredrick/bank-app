package com.nonso.bankapp.converter;

import com.nonso.bankapp.dto.response.AccountResource;
import com.nonso.bankapp.entities.Account;
import com.nonso.bankapp.entities.Wallet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
@RequiredArgsConstructor
public class AccountToResourceConverter {

    private final WalletToResourceConverter walletToResourceConverter;

    public AccountResource convert(Account account, List<Wallet> wallets) {
        return AccountResource.builder()
                .id(account.getId())
                .firstname(account.getFirstName())
                .lastname(account.getLastName())
                .email(account.getEmail())
                .bankAccounts(wallets.parallelStream().map(walletToResourceConverter::convert).collect(toList()))
                .build();
    }
}
