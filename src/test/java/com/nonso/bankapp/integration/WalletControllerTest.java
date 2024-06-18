package com.nonso.bankapp.integration;

import com.nonso.bankapp.AbstractIntegrationTest;
import com.nonso.bankapp.dto.response.WalletResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static com.nonso.bankapp.utils.TestUtil.getJsonObject;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = {"/scripts/create_admin.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"/scripts/create_account.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"/scripts/delete_data.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class WalletControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("retrieve wallet details successfully")
    void retrieveWalletDetailsSuccessfully() throws Exception {
        MockHttpServletResponse result = mvc.perform(get("/api/v1/wallets/7")
                .header("Authorization", "Bearer " + getAccessToken())
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        String response = result.getContentAsString();
        WalletResource resource = getJsonObject(response, WalletResource.class);

        assertEquals(200, result.getStatus());
        assertEquals("NGN", resource.getCurrencyCode());
        assertEquals(3000.00, resource.getBalance().floatValue());
        assertEquals("9433010941", resource.getWalletNumber());
    }

    @Test
    @DisplayName("retrieve account wallet failed due to invalid wallet id")
    void retrieveWalletFailedDueToInvalidWalletId() throws Exception {
        mvc.perform(get("/api/v1/wallets/100")
                .header("Authorization", "Bearer " + getAccessToken())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("WALLET_DOES_NOT_EXIST"))
                .andExpect(jsonPath("$.details.error").value("Wallet does not exist"));
    }

    @Test
    @DisplayName("retrieve account wallet details successfully")
    void retrieveAccountWalletDetailsSuccessfully() throws Exception {
        MockHttpServletResponse result = mvc.perform(get("/api/v1/wallets/7/accounts/3")
                .header("Authorization", "Bearer " + getAccessToken())
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        String response = result.getContentAsString();
        WalletResource resource = getJsonObject(response, WalletResource.class);

        assertEquals(200, result.getStatus());
        assertEquals("NGN", resource.getCurrencyCode());
        assertEquals(3000.00, resource.getBalance().floatValue());
        assertEquals("9433010941", resource.getWalletNumber());
    }

    @Test
    @DisplayName("retrieve account wallet failed due to invalid wallet id")
    void retrieveAccountWalletFailedDueToInvalidWalletId() throws Exception {
        mvc.perform(get("/api/v1/wallets/100/accouunts/3")
                .header("Authorization", "Bearer " + getAccessToken())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("WALLET_DOES_NOT_EXIST"))
                .andExpect(jsonPath("$.details.error").value("Wallet does not exist"));
    }
}
