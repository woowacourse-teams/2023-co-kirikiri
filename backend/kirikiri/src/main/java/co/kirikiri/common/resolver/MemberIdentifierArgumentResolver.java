package co.kirikiri.common.resolver;

import co.kirikiri.common.interceptor.Authenticated;
import co.kirikiri.service.auth.AuthService;
import co.kirikiri.service.exception.AuthenticationException;
import co.kirikiri.service.exception.ServerException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class MemberIdentifierArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String BEARER = "Bearer ";

    private final AuthService authService;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        if (!parameter.hasMethodAnnotation(Authenticated.class)) {
            throw new ServerException("MemberIdentifier는 인증된 사용자만 사용 가능합니다. (@Authenticated)");
        }
        return parameter.getParameterType().equals(String.class)
                && parameter.hasParameterAnnotation(MemberIdentifier.class);
    }

    @Override
    public String resolveArgument(final MethodParameter parameter, final ModelAndViewContainer mavContainer,
                                  final NativeWebRequest webRequest, final WebDataBinderFactory binderFactory) {
        final String authorizationHeader = webRequest.getHeader(HttpHeaders.AUTHORIZATION);
        checkHeader(authorizationHeader);
        final String token = authorizationHeader.substring(BEARER.length());
        return authService.findIdentifierByToken(token);
    }

    private void checkHeader(final String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER)) {
            throw new AuthenticationException("인증 헤더가 적절하지 않습니다.");
        }
    }
}
