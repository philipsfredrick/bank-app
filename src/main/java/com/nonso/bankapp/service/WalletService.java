package com.nonso.bankapp.service;

import com.nonso.bankapp.dto.ErrorCode;
import com.nonso.bankapp.dto.request.CreateWalletRequest;
import com.nonso.bankapp.entities.Account;
import com.nonso.bankapp.entities.Wallet;
import com.nonso.bankapp.exception.AccountServiceException;
import com.nonso.bankapp.exception.BankServiceException;
import com.nonso.bankapp.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static com.nonso.bankapp.dto.ErrorCode.WALLET_DOES_NOT_EXIST;
import static com.nonso.bankapp.utils.GeneralConstants.MAX_ACCOUNT_NUMBER;
import static java.lang.String.format;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    public List<Wallet> createAccountWallets(Account account, List<CreateWalletRequest> request) {
        try {
            List<Wallet> wallets = convert(account, request);
            walletRepository.saveAll(wallets);

            return retrieveAccountWallets(account);
        } catch (Exception e) {
            log.error(format("An error occurred while creating account wallet. Possible reasons: %s",
                    e.getLocalizedMessage()));
            throw new AccountServiceException("Account wallet could not be created. Please contact support", INTERNAL_SERVER_ERROR);
        }
    }


    public List<Wallet> retrieveAccountWallets(Account account) {
        return walletRepository.findWalletByAccount_Id(account.getId());
    }

    public Wallet retrieveWallet(Long walletId, Long accountId) {
        return walletRepository.findWalletByIdAndAccountById(walletId, accountId).orElseThrow(
                () -> new BankServiceException("Wallet does not exist", NOT_FOUND, WALLET_DOES_NOT_EXIST));
    }

    public Wallet retrieveWallet(Long walletId) {
        return walletRepository.findById(walletId).orElseThrow(
                () -> new BankServiceException("Wallet does not exist", NOT_FOUND, WALLET_DOES_NOT_EXIST));
    }

    public Wallet retrieveWalletForUpdate(String walletNumber) {
        return walletRepository.findForUpdateByWalletNumber(walletNumber).orElseThrow(
                () -> new BankServiceException("Wallet does not exist", NOT_FOUND, WALLET_DOES_NOT_EXIST));
    }


    private List<Wallet> convert(Account account, List<CreateWalletRequest> request) {
        List<Wallet> wallets = new ArrayList<>();

        request.forEach(details -> {
            Wallet wallet = new Wallet();
            wallet.setBalance(details.getInitialDeposit());
            wallet.setCurrencyCode(details.getCurrencyCode());
            wallet.setAccount(account);
            wallet.setWalletNumber(generateUniqueWalletNumber());
            wallets.add(wallet);
        });
        return wallets;
    }

    private String generateUniqueWalletNumber() {
        boolean walletNumberIsNotUnique = true;
        String walletNumber = generateWalletNumber();

        int generationCount = 0;
        for (; walletNumberIsNotUnique && generationCount < 10; generationCount++) {
            Optional<Wallet> wallet = walletRepository.findWalletByWalletNumber(walletNumber);
            if (wallet.isEmpty()) {
                walletNumberIsNotUnique = false;
            } else {
                walletNumber = generateWalletNumber();
            }
        }

        if (walletNumberIsNotUnique && generationCount == 10) {
            throw new AccountServiceException(
                    "Unique account number could not be generated in 10 trails", INTERNAL_SERVER_ERROR
            );
        }
        return walletNumber;
    }

    private static String generateWalletNumber() {
        Random random = new Random();
        int randomNum = random.nextInt(MAX_ACCOUNT_NUMBER - 1) + 1; // generate a random number between 1 and MAX_ACCOUNT_NUMBER
        String accountNumber = format("%9d", randomNum);
        int sum = 0;
        for (int i = 0; i < accountNumber.length(); i++) {
            sum += Character.getNumericValue(accountNumber.charAt(i));
        }
        int checkDigit = (sum * 7) % 10; // calculate the check digit
        accountNumber = accountNumber + checkDigit; // add the check digit to the end of the account number
        return accountNumber;
    }
}
