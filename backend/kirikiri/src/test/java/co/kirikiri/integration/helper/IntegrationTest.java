package co.kirikiri.integration.helper;

import co.kirikiri.persistence.helper.RedisTestContainer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestConstructor(autowireMode = AutowireMode.ALL)
@Import({TestConfig.class, RedisTestContainer.class})
public class IntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected TestTransactionService testTransactionService;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @AfterEach
    void tearDown() {
        databaseCleaner.execute();
    }

    protected <T> T jsonToClass(final String responseBody, final TypeReference<T> typeReference)
            throws JsonProcessingException {
        return objectMapper.readValue(responseBody, typeReference);
    }
}
