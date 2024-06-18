package com.nonso.bankapp;

import com.nonso.bankapp.dto.request.AuthenticationRequest;
import com.nonso.bankapp.dto.response.AuthenticationResponse;
import com.nonso.bankapp.testcontainer.BankAppMySQLDBContainer;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.nonso.bankapp.utils.TestUtil.asJsonString;
import static com.nonso.bankapp.utils.TestUtil.getJsonObject;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Testcontainers
@ActiveProfiles({"test"})
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Container
    protected static final BankAppMySQLDBContainer MYSQL_CONTAINER = BankAppMySQLDBContainer.getInstance();

    public String getAccessToken() throws Exception {
        AuthenticationRequest requestBody = new AuthenticationRequest("user1@test.com", "123456");
        MockHttpServletResponse response = mockMvc.perform(post("/api/v1/auth/login")
                .content(asJsonString(requestBody))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        return getJsonObject(response.getContentAsString(), AuthenticationResponse.class).getAccessToken();
    }
}
