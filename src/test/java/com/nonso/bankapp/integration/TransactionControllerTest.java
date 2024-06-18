package com.nonso.bankapp.integration;

import com.nonso.bankapp.AbstractIntegrationTest;
import com.nonso.bankapp.dto.request.TransferRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static com.nonso.bankapp.utils.TestUtil.asJsonString;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TransactionControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("transfer funds to inter account wallet successfully")
    @Sql(scripts = {"/scripts/create_admin.sql", "/scripts/create_account.sql"}, executionPhase = BEFORE_TEST_METHOD)
    @Sql(scripts = {"/scripts/delete_data.sql"}, executionPhase = AFTER_TEST_METHOD)
    void transferFundsToInterAccountWalletSuccessfully() throws Exception {
        String token = getAccessToken();
        TransferRequest requestBody = buildTransferRequest(BigDecimal.valueOf(500.00f), "9688710733", "9130704994");

        mvc.perform(post("/api/v1/transactions")
                .header("Authorization", "Bearer " + token)
                .content(asJsonString(requestBody))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.status").value("SUCCESSFUL"))
                .andExpect(jsonPath("$.amount").value(500.00))
                .andExpect(jsonPath("$.currency_code").value("USD"))
                .andExpect(jsonPath("$.source_wallet_no").value("9688710733"))
                .andExpect(jsonPath("$.destination_wallet_no").value("9130704994"))
                .andExpect(jsonPath("$.authorized_by.id").value("2"))
                .andExpect(jsonPath("$.authorized_by.first_name").value("Peter"))
                .andExpect(jsonPath("$.authorized_by.last_name").value("John"));
    }

    @Test
    @DisplayName("transfer funds to intra account wallet successfully")
    @Sql(scripts = {"/scripts/create_admin.sql", "/scripts/create_account.sql"}, executionPhase = BEFORE_TEST_METHOD)
    @Sql(scripts = {"/scripts/delete_data.sql"}, executionPhase = AFTER_TEST_METHOD)
    void transferFundsToIntraAccountWalletSuccessfully() throws Exception {
        String token = getAccessToken();
        TransferRequest requestBody = buildTransferRequest(BigDecimal.valueOf(50.00f), "9688710733", "7324108711");

        mvc.perform(post("/api/v1/transactions")
                .header("Authorization", "Bearer " + token)
                .content(asJsonString(requestBody))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.status").value("SUCCESSFUL"))
                .andExpect(jsonPath("$.amount").value(50.00))
                .andExpect(jsonPath("$.currency_code").value("USD"))
                .andExpect(jsonPath("$.source_wallet_no").value("9688710733"))
                .andExpect(jsonPath("$.destination_wallet_no").value("7324108711"))
                .andExpect(jsonPath("$.authorized_by.id").value("2"))
                .andExpect(jsonPath("$.authorized_by.first_name").value("Peter"))
                .andExpect(jsonPath("$.authorized_by.last_name").value("John"));
    }


    @Test
    @DisplayName("transfer funds to account wallet failed due to insufficient balance")
    @Sql(scripts = {"/scripts/create_admin.sql", "/scripts/create_account.sql"}, executionPhase = BEFORE_TEST_METHOD)
    @Sql(scripts = {"/scripts/delete_data.sql"}, executionPhase = AFTER_TEST_METHOD)
    void transferFundsToAccountWalletFailedDueToInsufficientBalance() throws Exception {
        String token = getAccessToken();
        TransferRequest requestBody = buildTransferRequest(BigDecimal.valueOf(5000.00f), "7324108711", "9130704994");

        mvc.perform(post("/api/v1/transactions")
                        .header("Authorization", "Bearer " + token)
                        .content(asJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INSUFFICIENT_BALANCE"))
                .andExpect(jsonPath("$.details.error").value("Insufficient funds in the wallet to complete this transaction"));

    }

    @Test
    @DisplayName("transfer funds to account wallet failed due to wallet have different currency")
    @Sql(scripts = {"/scripts/create_admin.sql", "/scripts/create_account.sql"}, executionPhase = BEFORE_TEST_METHOD)
    @Sql(scripts = {"/scripts/delete_data.sql"}, executionPhase = AFTER_TEST_METHOD)
    void transferFundsToAccountWalletFailedDueToWalletHaveDifferentCurrency() throws Exception {
        String token = getAccessToken();
        TransferRequest requestBody = buildTransferRequest(BigDecimal.valueOf(500.00f), "7324108711", "7035476817");

        mvc.perform(post("/api/v1/transactions")
                        .header("Authorization", "Bearer " + token)
                        .content(asJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("CURRENCY_CODE_MISMATCH"))
                .andExpect(jsonPath("$.details.error").value("Currency code mismatch. Please check the accounts"));
    }

    @Test
    @DisplayName("retrieve wallet transactions successfully")
    @Sql(scripts = {"/scripts/create_admin.sql", "/scripts/create_account.sql", "/scripts/create_transaction.sql"},
            executionPhase = BEFORE_TEST_METHOD)
    @Sql(scripts = {"/scripts/delete_data.sql"}, executionPhase = AFTER_TEST_METHOD)
    void retrieveWalletTransactionsSuccessfully() throws Exception {
        mvc.perform(get("/api/v1/transactions/wallets/8")
                        .header("Authorization", "Bearer " + getAccessToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.content[0].type").value("DEBIT"))
                .andExpect(jsonPath("$.content[0].wallet_no").value("9688710733"))
                .andExpect(jsonPath("$.content[0].old_balance").value(3000.00))
                .andExpect(jsonPath("$.content[0].new_balance").value(2440.00))
                .andExpect(jsonPath("$.content[0].transaction.currency_code").value("USD"))
                .andExpect(jsonPath("$.content[0].transaction.amount").value(560.00))
                .andExpect(jsonPath("$.content[0].transaction.status").value("SUCCESSFUL"))
                .andExpect(jsonPath("$.content[0].transaction.authorized_by.id").value("2"))
                .andExpect(jsonPath("$.content[0].transaction.authorized_by.first_name").value("Peter"))
                .andExpect(jsonPath("$.content[0].transaction.authorized_by.last_name").value("John"));
    }

    @Test
    @DisplayName("retrieve wallet transactions failed due to invalid wallet id")
    void retrieveWalletTransactionsFailedDueToInvalidWalletId() throws Exception {
        mvc.perform(get("/api/v1/transactions/wallets/200")
                        .header("Authorization", "Bearer " + getAccessToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("WALLET_DOES_NOT_EXIST"))
                .andExpect(jsonPath("$.details.error").value("Wallet does not exist"));
    }

    @Test
    @DisplayName("retrieve account transactions successfully")
    @Sql(scripts = {"/scripts/create_admin.sql", "/scripts/create_account.sql", "/scripts/create_transaction.sql"},
            executionPhase = BEFORE_TEST_METHOD)
    @Sql(scripts = {"/scripts/delete_data.sql"}, executionPhase = AFTER_TEST_METHOD)
    void retrieveWalletAccountTransactionsSuccessfully() throws Exception {
        mvc.perform(get("/api/v1/transactions/accounts/3")
                        .header("Authorization", "Bearer " + getAccessToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.content[0].type").value("DEBIT"))
                .andExpect(jsonPath("$.content[0].wallet_no").value("9688710733"))
                .andExpect(jsonPath("$.content[0].old_balance").value(3000.00))
                .andExpect(jsonPath("$.content[0].new_balance").value(2440.00))
                .andExpect(jsonPath("$.content[0].transaction.currency_code").value("USD"))
                .andExpect(jsonPath("$.content[0].transaction.amount").value(560.00))
                .andExpect(jsonPath("$.content[0].transaction.status").value("SUCCESSFUL"))
                .andExpect(jsonPath("$.content[0].transaction.authorized_by.id").value("2"))
                .andExpect(jsonPath("$.content[0].transaction.authorized_by.first_name").value("Peter"))
                .andExpect(jsonPath("$.content[0].transaction.authorized_by.last_name").value("John"))
                .andExpect(jsonPath("$.content[1].type").value("CREDIT"))
                .andExpect(jsonPath("$.content[1].wallet_no").value("5334505531"))
                .andExpect(jsonPath("$.content[1].old_balance").value(2440.00))
                .andExpect(jsonPath("$.content[1].new_balance").value(3040.00))
                .andExpect(jsonPath("$.content[1].transaction.currency_code").value("EUR"))
                .andExpect(jsonPath("$.content[1].transaction.amount").value(600.00))
                .andExpect(jsonPath("$.content[1].transaction.status").value("SUCCESSFUL"))
                .andExpect(jsonPath("$.content[1].transaction.authorized_by.id").value("2"))
                .andExpect(jsonPath("$.content[1].transaction.authorized_by.first_name").value("Peter"))
                .andExpect(jsonPath("$.content[1].transaction.authorized_by.last_name").value("John"));
    }

    @Test
    @DisplayName("retrieve account transactions failed due to invalid account id")
    void retrieveAccountTransactionsFailedDueToInvalidAccountId() throws Exception {
        mvc.perform(get("/api/v1/transactions/accounts/200")
                        .header("Authorization", "Bearer " + getAccessToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("ACCOUNT_DOES_NOT_EXIST"))
                .andExpect(jsonPath("$.details.error").value("Invalid account id"));
    }

    private TransferRequest buildTransferRequest(BigDecimal amount, String sourceWalletNo, String destinationWalletNo) {
        return TransferRequest.builder()
                .amount(amount)
                .sourceWalletNo(sourceWalletNo)
                .destinationWalletNo(destinationWalletNo)
                .build();
    }
}
