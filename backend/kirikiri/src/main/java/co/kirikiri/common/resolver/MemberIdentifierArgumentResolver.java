package co.kirikiri.common.resolver;

import co.kirikiri.exception.AuthenticationException;
import co.kirikiri.service.AuthService;
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

    private static final String TYPE = "Bearer ";

    private final AuthService authService;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.getParameterType().equals(String.class)
                && parameter.hasParameterAnnotation(MemberIdentifier.class);
    }

    @Override
    public String resolveArgument(final MethodParameter parameter, final ModelAndViewContainer mavContainer, final NativeWebRequest webRequest, final WebDataBinderFactory binderFactory) throws Exception {
        final String authorizationHeader = webRequest.getHeader((HttpHeaders.AUTHORIZATION));
        if (authorizationHeader == null || !authorizationHeader.startsWith(TYPE)) {
            throw new AuthenticationException("Authorization Header Is Empty.");
        }
        return authService.findMemberIdentifier(authorizationHeader);
    }
}
