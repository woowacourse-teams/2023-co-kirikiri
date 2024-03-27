package co.kirikiri.roadmap.resolver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import co.kirikiri.common.exception.BadRequestException;
import co.kirikiri.member.service.dto.request.MemberJoinRequest;
import co.kirikiri.roadmap.service.dto.request.RoadmapSaveRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

@ExtendWith(MockitoExtension.class)
class RoadmapSaveArgumentResolverImplTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private MethodParameter parameter;

    @Mock
    private ModelAndViewContainer mavContainer;

    @Mock
    private NativeWebRequest nativeWebRequest;

    @Mock
    private WebDataBinderFactory binderFactory;

    @InjectMocks
    private RoadmapSaveArgumentResolverImpl resolver;

    @Test
    void 정상적으로_핸들러의_파라미터가_객체를_타도록_한다() {
        //given
        Mockito.<Class<?>>when(parameter.getParameterType())
                .thenReturn(RoadmapSaveRequest.class);

        //when
        final boolean result = resolver.supportsParameter(parameter);

        //then
        assertThat(result).isTrue();
    }

    @Test
    void 핸들러의_파라미터가_객체를_타지_않는다() {
        //given
        Mockito.<Class<?>>when(parameter.getParameterType())
                .thenReturn(MemberJoinRequest.class);

        //when
        final boolean result = resolver.supportsParameter(parameter);

        //then
        assertThat(result).isFalse();
    }

    @Test
    void 멀티파트폼_요청이_아닐_경우_예외를_던진() {
        //given
        when(nativeWebRequest.getNativeRequest())
                .thenReturn(new MockHttpServletRequest());

        //when
        //then
        assertThatThrownBy(() -> resolver.resolveArgument(parameter, mavContainer, nativeWebRequest, binderFactory))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void 요청에_jsonData가_포함되어있지_않을_경우_예외를_던진다() {
        //given
        final MockMultipartHttpServletRequest multipartRequest = new MockMultipartHttpServletRequest();

        when(nativeWebRequest.getNativeRequest()).thenReturn(multipartRequest);

        //when
        //then
        assertThatThrownBy(() -> resolver.resolveArgument(parameter, mavContainer, nativeWebRequest, binderFactory))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void jsonData가_RoadmapSaveRequest_형식이_아닐_경우_예외를_던진다() throws JsonProcessingException {
        //given
        final MockMultipartHttpServletRequest multipartRequest = new MockMultipartHttpServletRequest();
        final String jsonData = "{\"key\":\"value\"}";
        multipartRequest.addParameter("jsonData", jsonData);
        when(nativeWebRequest.getNativeRequest()).thenReturn(multipartRequest);
        when(objectMapper.readValue(jsonData, RoadmapSaveRequest.class))
                .thenThrow(new JsonProcessingException("message") {
                });

        //when
        //then
        assertThatThrownBy(() -> resolver.resolveArgument(parameter, mavContainer, nativeWebRequest, binderFactory))
                .isInstanceOf(BadRequestException.class);
    }
}
