package co.kirikiri.checkfeed.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.kirikiri.checkfeed.service.GoalRoomCheckFeedService;
import co.kirikiri.checkfeed.service.dto.response.CheckFeedMemberResponse;
import co.kirikiri.checkfeed.service.dto.response.CheckFeedResponse;
import co.kirikiri.checkfeed.service.dto.response.GoalRoomCheckFeedResponse;
import co.kirikiri.common.exception.BadRequestException;
import co.kirikiri.common.exception.NotFoundException;
import co.kirikiri.common.helper.ControllerTestHelper;
import co.kirikiri.common.service.dto.ErrorResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(GoalRoomCheckFeedController.class)
class GoalRoomCheckFeedApiTest extends ControllerTestHelper {

    @MockBean
    private GoalRoomCheckFeedService goalRoomCheckFeedService;

    @Test
    void 인증_피드_등록_요청을_보낸다() throws Exception {
        //given
        final String imageName = "image";
        final String originalImageName = "originalImageName.jpeg";
        final String contentType = "image/jpeg";
        final String image = "테스트 이미지";
        final String description = "이미지 설명";
        final String filePath = "path/to/directories/" + contentType;
        final MockMultipartFile imageFile = new MockMultipartFile(imageName, originalImageName,
                contentType, image.getBytes());

        given(goalRoomCheckFeedService.createCheckFeed(anyString(), anyLong(), any()))
                .willReturn(filePath);

        //expect
        mockMvc.perform(
                        RestDocumentationRequestBuilders
                                .multipart(API_PREFIX + "/goal-rooms/{goalRoomId}/checkFeeds", 1L)
                                .file(imageFile)
                                .file("text", description.getBytes())
                                .header("Authorization", "Bearer accessToken")
                                .contextPath(API_PREFIX)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", filePath))
                .andDo(
                        documentationResultHandler.document(
                                requestHeaders(
                                        headerWithName("Authorization").description("액세스 토큰")
                                ),
                                pathParameters(
                                        parameterWithName("goalRoomId").description("골룸 아이디")
                                ),
                                requestParts(
                                        partWithName("image").description("업로드한 이미지"),
                                        partWithName("text").description("인증 피드 본문")
                                ),
                                responseHeaders(
                                        headerWithName("Location").description("저장된 이미지 경로")
                                )));
    }

    @Test
    void 인증_피드_등록시_노드_기간에_해당하지_않으면_예외가_발생한다() throws Exception {
        // given
        final String imageName = "image";
        final String originalImageName = "originalImageName.jpeg";
        final String contentType = "image/jpeg";
        final String image = "테스트 이미지";
        final String description = "이미지 설명";
        final MockMultipartFile imageFile = new MockMultipartFile(imageName, originalImageName,
                contentType, image.getBytes());

        doThrow(new BadRequestException("인증 피드는 노드 기간 내에만 작성할 수 있습니다."))
                .when(goalRoomCheckFeedService)
                .createCheckFeed(anyString(), anyLong(), any());

        //when
        mockMvc.perform(
                        RestDocumentationRequestBuilders
                                .multipart(API_PREFIX + "/goal-rooms/{goalRoomId}/checkFeeds", 1L)
                                .file(imageFile)
                                .param("description", description)
                                .header("Authorization", "Bearer accessToken")
                                .contextPath(API_PREFIX)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("인증 피드는 노드 기간 내에만 작성할 수 있습니다."))
                .andDo(
                        documentationResultHandler.document(
                                requestHeaders(
                                        headerWithName("Authorization").description("액세스 토큰")
                                ),
                                pathParameters(
                                        parameterWithName("goalRoomId").description("골룸 아이디")
                                ),
                                requestParts(
                                        partWithName("image").description("업로드한 이미지")
                                ),
                                responseFields(
                                        fieldWithPath("message").description("예외 메세지")
                                )));
    }

    @Test
    void 인증_피드_등록_요청시_멤버가_존재하지_않을_경우_예외를_반환한다() throws Exception {
        //given
        final String imageName = "image";
        final String originalImageName = "originalImageName.jpeg";
        final String contentType = "image/jpeg";
        final String image = "테스트 이미지";
        final String description = "이미지 설명";
        final MockMultipartFile imageFile = new MockMultipartFile(imageName, originalImageName,
                contentType, image.getBytes());

        doThrow(new NotFoundException("존재하지 않는 회원입니다."))
                .when(goalRoomCheckFeedService)
                .createCheckFeed(anyString(), anyLong(), any());

        //when
        mockMvc.perform(
                        RestDocumentationRequestBuilders
                                .multipart(API_PREFIX + "/goal-rooms/{goalRoomId}/checkFeeds", 1L)
                                .file(imageFile)
                                .file("text", description.getBytes())
                                .header("Authorization", "Bearer accessToken")
                                .contextPath(API_PREFIX)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("존재하지 않는 회원입니다."))
                .andDo(
                        documentationResultHandler.document(
                                requestHeaders(
                                        headerWithName("Authorization").description("액세스 토큰")
                                ),
                                pathParameters(
                                        parameterWithName("goalRoomId").description("골룸 아이디")
                                ),
                                requestParts(
                                        partWithName("image").description("업로드한 이미지"),
                                        partWithName("text").description("인증 피드 본문")
                                ),
                                responseFields(
                                        fieldWithPath("message").description("예외 메세지")
                                )));
    }

    @Test
    void 인증_피드_등록_요청시_로드맵이_존재하지_않을_경우_예외를_반환한다() throws Exception {
        //given
        final String imageName = "image";
        final String originalImageName = "originalImageName.jpeg";
        final String contentType = "image/jpeg";
        final String image = "테스트 이미지";
        final String description = "이미지 설명";
        final MockMultipartFile imageFile = new MockMultipartFile(imageName, originalImageName,
                contentType, image.getBytes());

        doThrow(new NotFoundException("골룸 정보가 존재하지 않습니다. goalRoomId = 1L"))
                .when(goalRoomCheckFeedService)
                .createCheckFeed(anyString(), anyLong(), any());

        //when
        mockMvc.perform(
                        RestDocumentationRequestBuilders
                                .multipart(API_PREFIX + "/goal-rooms/{goalRoomId}/checkFeeds", 1L)
                                .file(imageFile)
                                .file("text", description.getBytes())
                                .header("Authorization", "Bearer accessToken")
                                .contextPath(API_PREFIX)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("골룸 정보가 존재하지 않습니다. goalRoomId = 1L"))
                .andDo(
                        documentationResultHandler.document(
                                requestHeaders(
                                        headerWithName("Authorization").description("액세스 토큰")
                                ),
                                pathParameters(
                                        parameterWithName("goalRoomId").description("골룸 아이디")
                                ),
                                requestParts(
                                        partWithName("image").description("업로드한 이미지"),
                                        partWithName("text").description("인증 피드 본문")
                                ),
                                responseFields(
                                        fieldWithPath("message").description("예외 메세지")
                                )));
    }

    @Test
    void 골룸의_인증피드를_전체_조회한다() throws Exception {
        // given
        final GoalRoomCheckFeedResponse goalRoomCheckFeedResponse1 = new GoalRoomCheckFeedResponse(
                new CheckFeedMemberResponse(1L, "name1", "imageUrl"),
                new CheckFeedResponse(1L, "imageUrl", "image description1", LocalDate.now()));
        final GoalRoomCheckFeedResponse goalRoomCheckFeedResponse2 = new GoalRoomCheckFeedResponse(
                new CheckFeedMemberResponse(2L, "name2", "imageUrl"),
                new CheckFeedResponse(2L, "imageUrl", "image description2", LocalDate.now()));

        final List<GoalRoomCheckFeedResponse> expected = List.of(goalRoomCheckFeedResponse2,
                goalRoomCheckFeedResponse1);

        when(goalRoomCheckFeedService.findGoalRoomCheckFeeds(any(), any()))
                .thenReturn(expected);

        // when
        final String response = mockMvc.perform(
                        get(API_PREFIX + "/goal-rooms/{goalRoomId}/checkFeeds", 1L)
                                .header(AUTHORIZATION, String.format(BEARER_TOKEN_FORMAT, "test-token"))
                                .contextPath(API_PREFIX))
                .andExpect(status().isOk())
                .andDo(
                        documentationResultHandler.document(
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                                ),
                                pathParameters(
                                        parameterWithName("goalRoomId").description("골룸 아이디")
                                ),
                                responseFields(
                                        fieldWithPath("[0].member.id").description("사용자 ID"),
                                        fieldWithPath("[0].member.name").description("사용자 닉네임"),
                                        fieldWithPath("[0].member.imageUrl").description("사용자 이미지 Url"),
                                        fieldWithPath("[0].checkFeed.id").description("인증 피드 ID"),
                                        fieldWithPath("[0].checkFeed.imageUrl").description("인증 피드 이미지 Url"),
                                        fieldWithPath("[0].checkFeed.description").description("인증 피드 설명"),
                                        fieldWithPath("[0].checkFeed.createdAt").description("인증 피드 등록 날짜"))))
                .andReturn().getResponse()
                .getContentAsString();

        // then
        final List<GoalRoomCheckFeedResponse> 골룸_인증피드_전체_조회_응답 = objectMapper.readValue(response,
                new TypeReference<>() {
                });
        assertThat(골룸_인증피드_전체_조회_응답)
                .isEqualTo(expected);
    }

    @Test
    void 골룸_인증피드_전체_조회_시_존재하지_않는_골룸일_경우_예외가_발생한다() throws Exception {
        //given
        doThrow(new NotFoundException("존재하지 않는 골룸입니다. goalRoomId = 1"))
                .when(goalRoomCheckFeedService)
                .findGoalRoomCheckFeeds(any(), any());

        //when
        final MvcResult mvcResult = mockMvc.perform(get(API_PREFIX + "/goal-rooms/{goalRoomId}/checkFeeds", 1L)
                        .header(AUTHORIZATION, String.format(BEARER_TOKEN_FORMAT, "test-token"))
                        .contextPath(API_PREFIX))
                .andExpect(status().isNotFound())
                .andDo(
                        documentationResultHandler.document(
                                pathParameters(
                                        parameterWithName("goalRoomId").description("골룸 아이디")
                                ),
                                responseFields(
                                        fieldWithPath("message").description("예외 메세지")
                                )))
                .andReturn();

        // then
        final ErrorResponse responses = jsonToClass(mvcResult, new TypeReference<>() {
        });

        assertThat(responses).isEqualTo(new ErrorResponse("존재하지 않는 골룸입니다. goalRoomId = 1"));
    }

    @Test
    void 골룸_인증피드_전체_조회_시_골룸에_참여하지_않은_사용자일_경우_예외_발생() throws Exception {
        //given
        doThrow(new BadRequestException("골룸에 참여하지 않은 회원입니다."))
                .when(goalRoomCheckFeedService)
                .findGoalRoomCheckFeeds(any(), any());

        //when
        final MvcResult mvcResult = mockMvc.perform(get(API_PREFIX + "/goal-rooms/{goalRoomId}/checkFeeds", 1L)
                        .header(AUTHORIZATION, String.format(BEARER_TOKEN_FORMAT, "test-token"))
                        .contextPath(API_PREFIX))
                .andExpect(status().isBadRequest())
                .andDo(
                        documentationResultHandler.document(
                                pathParameters(
                                        parameterWithName("goalRoomId").description("골룸 아이디")
                                ),
                                responseFields(
                                        fieldWithPath("message").description("예외 메세지")
                                )))
                .andReturn();

        // then
        final ErrorResponse responses = jsonToClass(mvcResult, new TypeReference<>() {
        });

        assertThat(responses).isEqualTo(new ErrorResponse("골룸에 참여하지 않은 회원입니다."));
    }
}
