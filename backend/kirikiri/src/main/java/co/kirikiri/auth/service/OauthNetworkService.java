package co.kirikiri.auth.service;

import java.util.Map;
import org.springframework.http.ResponseEntity;

public interface OauthNetworkService {

    <T> ResponseEntity<T> requestToken(final Class<T> clazz, final Map<String, String> queryParams);

    <T> ResponseEntity<T> requestMemberInfo(final Class<T> clazz, final Map<String, String> headers);
}
