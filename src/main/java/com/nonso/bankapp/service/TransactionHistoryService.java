package com.nonso.bankapp.service;

import com.nonso.bankapp.entities.TransactionDetails;
import com.nonso.bankapp.repository.TransactionDetailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionHistoryService {

    private final TransactionDetailRepository transactionDetailRepository;

    public Page<TransactionDetails> retrieveWalletTransactions(Integer page, Integer size, Long walletId) {
        return transactionDetailRepository.findTransactionDetailsByWallet_Id(walletId, getPageable(page, size));
    }

    public Page<TransactionDetails> retrieveAccountTransactionDetails(Integer page, Integer size, Long accountId) {
        return transactionDetailRepository.findTransactionDetailsByAccount_Id(accountId, getPageable(page, size));
    }


    private Pageable getPageable(Integer page, Integer size) {
        size = size < 1 || size > 10 ? 5 : size;
        page = page < 1 ? 1 : page;
        return PageRequest.of(--page, size, Sort.Direction.DESC, "createdAt");
    }
}
