package com.nonso.bankapp.converter;

import com.nonso.bankapp.dto.response.AdminResource;
import com.nonso.bankapp.dto.response.TransactionResource;
import com.nonso.bankapp.entities.Admin;
import com.nonso.bankapp.entities.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static java.math.RoundingMode.HALF_UP;

@Component
@RequiredArgsConstructor
public class TransactionToResourceConverter {

    public TransactionResource convert(Admin admin, String sourceWalletNo,
                                       String destinationWalletNo, Transaction transaction) {
        return TransactionResource.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .status(transaction.getStatus())
                .currencyCode(transaction.getCurrencyCode())
                .sourceWalletNo(sourceWalletNo)
                .destinationWalletNo(destinationWalletNo)
                .authorizedBy(AdminToResourceConverter.convert(admin))
                .createdAt(transaction.getCreatedAt())
                .build();
    }

    public TransactionResource convert(Transaction transaction) {
        return TransactionResource.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount().setScale(2,HALF_UP))
                .status(transaction.getStatus())
                .currencyCode(transaction.getCurrencyCode())
                .sourceWalletNo(transaction.getWallet().getWalletNumber())
                .destinationWalletNo(transaction.getConsortWallet().getWalletNumber())
                .authorizedBy(AdminToResourceConverter.convert(transaction.getAdmin()))
                .createdAt(transaction.getCreatedAt())
                .build();
    }

    static class AdminToResourceConverter {
        public static AdminResource convert(Admin admin) {
            return AdminResource.builder()
                    .id(admin.getId())
                    .firstName(admin.getFirstName())
                    .lastName(admin.getLastName())
                    .build();
        }
    }
}
