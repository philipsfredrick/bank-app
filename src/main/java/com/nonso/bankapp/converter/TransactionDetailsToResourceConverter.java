package com.nonso.bankapp.converter;

import com.nonso.bankapp.dto.response.TransactionDetailResource;
import com.nonso.bankapp.entities.TransactionDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static java.math.RoundingMode.HALF_UP;

@Component
@RequiredArgsConstructor
public class TransactionDetailsToResourceConverter {

    private final TransactionToResourceConverter transactionToResourceConverter;

    public TransactionDetailResource convert(TransactionDetails transactionDetails) {

        return TransactionDetailResource.builder()
                .id(transactionDetails.getId())
                .type(transactionDetails.getOperationType())
                .oldBalance(transactionDetails.getOldBalance().setScale(2, HALF_UP))
                .newBalance(transactionDetails.getNewBalance().setScale(2, HALF_UP))
                .walletNo(transactionDetails.getWallet().getWalletNumber())
                .createdAt(transactionDetails.getCreatedAt())
                .transactionResource(transactionToResourceConverter.convert(transactionDetails.getTransaction()))
                .build();
    }
}
