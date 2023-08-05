package co.kirikiri.common.resolver;

import co.kirikiri.exception.BadRequestException;
import co.kirikiri.exception.ServerException;
import co.kirikiri.service.dto.roadmap.request.RoadmapNodeSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapSaveRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RoadmapSaveArgumentResolver implements HandlerMethodArgumentResolver {

    private final ObjectMapper objectMapper;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.getParameterType().equals(RoadmapSaveRequest.class);
    }

    @Override
    public Object resolveArgument(final MethodParameter parameter, final ModelAndViewContainer mavContainer,
                                  final NativeWebRequest nativeWebRequest, final WebDataBinderFactory binderFactory) throws MethodArgumentNotValidException {
        final HttpServletRequest request = (HttpServletRequest) nativeWebRequest.getNativeRequest();
        checkIsMultipart(request);
        final MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        final RoadmapSaveRequest roadmapSaveRequest = makeRoadmapSaveRequest(multipartRequest);
        validateRequest(parameter, nativeWebRequest, binderFactory, roadmapSaveRequest);
        return roadmapSaveRequest;
    }

    private void checkIsMultipart(final HttpServletRequest request) {
        final MultipartResolver multipartResolver = new StandardServletMultipartResolver();
        if (!multipartResolver.isMultipart(request)) {
            throw new BadRequestException("multipart/form-data 형식으로 들어와야합니다.");
        }
    }

    private RoadmapSaveRequest makeRoadmapSaveRequest(final MultipartHttpServletRequest multipartRequest) {
        final String jsonData = extractJsonData(getJsonData(multipartRequest));
        final RoadmapSaveRequest roadmapSaveRequest = bindRoadmapSaveRequest(jsonData);
        for (final RoadmapNodeSaveRequest roadmapNode : roadmapSaveRequest.roadmapNodes()) {
            final List<MultipartFile> images = multipartRequest.getFiles(roadmapNode.getTitle());
            roadmapNode.setImages(images);
        }
        return roadmapSaveRequest;
    }

    private void validateRequest(final MethodParameter parameter, final NativeWebRequest nativeWebRequest, final WebDataBinderFactory binderFactory, final RoadmapSaveRequest roadmapSaveRequest) throws MethodArgumentNotValidException {
        final WebDataBinder binder = findWebDataBinder(nativeWebRequest, binderFactory, roadmapSaveRequest);
        binder.validate();

        if (binder.getBindingResult().hasErrors()) {
            throw new MethodArgumentNotValidException(parameter, binder.getBindingResult());
        }
    }

    private WebDataBinder findWebDataBinder(final NativeWebRequest nativeWebRequest, final WebDataBinderFactory binderFactory, final RoadmapSaveRequest roadmapSaveRequest) {
        try {
            return binderFactory.createBinder(nativeWebRequest, roadmapSaveRequest, "roadmapSaveRequest");
        } catch (final Exception e) {
            throw new ServerException("Request Validation 실패");
        }
    }

    private RoadmapSaveRequest bindRoadmapSaveRequest(final String jsonData) {
        final RoadmapSaveRequest roadmapSaveRequest;
        try {
            roadmapSaveRequest = objectMapper.readValue(jsonData, RoadmapSaveRequest.class);
        } catch (final JsonProcessingException e) {
            throw new BadRequestException("바인딩 실패");
        }
        return roadmapSaveRequest;
    }

    private MultipartFile getJsonData(final MultipartHttpServletRequest multipartRequest) {
        try {
            return multipartRequest.getFile("jsonData");
        } catch (final NullPointerException exception) {
            throw new BadRequestException("로드맵 생성 시 jsonData는 필수입니다.");
        }
    }

    private String extractJsonData(final MultipartFile file) {
        try {
            return new String(file.getBytes(), StandardCharsets.UTF_8);
        } catch (final IOException e) {
            throw new ServerException("Json 데이터 바인등에 실패 하였습니다.");
        }
    }
}
