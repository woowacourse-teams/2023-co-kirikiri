package co.kirikiri.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.kirikiri.controller.helper.ControllerTestHelper;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.service.GoalRoomCreateService;
import co.kirikiri.service.GoalRoomReadService;
import co.kirikiri.service.dto.ErrorResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomCertifiedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomNodeResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@WebMvcTest(GoalRoomController.class)
class GoalRoomReadApiTest extends ControllerTestHelper {

    @MockBean
    private GoalRoomReadService goalRoomReadService;

    @MockBean
    private GoalRoomCreateService goalRoomCreateService;

    @Test
    void 골룸_아이디로_골룸을_조회한다() throws Exception {
        // given
        final GoalRoomResponse expected = 골룸_조회_응답을_생성한다();
        when(goalRoomReadService.findGoalRoom(any()))
                .thenReturn(expected);

        // when
        final String response = mockMvc.perform(
                        get(API_PREFIX + "/goal-rooms/{goalRoomId}", 1L)
                                .contextPath(API_PREFIX))
                .andExpect(status().isOk())
                .andDo(
                        documentationResultHandler.document(
                                pathParameters(
                                        parameterWithName("goalRoomId").description("골룸 아이디")
                                ),
                                responseFields(
                                        fieldWithPath("name").description("골룸 제목"),
                                        fieldWithPath("currentMemberCount").description("현재 참여 인원 수"),
                                        fieldWithPath("limitedMemberCount").description("모집 인원 수"),
                                        fieldWithPath("goalRoomNodes[0].title").description("골룸 로드맵 노드 제목"),
                                        fieldWithPath("goalRoomNodes[0].startDate").description("골룸 로드맵 노드 시작 날짜"),
                                        fieldWithPath("goalRoomNodes[0].endDate").description("골룸 로드맵 노드 종료 날짜"),
                                        fieldWithPath("goalRoomNodes[0].checkCount").description("골룸 로드맵 노드 인증 횟수"),
                                        fieldWithPath("period").description("골룸 진행 기간"))))
                .andReturn().getResponse()
                .getContentAsString();

        // then
        final GoalRoomResponse 골룸_단일_조회_응답 = objectMapper.readValue(response, new TypeReference<>() {
        });
        assertThat(골룸_단일_조회_응답)
                .isEqualTo(expected);
    }

    @Test
    void 골룸_아이디로_골룸_조회시_아이디가_유효하지_않으면_예외가_발생한다() throws Exception {
        // given
        when(goalRoomReadService.findGoalRoom(any()))
                .thenThrow(new NotFoundException("골룸 정보가 존재하지 않습니다. goalRoomId = 1L"));

        // when
        final String response = mockMvc.perform(get(API_PREFIX + "/goal-rooms/{goalRoomId}", 1L)
                        .contextPath(API_PREFIX))
                .andExpectAll(
                        status().is4xxClientError(),
                        jsonPath("$.message").value("골룸 정보가 존재하지 않습니다. goalRoomId = 1L"))
                .andDo(documentationResultHandler.document(
                        pathParameters(
                                parameterWithName("goalRoomId").description("골룸 아이디")),
                        responseFields(fieldWithPath("message").description("예외 메시지"))))
                .andReturn().getResponse()
                .getContentAsString();

        // then
        final ErrorResponse errorResponse = objectMapper.readValue(response, ErrorResponse.class);
        final ErrorResponse expected = new ErrorResponse("골룸 정보가 존재하지 않습니다. goalRoomId = 1L");
        assertThat(errorResponse)
                .isEqualTo(expected);
    }

    @Test
    void 골룸_아이디와_사용자_아이디로_골룸을_조회한다() throws Exception {
        // given
        final GoalRoomCertifiedResponse expected = 로그인시_골룸_조회_응답을_생성한다(true);
        when(goalRoomReadService.findGoalRoom(any(), any()))
                .thenReturn(expected);

        // when
        final String response = mockMvc.perform(
                        get(API_PREFIX + "/goal-rooms/{goalRoomId}", 1L)
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
                                        fieldWithPath("name").description("골룸 제목"),
                                        fieldWithPath("currentMemberCount").description("현재 참여 인원 수"),
                                        fieldWithPath("limitedMemberCount").description("모집 인원 수"),
                                        fieldWithPath("goalRoomNodes[0].title").description("골룸 로드맵 노드 제목"),
                                        fieldWithPath("goalRoomNodes[0].startDate").description("골룸 로드맵 노드 시작 날짜"),
                                        fieldWithPath("goalRoomNodes[0].endDate").description("골룸 로드맵 노드 종료 날짜"),
                                        fieldWithPath("goalRoomNodes[0].checkCount").description("골룸 로드맵 노드 인증 횟수"),
                                        fieldWithPath("period").description("골룸 진행 기간"),
                                        fieldWithPath("isJoined").description("골룸 참여 여부 (true / false)"))))
                .andReturn().getResponse()
                .getContentAsString();

        // then
        final GoalRoomCertifiedResponse 골룸_단일_조회_응답 = objectMapper.readValue(response, new TypeReference<>() {
        });
        assertThat(골룸_단일_조회_응답)
                .isEqualTo(expected);
    }

    @Test
    void 골룸_아이디와_사용자_아이디로_골룸_조회시_골룸_아이디가_유효하지_않으면_예외_발생() throws Exception {
        // given
        when(goalRoomReadService.findGoalRoom(any(), any()))
                .thenThrow(new NotFoundException("골룸 정보가 존재하지 않습니다. goalRoomId = 1L"));

        // when
        final String response = mockMvc.perform(get(API_PREFIX + "/goal-rooms/{goalRoomId}", 1L)
                        .header(AUTHORIZATION, String.format(BEARER_TOKEN_FORMAT, "test-token"))
                        .contextPath(API_PREFIX))
                .andExpectAll(
                        status().is4xxClientError(),
                        jsonPath("$.message").value("골룸 정보가 존재하지 않습니다. goalRoomId = 1L"))
                .andDo(documentationResultHandler.document(
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("액세스 토큰")
                        ),
                        pathParameters(
                                parameterWithName("goalRoomId").description("골룸 아이디")),
                        responseFields(fieldWithPath("message").description("예외 메시지"))))
                .andReturn().getResponse()
                .getContentAsString();

        // then
        final ErrorResponse errorResponse = objectMapper.readValue(response, ErrorResponse.class);
        final ErrorResponse expected = new ErrorResponse("골룸 정보가 존재하지 않습니다. goalRoomId = 1L");
        assertThat(errorResponse)
                .isEqualTo(expected);
    }
    
    private GoalRoomResponse 골룸_조회_응답을_생성한다() {
        final List<GoalRoomNodeResponse> goalRoomNodeResponses = List.of(
                new GoalRoomNodeResponse("로드맵 1주차", LocalDate.of(2023, 7, 19),
                        LocalDate.of(2023, 7, 30), 10),
                new GoalRoomNodeResponse("로드맵 2주차", LocalDate.of(2023, 8, 1),
                        LocalDate.of(2023, 8, 5), 2));
        return new GoalRoomResponse("골룸", 1, 10, goalRoomNodeResponses, 17);
    }

    private GoalRoomCertifiedResponse 로그인시_골룸_조회_응답을_생성한다(final boolean isJoined) {
        final List<GoalRoomNodeResponse> goalRoomNodeResponses = List.of(
                new GoalRoomNodeResponse("로드맵 1주차", LocalDate.of(2023, 7, 19),
                        LocalDate.of(2023, 7, 30), 10),
                new GoalRoomNodeResponse("로드맵 2주차", LocalDate.of(2023, 8, 1),
                        LocalDate.of(2023, 8, 5), 2));
        return new GoalRoomCertifiedResponse("골룸", 1, 10, goalRoomNodeResponses, 17, isJoined);
    }
}
