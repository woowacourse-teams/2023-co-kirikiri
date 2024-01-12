package co.kirikiri.service;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface OauthNetworkService {

    <T> ResponseEntity<T> requestToken(final Class<T> clazz, final Map<String, String> queryParams);

    <T> ResponseEntity<T> requestMemberInfo(final Class<T> clazz, final Map<String, String> headers);
}
