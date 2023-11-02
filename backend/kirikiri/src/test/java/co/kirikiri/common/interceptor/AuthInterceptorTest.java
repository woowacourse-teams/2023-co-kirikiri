package co.kirikiri.common.interceptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import co.kirikiri.service.auth.AuthService;
import co.kirikiri.service.exception.AuthenticationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;

@ExtendWith(MockitoExtension.class)
class AuthInterceptorTest {

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @Mock
    private AuthService authService;

    @Mock
    private HandlerMethod handlerMethod;

    @InjectMocks
    private AuthInterceptor authInterceptor;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    void 인증이_필요한_요청에서_정상적으로_유효한_토큰을_검사한다() {
        //given
        when(handlerMethod.hasMethodAnnotation(any()))
                .thenReturn(true);
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer test-token");
        when(authService.isCertified(anyString())).thenReturn(true);

        //when
        final boolean result = authInterceptor.preHandle(request, response, handlerMethod);

        //then
        assertThat(result).isTrue();
    }

    @Test
    void 인증이_필요한_요청에서_Authorization_헤더가_없을때_예외를_던진다() {
        //given
        when(handlerMethod.hasMethodAnnotation(any()))
                .thenReturn(true);

        //when
        //then
        assertThatThrownBy(() -> authInterceptor.preHandle(request, response, handlerMethod))
                .isInstanceOf(AuthenticationException.class);
    }

    @Test
    void 인증이_필요한_요청에서_Authorization_헤더가_Bearer가_없을때_예외를_던진다() {
        //given
        when(handlerMethod.hasMethodAnnotation(any()))
                .thenReturn(true);
        request.addHeader(HttpHeaders.AUTHORIZATION, "test-token");

        //when
        assertThatThrownBy(() -> authInterceptor.preHandle(request, response, handlerMethod))
                .isInstanceOf(AuthenticationException.class);
    }

    @Test
    void 인증이_필요한_요청에서_토큰이_유효하지_않을때_예외를_던진다() {
        //given
        when(handlerMethod.hasMethodAnnotation(any()))
                .thenReturn(true);
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer test-token");
        when(authService.isCertified(anyString())).thenReturn(false);

        //when
        assertThatThrownBy(() -> authInterceptor.preHandle(request, response, handlerMethod))
                .isInstanceOf(AuthenticationException.class);
    }
}
