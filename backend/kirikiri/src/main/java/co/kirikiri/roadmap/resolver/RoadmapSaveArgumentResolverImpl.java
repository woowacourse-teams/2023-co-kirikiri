package co.kirikiri.roadmap.resolver;

import co.kirikiri.roadmap.service.dto.request.RoadmapNodeSaveRequest;
import co.kirikiri.roadmap.service.dto.request.RoadmapSaveRequest;
import co.kirikiri.service.exception.BadRequestException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RoadmapSaveArgumentResolverImpl implements RoadmapSaveArgumentResolver {


    private final ObjectMapper objectMapper;
    private final Validator validator;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.getParameterType().equals(RoadmapSaveRequest.class);
    }

    @Override
    public Object resolveArgument(final MethodParameter parameter, final ModelAndViewContainer mavContainer,
                                  final NativeWebRequest nativeWebRequest, final WebDataBinderFactory binderFactory)
            throws MethodArgumentNotValidException {
        final HttpServletRequest request = (HttpServletRequest) nativeWebRequest.getNativeRequest();
        checkMultipart(request);
        final MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        final RoadmapSaveRequest roadmapSaveRequestNotIncludeImage = makeRoadmapSaveRequestNotIncludeImage(
                multipartRequest);
        validateRequest(parameter, roadmapSaveRequestNotIncludeImage);
        return makeRoadmapSaveRequestIncludeImage(roadmapSaveRequestNotIncludeImage, multipartRequest);
    }

    private void checkMultipart(final HttpServletRequest request) {
        final MultipartResolver multipartResolver = new StandardServletMultipartResolver();
        if (!multipartResolver.isMultipart(request)) {
            throw new BadRequestException("multipart/form-data 형식으로 들어와야합니다.");
        }
    }

    private RoadmapSaveRequest makeRoadmapSaveRequestNotIncludeImage(
            final MultipartHttpServletRequest multipartRequest) {
        final String jsonData = getJsonData(multipartRequest);
        return bindRoadmapSaveRequest(jsonData);
    }

    private void validateRequest(final MethodParameter parameter, final RoadmapSaveRequest roadmapSaveRequest)
            throws MethodArgumentNotValidException {
        final DataBinder binder = new DataBinder(roadmapSaveRequest);
        binder.setValidator(validator);
        binder.validate();

        if (binder.getBindingResult().hasErrors()) {
            throw new MethodArgumentNotValidException(parameter, binder.getBindingResult());
        }
    }

    private RoadmapSaveRequest makeRoadmapSaveRequestIncludeImage(final RoadmapSaveRequest roadmapSaveRequest,
                                                                  final MultipartHttpServletRequest multipartRequest) {
        for (final RoadmapNodeSaveRequest roadmapNode : roadmapSaveRequest.roadmapNodes()) {
            final List<MultipartFile> images = multipartRequest.getFiles(roadmapNode.getTitle());
            roadmapNode.setImages(images);
        }
        return roadmapSaveRequest;
    }

    private String getJsonData(final MultipartHttpServletRequest multipartRequest) {
        final String jsonData = multipartRequest.getParameter("jsonData");
        if (jsonData == null) {
            throw new BadRequestException("로드맵 생성 시 jsonData는 필수입니다.");
        }
        return multipartRequest.getParameter("jsonData");
    }

    private RoadmapSaveRequest bindRoadmapSaveRequest(final String jsonData) {
        try {
            return objectMapper.readValue(jsonData, RoadmapSaveRequest.class);
        } catch (final JsonProcessingException exception) {
            throw new BadRequestException("로드맵 생성 요청 형식이 틀렸습니다.");
        }
    }
}
