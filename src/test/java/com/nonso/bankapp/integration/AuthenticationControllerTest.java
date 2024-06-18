package com.nonso.bankapp.integration;

import com.nonso.bankapp.AbstractIntegrationTest;
import com.nonso.bankapp.dto.request.AdminRegistrationRequest;
import com.nonso.bankapp.dto.request.AuthenticationRequest;
import com.nonso.bankapp.dto.response.AuthenticationResponse;
import com.nonso.bankapp.dto.response.RegistrationResponse;
import com.nonso.bankapp.exception.BankAppException;
import com.nonso.bankapp.service.AuthenticationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static com.nonso.bankapp.utils.TestUtil.asJsonString;
import static com.nonso.bankapp.utils.TestUtil.getJsonObject;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = {"/scripts/delete_data.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class AuthenticationControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("register a new admin successfully")
    void registerAdmin() throws Exception {
        AdminRegistrationRequest request = buildAdminRegistrationRequest();

        MockHttpServletResponse result = mvc.perform(post("/api/v1/auth/register")
                .content(asJsonString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        String response = result.getContentAsString();
        RegistrationResponse registrationResponse = getJsonObject(response, RegistrationResponse.class);

        assertNotNull(registrationResponse.getMessage());
        assertEquals("Admin account created successfully", registrationResponse.getMessage());
        assertEquals(200, result.getStatus());

    }

    @Test
    @DisplayName("failed admin registration - email already in use")
    void registerAdminFailedDueToEmailAlreadyInUse() throws Exception {
        AdminRegistrationRequest requestBody = buildAdminRegistrationRequest();

        mvc.perform(post("/api/v1/auth/register")
                .content(asJsonString(requestBody))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        BankAppException exception = assertThrows(
                BankAppException.class, () -> authenticationService.register(requestBody)
        );

        assertNotNull(exception);
        assertEquals("An error occurred while creating admin account, please contact support",
                exception.getMessage());
        assertEquals(500, exception.getStatus().value());
    }

    @Test
    @DisplayName("register admin failure from constraint violation")
    void registerAdminFailedDueToDtoConstraintViolation() throws Exception {
        AdminRegistrationRequest requestBody = new AdminRegistrationRequest();

        mvc.perform(post("/api/v1/auth/register")
                .content(asJsonString(requestBody))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("CONSTRAINT_VIOLATION"))
                .andExpect(jsonPath("$.details.email").value("email must not be blank"))
                .andExpect(jsonPath("$.details.password").value("password must not be blank"))
                .andExpect(jsonPath("$.details.firstName").value("first_name must not be blank"))
                .andExpect(jsonPath("$.details.lastName").value("last_name must not be blank"));
    }

    @Test
    @DisplayName("Admin login successful")
    void authenticateAdmin() throws Exception {
        authenticationService.register(buildAdminRegistrationRequest());

        AuthenticationRequest requestBody = buildAdminLoginRequest();
        MockHttpServletResponse result = mvc.perform(post("/api/v1/auth/login")
                .content(asJsonString(requestBody))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        String response = result.getContentAsString();
        AuthenticationResponse authenticationResponse = getJsonObject(response, AuthenticationResponse.class);

        assertNotNull(authenticationResponse.getAccessToken());
        assertEquals(200, result.getStatus());
    }

    @Test
    @DisplayName("unsuccessful admin authentication admin due to invalid credentials")
    void unsuccessfulAdminAuthenticationDueToInvalidCredentials() throws Exception {
        AuthenticationRequest requestBody = buildAdminLoginRequest();
        mvc.perform(post("/api/v1/auth/login")
                .content(asJsonString(requestBody))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.details.errorCode").value("Invalid email or/and password"));
    }

    AdminRegistrationRequest buildAdminRegistrationRequest() {
        return AdminRegistrationRequest.builder()
                .firstName("test1")
                .lastName("user1")
                .email("user1@test.com")
                .password("123456")
                .build();
    }

    AuthenticationRequest buildAdminLoginRequest() {
        return AuthenticationRequest.builder()
                .email("user1@test.com")
                .password("123456")
                .build();

    }
}
