package com.nonso.bankapp.integration;

import com.nonso.bankapp.AbstractIntegrationTest;
import com.nonso.bankapp.dto.request.CreateAccountRequest;
import com.nonso.bankapp.dto.request.CreateWalletRequest;
import com.nonso.bankapp.dto.response.AccountResource;
import com.nonso.bankapp.dto.response.WalletResource;
import com.nonso.bankapp.service.AccountResourceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static com.nonso.bankapp.utils.TestUtil.*;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = {"/scripts/create_admin.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"/scripts/delete_data.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class AccountControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private AccountResourceService accountResourceService;

    @Test
    @DisplayName("register a customer account successfully")
    void createAccountSuccessfully() throws Exception {
        String token = getAccessToken();
        CreateAccountRequest requestBody = buildCreateAccountRequest();
        requestBody.setBankAccounts(singletonList(buildCreateWalletRequest()));

        MockHttpServletResponse result = mvc.perform(post("/api/v1/accounts")
                .header("Authorization", "Bearer " + token)
                .content(asJsonString(requestBody))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        String response = result.getContentAsString();
        AccountResource resource = getJsonObject(response, AccountResource.class);

        assertEquals(200, result.getStatus());
        assertEquals("account1@test.com", resource.getEmail());
        assertEquals("test1", resource.getFirstName());
        assertEquals("account1", resource.getLastName());
        assertNotNull(resource.getBankAccounts());
        assertEquals("USD", resource.getBankAccounts().get(0).getCurrencyCode());
        assertEquals(20000.00F, resource.getBankAccounts().get(0).getBalance().floatValue());
        assertNotNull(resource.getBankAccounts().get(0).getWalletNumber());
    }

    @Test
    @Transactional
    @DisplayName("register a customer account failed due to email is in use")
    void createAccountFailedDueToEmailIsInUse() throws Exception {
        String token = getAccessToken();
        CreateAccountRequest requestBody = buildCreateAccountRequest();
        requestBody.setBankAccounts(singletonList(buildCreateWalletRequest()));

        accountResourceService.createCustomerAccount(requestBody);

        mvc.perform(post("/api/v1/accounts")
                .header("Authorization", "Bearer " + token)
                .content(asJsonString(requestBody))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("EMAIL_ALREADY_IN_USE"));
    }

    @Test
    @DisplayName("register a customer account failed due to constraint violation")
    void createAccountFailedDueToDtoConstraintViolation() throws Exception {
        String token = getAccessToken();
        CreateAccountRequest requestBody = buildCreateAccountRequest();

        mvc.perform(post("/api/v1/accounts")
                .header("Authorization", "Bearer " + token)
                .content(asJsonString(requestBody))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("CONSTRAINT_VIOLATION"))
                .andExpect(jsonPath("$.details.bankAccounts").value("bank account detail must be added"));
    }

    @Test
    @DisplayName("add new wallet to customer account successfully")
    void addNewWalletToAccountSuccessfully() throws Exception {
        String token = getAccessToken();
        CreateAccountRequest request = buildCreateAccountRequest();
        request.setBankAccounts(singletonList(buildCreateWalletRequest()));
        AccountResource accountResource = accountResourceService.createCustomerAccount(request);

        List<CreateWalletRequest> requestBody = singletonList(
                buildCreateWalletRequest("EUR", BigDecimal.valueOf(1560d)));

        MockHttpServletResponse result = mvc.perform(post("/api/v1/accounts/" + accountResource.getId())
                .header("Authorization", "Bearer " + token)
                .content(asJsonString(requestBody))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        String response = result.getContentAsString();
        AccountResource resource = getJsonObject(response, AccountResource.class);

        assertEquals(200, result.getStatus());
        assertEquals("account1@test.com", resource.getEmail());
        assertEquals("test1", resource.getFirstName());
        assertEquals("account1", resource.getLastName());
        assertNotNull(resource.getBankAccounts());
        assertEquals(2, resource.getBankAccounts().size());
        assertEquals("USD", resource.getBankAccounts().get(0).getCurrencyCode());
        assertEquals(20000.00F, resource.getBankAccounts().get(0).getBalance().floatValue());
        assertNotNull(resource.getBankAccounts().get(0).getWalletNumber());
        assertEquals("EUR", resource.getBankAccounts().get(1).getCurrencyCode());
        assertEquals(1560.00F, resource.getBankAccounts().get(1).getBalance().floatValue());
        assertNotNull(resource.getBankAccounts().get(1).getWalletNumber());
    }

    @Test
    @DisplayName("retrieve customer account details successfully")
    void retrieveAccountDetailsSuccessfully() throws Exception {
        String token = getAccessToken();
        CreateAccountRequest request = buildCreateAccountRequest();
        request.setBankAccounts(singletonList(buildCreateWalletRequest()));
        AccountResource accountResource = accountResourceService.createCustomerAccount(request);

        MockHttpServletResponse result = mvc.perform(get("/api/v1/accounts/" + accountResource.getId())
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        String response = result.getContentAsString();
        AccountResource resource = getJsonObject(response, AccountResource.class);

        assertEquals(200, result.getStatus());
        assertEquals("account1@test.com", resource.getEmail());
        assertEquals("test1", resource.getFirstName());
        assertEquals("account1", resource.getLastName());
        assertNotNull(resource.getBankAccounts());
        assertEquals(1, resource.getBankAccounts().size());
        assertEquals("USD", resource.getBankAccounts().get(0).getCurrencyCode());
        assertEquals(20000.00F, resource.getBankAccounts().get(0).getBalance().floatValue());
    }

    @Test
    @DisplayName("retrieve customer account wallets successfully")
    void retrieveAccountWalletsSuccessfully() throws Exception {
        String token = getAccessToken();
        CreateAccountRequest request = buildCreateAccountRequest();
        request.setBankAccounts(Arrays.asList(
                buildCreateWalletRequest(), buildCreateWalletRequest("EUR", BigDecimal.valueOf(1560d))));
        AccountResource accountResource = accountResourceService.createCustomerAccount(request);

        MockHttpServletResponse result = mvc.perform(get("/api/v1/accounts/" + accountResource.getId() + "/balances")
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        String response = result.getContentAsString();
        List<WalletResource> resources = getListFromJsonObject(response, WalletResource.class);

        assertEquals(200, result.getStatus());
        assertEquals(2, resources.size());
        assertEquals("USD", resources.get(0).getCurrencyCode());
        assertEquals(20000.00F, resources.get(0).getBalance().floatValue());
        assertEquals("EUR", resources.get(1).getCurrencyCode());
        assertEquals(1560.00F, resources.get(1).getBalance().floatValue());

    }

    private CreateAccountRequest buildCreateAccountRequest() {
        return CreateAccountRequest.builder()
                .firstName("test1")
                .lastName("account1")
                .email("account1@test.com")
                .build();
    }

    private CreateWalletRequest buildCreateWalletRequest() {
        return CreateWalletRequest.builder()
                .currencyCode("USD")
                .initialDeposit(BigDecimal.valueOf(20000.0d))
                .build();
    }

    private CreateWalletRequest buildCreateWalletRequest(String currencyCode, BigDecimal amount) {
        return CreateWalletRequest.builder()
                .currencyCode(currencyCode)
                .initialDeposit(amount)
                .build();
    }
}
