package com.nonso.bankapp.service;

import com.nonso.bankapp.converter.TransactionDetailsToResourceConverter;
import com.nonso.bankapp.converter.TransactionToResourceConverter;
import com.nonso.bankapp.dto.request.TransferRequest;
import com.nonso.bankapp.dto.response.PaginatedTransactionDetailResource;
import com.nonso.bankapp.dto.response.TransactionDetailResource;
import com.nonso.bankapp.dto.response.TransactionResource;
import com.nonso.bankapp.entities.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionResourceService {

    private final CredentialService credentialService;

    private final AccountService accountService;

    private final WalletService walletService;

    private final TransactionService transactionService;

    private final TransactionHistoryService transactionHistoryService;

    private final TransactionToResourceConverter transactionToResourceConverter;

    private final TransactionDetailsToResourceConverter transactionDetailsToResourceConverter;


    @Transactional
    public TransactionResource transact(TransferRequest request) {
        Admin admin = credentialService.getAdminAccount();
        Transaction transaction = transactionService.process(request, admin);
        return transactionToResourceConverter.convert(
                admin, request.getSourceWalletNo(), request.getDestinationWalletNo(), transaction
        );
    }

    @Transactional
    public PaginatedTransactionDetailResource retrieveWalletTransactions(Long walletId, Integer page, Integer size) {
        Wallet wallet = walletService.retrieveWallet(walletId);
        Page<TransactionDetails> transactionDetails = transactionHistoryService.retrieveWalletTransactions(
                page,size, wallet.getId()
        );
        List<TransactionDetailResource> transactionDetailResources = transactionDetails.getContent()
                .stream().map(transactionDetailsToResourceConverter::convert).toList();

        return PaginatedTransactionDetailResource.builder()
                .transactionDetailResource(transactionDetailResources)
                .currentPage(transactionDetails.getPageable().getPageNumber() + 1)
                .totalElements(transactionDetails.getTotalElements())
                .totalPage(transactionDetails.getTotalPages())
                .build();
    }

    @Transactional
    public PaginatedTransactionDetailResource retrieveAccountTransactions(Long accountId, Integer page, Integer size) {
        Account account = accountService.retrieveAccount(accountId);
        Page<TransactionDetails> transactionDetails = transactionHistoryService.retrieveAccountTransactionDetails(
                page, size, account.getId()
        );
        List<TransactionDetailResource> transactionDetailResources = transactionDetails.getContent()
                .stream().map(transactionDetailsToResourceConverter::convert).toList();

        return PaginatedTransactionDetailResource.builder()
                .transactionDetailResource(transactionDetailResources)
                .currentPage(transactionDetails.getPageable().getPageNumber() + 1)
                .totalElements(transactionDetails.getTotalElements())
                .totalPage(transactionDetails.getTotalPages())
                .build();
    }
}
