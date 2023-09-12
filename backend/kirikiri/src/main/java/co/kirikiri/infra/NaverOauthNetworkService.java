package co.kirikiri.infra;

import co.kirikiri.service.OauthNetworkService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
public class NaverOauthNetworkService implements OauthNetworkService {

    private static final String QUERY_PARAMETER_EQUAL = "=";
    private static final String QUERY_PARAMETER_DELIMITER = "&";

    private final RestTemplate restTemplate;
    private final String clientId;
    private final String clientSecret;
    private final String tokenUrl;
    private final String memberInfoUrl;

    public NaverOauthNetworkService(final RestTemplate restTemplate,
                                    @Value("${oauth.naver.client-id}") final String clientId,
                                    @Value("${oauth.naver.client-secret}") final String client_secret,
                                    @Value("${oauth.naver.token-url}") final String tokenUrl,
                                    @Value("${oauth.naver.member-info-url}") final String memberInfoUrl) {
        this.restTemplate = restTemplate;
        this.clientId = clientId;
        this.clientSecret = client_secret;
        this.tokenUrl = tokenUrl;
        this.memberInfoUrl = memberInfoUrl;
    }

    @Override
    public <T> ResponseEntity<T> requestToken(final Class<T> clazz, final Map<String, String> queryParams) {
        final String baseUrl = String.format(tokenUrl + "client_id=%s&client_secret=%s&", clientId, clientSecret);
        final StringBuilder stringBuilder = new StringBuilder(baseUrl);
        for (final Map.Entry<String, String> entry : queryParams.entrySet()) {
            stringBuilder.append(entry.getKey())
                    .append(QUERY_PARAMETER_EQUAL)
                    .append(entry.getValue())
                    .append(QUERY_PARAMETER_DELIMITER);
        }
        return restTemplate.getForEntity(stringBuilder.toString(), clazz);
    }

    @Override
    public <T> ResponseEntity<T> requestMemberInfo(final Class<T> clazz, final Map<String, String> headers) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        for (final Map.Entry<String, String> entry : headers.entrySet()) {
            httpHeaders.set(entry.getKey(), entry.getValue());
        }
        final HttpEntity<Object> httpEntity = new HttpEntity<>(httpHeaders);
        return restTemplate.exchange(memberInfoUrl, HttpMethod.GET, httpEntity, clazz);
    }
}
