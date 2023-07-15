package co.kirikiri.integration;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import java.io.UnsupportedEncodingException;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTest {

    @Value("${server.servlet.contextPath}")
    protected String API_PREFIX;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    protected ObjectMapper objectMapper = new ObjectMapper();

    protected <T> T jsonToClass(final String responseBody, final TypeReference<T> typeReference) throws JsonProcessingException, UnsupportedEncodingException {
        return objectMapper.readValue(responseBody, typeReference);
    }
}
