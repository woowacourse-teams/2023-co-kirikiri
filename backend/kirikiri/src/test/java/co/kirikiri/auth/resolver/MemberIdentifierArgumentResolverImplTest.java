package co.kirikiri.auth.resolver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import co.kirikiri.auth.service.AuthService;
import co.kirikiri.common.exception.AuthenticationException;
import co.kirikiri.common.exception.ServerException;
import co.kirikiri.common.interceptor.Authenticated;
import co.kirikiri.common.resolver.MemberIdentifier;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

@ExtendWith(MockitoExtension.class)
class MemberIdentifierArgumentResolverImplTest {

    private static final String BEARER = "Bearer ";

    @Mock
    private AuthService authService;

    @Mock
    private MethodParameter parameter;

    @Mock
    private ModelAndViewContainer mavContainer;

    @Mock
    private NativeWebRequest webRequest;

    @Mock
    private WebDataBinderFactory binderFactory;

    @InjectMocks
    private MemberIdentifierArgumentResolverImpl memberIdentifierArgumentResolver;

    @Test
    void 정상적으로_String_타입이고_MemberIdentifier이_붙은_인자인_경우() {
        //given
        Mockito.<Class<?>>when(parameter.getParameterType())
                .thenReturn(String.class);
        when(parameter.hasParameterAnnotation(MemberIdentifier.class))
                .thenReturn(true);
        when(parameter.hasMethodAnnotation(Authenticated.class))
                .thenReturn(true);

        //when
        final boolean result = memberIdentifierArgumentResolver.supportsParameter(parameter);

        //then
        assertThat(result).isTrue();
    }

    @Test
    void String_타입이_붙지_않은_경우() {
        //given
        Mockito.<Class<?>>when(parameter.getParameterType())
                .thenReturn(List.class);
        when(parameter.hasMethodAnnotation(Authenticated.class))
                .thenReturn(true);

        //when
        final boolean result = memberIdentifierArgumentResolver.supportsParameter(parameter);

        //then
        assertThat(result).isFalse();
    }

    @Test
    void String_타입이고_MemberIdentifier이_붙지_않은_인자인_경우() {
        //given
        Mockito.<Class<?>>when(parameter.getParameterType())
                .thenReturn(String.class);
        when(parameter.hasParameterAnnotation(MemberIdentifier.class))
                .thenReturn(false);
        when(parameter.hasMethodAnnotation(Authenticated.class))
                .thenReturn(true);

        //when
        final boolean result = memberIdentifierArgumentResolver.supportsParameter(parameter);

        //then
        assertThat(result).isFalse();
    }

    @Test
    void String_타입이고_Authenticated가_붙지_않은_인자인_경우_예외가_발생한다() {
        //given
        when(parameter.hasMethodAnnotation(Authenticated.class))
                .thenReturn(false);

        //when
        //then
        assertThatThrownBy(() -> memberIdentifierArgumentResolver.supportsParameter(parameter))
                .isInstanceOf(ServerException.class)
                .hasMessageContaining("MemberIdentifier는 인증된 사용자만 사용 가능합니다. (@Authenticated)");
    }

    @Test
    void 정상적으로_토큰이_들어온_경우_토큰을_인증한다() {
        // given
        final String expectedIdentifier = "test";
        final String token = "testToken";

        when(webRequest.getHeader(HttpHeaders.AUTHORIZATION))
                .thenReturn(BEARER + token);
        when(authService.findIdentifierByToken(anyString()))
                .thenReturn(expectedIdentifier);

        // when
        final String actualIdentifier = memberIdentifierArgumentResolver.resolveArgument(parameter, mavContainer,
                webRequest, binderFactory);

        // then
        assertThat(actualIdentifier).isEqualTo(expectedIdentifier);
    }

    @Test
    void AUTHORIZATION_HEADER에_BEARER이_안붙은_경우_예외를_터트린다() {
        // given
        final String token = "testToken";

        when(webRequest.getHeader(HttpHeaders.AUTHORIZATION))
                .thenReturn(token);

        // when
        // then
        assertThatThrownBy(() -> memberIdentifierArgumentResolver.resolveArgument(parameter, mavContainer, webRequest,
                binderFactory))
                .isInstanceOf(AuthenticationException.class);

    }

    @Test
    void AUTHORIZATION_HEADER가_비어있을_경우_예외를_터트린다() {
        // given
        when(webRequest.getHeader(HttpHeaders.AUTHORIZATION))
                .thenReturn(null);

        // when
        // then
        assertThatThrownBy(() -> memberIdentifierArgumentResolver.resolveArgument(parameter, mavContainer, webRequest,
                binderFactory))
                .isInstanceOf(AuthenticationException.class);
    }

    @Test
    void 적절하지_않은_토큰이_들어올_경우_예외를_터트린다() {
        // given
        final String token = "testToken";

        when(webRequest.getHeader(HttpHeaders.AUTHORIZATION))
                .thenReturn(BEARER + token);
        when(authService.findIdentifierByToken(anyString()))
                .thenThrow(new AuthenticationException("토큰이 유효하지 않습니다."));

        // when
        // then
        assertThatThrownBy(() -> memberIdentifierArgumentResolver.resolveArgument(parameter, mavContainer, webRequest,
                binderFactory))
                .isInstanceOf(AuthenticationException.class);
    }
}
