package co.kirikiri.integration.fixture;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

public class CommonFixture {

    public static final String BEARER_TOKEN_FORMAT = "Bearer %s";
    public static final String AUTHORIZATION = "Authorization";
    public static final String LOCATION = "Location";
    public static String API_PREFIX = "/api";

    public static void 응답_상태_코드_검증(final ExtractableResponse<Response> 응답, final HttpStatus http_상태) {
        assertThat(응답.statusCode()).isEqualTo(http_상태.value());
    }

    public static Long 아이디를_반환한다(final ExtractableResponse<Response> 응답) {
        return Long.parseLong(응답.header(HttpHeaders.LOCATION).split("/")[3]);
    }
}
