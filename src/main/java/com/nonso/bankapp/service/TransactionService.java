package com.nonso.bankapp.service;

import com.nonso.bankapp.dto.ErrorCode;
import com.nonso.bankapp.dto.request.TransferRequest;
import com.nonso.bankapp.entities.Admin;
import com.nonso.bankapp.entities.Transaction;
import com.nonso.bankapp.entities.TransactionDetails;
import com.nonso.bankapp.entities.Wallet;
import com.nonso.bankapp.entities.enums.OperationType;
import com.nonso.bankapp.entities.enums.TransactionStatus;
import com.nonso.bankapp.exception.BankAppException;
import com.nonso.bankapp.exception.BankServiceException;
import com.nonso.bankapp.repository.TransactionDetailRepository;
import com.nonso.bankapp.repository.TransactionRepository;
import com.nonso.bankapp.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;

import static com.nonso.bankapp.dto.ErrorCode.*;
import static com.nonso.bankapp.entities.enums.OperationType.CREDIT;
import static com.nonso.bankapp.entities.enums.OperationType.DEBIT;
import static java.lang.String.format;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final WalletService walletService;

    private final WalletRepository walletRepository;

    private final TransactionTemplate transactionTemplate;

    private final TransactionRepository transactionRepository;

    private final TransactionDetailRepository transactionDetailRepository;

    public synchronized Transaction process(TransferRequest request, Admin admin) {
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        return transactionTemplate.execute(jpaTransactionStatus -> {
            // retrieve wallets for update
            Wallet sourceWallet = walletService.retrieveWalletForUpdate(request.getSourceWalletNo());
            Wallet destinationWallet = walletService.retrieveWalletForUpdate(request.getDestinationWalletNo());

            // validate currency and amount if sufficient and accurate
            validateRequest(sourceWallet, destinationWallet, request.getAmount());

            // update wallet balances
            BigDecimal sourceAccountOldBalance = sourceWallet.getBalance();
            BigDecimal sourceAccountNewBalance = sourceWallet.getBalance().add(request.getAmount().negate());
            BigDecimal destinationAccountOldBalance = destinationWallet.getBalance();
            BigDecimal destinationAccountNewBalance = destinationWallet.getBalance().add(request.getAmount());

            // execute wallet balance updates
            updateAccountBalance(request, sourceWallet, destinationWallet, sourceAccountNewBalance, destinationAccountNewBalance);

            // create the record for the transaction
            Transaction transaction = createTransactionRecords(request, admin, sourceWallet, destinationWallet);

            // create debit transaction details
            createTransactionDetailsRecords(
                    request.getAmount().negate(), transaction, DEBIT, sourceAccountOldBalance, sourceWallet
            );
            // create credit transaction details
            createTransactionDetailsRecords(
                    request.getAmount(), transaction, CREDIT, destinationAccountOldBalance, destinationWallet
            );
            log.info("Successfully transferred fund from wallet: {} to wallet: {}: amount: {}, currencyCode: {}",
                    sourceWallet.getWalletNumber(), destinationWallet.getWalletNumber(),
                    request.getAmount(), sourceWallet.getCurrencyCode());

            return transaction;
        });
    }

    private void createTransactionDetailsRecords(BigDecimal amount, Transaction transaction,
                                                 OperationType type, BigDecimal oldBalance,
                                                 Wallet wallet) {
        try {
            transactionDetailRepository.save(
                    TransactionDetails.builder()
                            .operationType(type)
                            .amount(amount)
                            .oldBalance(oldBalance)
                            .newBalance(wallet.getBalance())
                            .wallet(wallet)
                            .transaction(transaction)
                            .account(wallet.getAccount())
                            .build()
            );
        } catch (Exception e) {
            String msg = format("An error has occurred while creating transaction detail record: for wallet no: %s: " +
                            "amount: %f. Possible reason(s) : %s",
                    wallet.getWalletNumber(), amount, ExceptionUtils.getRootCauseMessage(e));
            log.error(msg);
            throw new BankAppException(
                    "Failed creation of transaction detail records", INTERNAL_SERVER_ERROR, ACCOUNT_BALANCE_UPDATE_FAILED);
        }
    }

    private Transaction createTransactionRecords(TransferRequest request, Admin admin,
                                                 Wallet sourceWallet, Wallet destinationWallet) {
        try {
            return transactionRepository.save(
                    Transaction.builder()
                            .operationType(DEBIT)
                            .amount(request.getAmount())
                            .status(TransactionStatus.SUCCESSFUL)
                            .currencyCode(sourceWallet.getCurrencyCode())
                            .wallet(sourceWallet)
                            .admin(admin)
                            .consortWallet(destinationWallet)
                            .build()
            );
        } catch (Exception e) {
            String msg = format("An error has occurred while creating transaction record: source wallet no: %s and " +
                            "destination wallet no: %s: Amount: %f. Possible reason(s) : %s",
                    sourceWallet.getWalletNumber(), destinationWallet.getWalletNumber(),
                    request.getAmount().floatValue(),
                    ExceptionUtils.getRootCauseMessage(e));
            log.error(msg);
            throw new BankAppException(
                    "Failed creation of transaction records", INTERNAL_SERVER_ERROR, ACCOUNT_BALANCE_UPDATE_FAILED);
        }
    }

    private void updateAccountBalance(TransferRequest request, Wallet sourceWallet,
                                      Wallet destinationWallet, BigDecimal sourceWalletNewBalance,
                                      BigDecimal destinationWalletNewBalance) {
        try {
            sourceWallet.setBalance(sourceWalletNewBalance);
            walletRepository.save(sourceWallet);

            destinationWallet.setBalance(destinationWalletNewBalance);
            walletRepository.save(destinationWallet);
        } catch (Exception e) {
            String msg = format("An error has occurred while updating account balance of source wallet no: %s and " +
                    "destination wallet no: %s: Amount: %f. Possible reason(s) : %s",
                    sourceWallet.getWalletNumber(), destinationWallet.getWalletNumber(), request.getAmount().floatValue(),
                    ExceptionUtils.getRootCauseMessage(e));
            log.error(msg);
            throw new BankAppException(
                    "Failed updating account balances", INTERNAL_SERVER_ERROR, ACCOUNT_BALANCE_UPDATE_FAILED);
        }
    }

    private void validateRequest(Wallet sourceWallet, Wallet destinationWallet, BigDecimal amount) {
        if (!sourceWallet.getCurrencyCode().equalsIgnoreCase(destinationWallet.getCurrencyCode())) {
            throw new BankServiceException(
                    "Currency code mismatch. Please check the accounts", BAD_REQUEST, CURRENCY_CODE_MISMATCH);
        }

        if (sourceWallet.getBalance().compareTo(amount) < 0) {
            throw new BankServiceException(
                    "Insufficient funds in the wallet to complete this transaction", BAD_REQUEST, INSUFFICIENT_BALANCE);
        }
    }
}
