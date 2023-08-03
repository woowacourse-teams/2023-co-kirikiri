package co.kirikiri.integration.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestConstructor(autowireMode = AutowireMode.ALL)
public class IntegrationTest {

    protected final String AUTHORIZATION = "Authorization";
    protected final String LOCATION = "Location";
    protected final String BEARER_TOKEN_FORMAT = "Bearer %s";

    @Value("${server.servlet.contextPath}")
    protected String API_PREFIX;

    @LocalServerPort
    private int port;

    @Autowired
    protected ObjectMapper objectMapper;

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
