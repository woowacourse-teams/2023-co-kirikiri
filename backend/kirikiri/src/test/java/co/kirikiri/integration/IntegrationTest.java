package co.kirikiri.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestConstructor(autowireMode = AutowireMode.ALL)
public class IntegrationTest {

    @LocalServerPort
    protected int port;

    @Autowired
    protected ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }
}
