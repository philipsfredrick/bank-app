package com.nonso.bankapp;

import com.nonso.bankapp.testcontainer.BankAppMySQLDBContainer;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ActiveProfiles({"test"})
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Container
    protected static final BankAppMySQLDBContainer MYSQLDB_CONTAINER = BankAppMySQLDBContainer.getInstance();

    public String getAccessToken() throws Exception {
        return null;
    }
}
