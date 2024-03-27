package co.kirikiri.auth.infra;

import co.kirikiri.auth.service.OauthNetworkService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NaverOauthNetworkService implements OauthNetworkService {

    private static final String QUERY_PARAMETER_EQUAL = "=";
    private static final String QUERY_PARAMETER_DELIMITER = "&";
    private static final String CLIENT_ID_PROPERTY = "oauth.naver.client-id";
    private static final String CLIENT_SECRET_PROPERTY = "oauth.naver.client-secret";
    private static final String TOKEN_URL_PROPERTY = "oauth.naver.token-url";
    private static final String MEMBER_INFO_URL_PROPERTY = "oauth.naver.member-info-url";

    private final RestTemplate restTemplate;
    private final Environment environment;

    @Override
    public <T> ResponseEntity<T> requestToken(final Class<T> clazz, final Map<String, String> queryParams) {
        final String baseUrl = String.format(findProperty(TOKEN_URL_PROPERTY) + "client_id=%s&client_secret=%s&",
                findProperty(CLIENT_ID_PROPERTY), findProperty(CLIENT_SECRET_PROPERTY));
        final StringBuilder stringBuilder = new StringBuilder(baseUrl);
        for (final Map.Entry<String, String> entry : queryParams.entrySet()) {
            stringBuilder.append(entry.getKey())
                    .append(QUERY_PARAMETER_EQUAL)
                    .append(entry.getValue())
                    .append(QUERY_PARAMETER_DELIMITER);
        }
        final String url = stringBuilder.substring(0, stringBuilder.lastIndexOf(QUERY_PARAMETER_DELIMITER));
        return restTemplate.getForEntity(url, clazz);
    }

    @Override
    public <T> ResponseEntity<T> requestMemberInfo(final Class<T> clazz, final Map<String, String> headers) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        for (final Map.Entry<String, String> entry : headers.entrySet()) {
            httpHeaders.set(entry.getKey(), entry.getValue());
        }
        final HttpEntity<Object> httpEntity = new HttpEntity<>(httpHeaders);
        return restTemplate.exchange(findProperty(MEMBER_INFO_URL_PROPERTY), HttpMethod.GET, httpEntity, clazz);
    }

    private String findProperty(final String property) {
        return environment.getProperty(property);
    }
}
