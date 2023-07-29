package co.kirikiri.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.kirikiri.controller.helper.ControllerTestHelper;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.service.GoalRoomCreateService;
import co.kirikiri.service.GoalRoomReadService;
import co.kirikiri.service.dto.ErrorResponse;
import co.kirikiri.service.dto.PageResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomCertifiedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomForListResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomNodeResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomResponse;
import co.kirikiri.service.dto.member.response.MemberResponse;
import co.kirikiri.service.dto.roadmap.request.RoadmapFilterTypeRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Test
    void 골룸_목록을_조건에_따라_조회한다() throws Exception {
        // given
        final PageResponse<GoalRoomForListResponse> 골룸_페이지_응답 = 골룸_페이지_응답을_생성한다();
        given(goalRoomReadService.findGoalRoomsByFilterType(any(), any()))
                .willReturn(골룸_페이지_응답);

        // when
        final String 응답값 = mockMvc.perform(
                        get(API_PREFIX + "/goal-rooms")
                                .param("filterCond", RoadmapFilterTypeRequest
                                        .LATEST.name())
                                .param("page", "1")
                                .param("size", "10")
                                .contextPath(API_PREFIX))
                .andExpect(status().isOk())
                .andDo(
                        documentationResultHandler.document(
                                queryParameters(
                                        parameterWithName("filterCond").description(
                                                "필터 조건(LATEST, PARTICIPATION_RATE)").optional(),
                                        parameterWithName("page").description("타겟 페이지 (1부터 시작)"),
                                        parameterWithName("size").description("한 페이지에서 받아올 로드맵의 수")),
                                responseFields(
                                        fieldWithPath("currentPage").description("현재 페이지 값"),
                                        fieldWithPath("totalPage").description("총 페이지 수"),
                                        fieldWithPath("data[0].goalRoomId").description("골룸 아이디"),
                                        fieldWithPath("data[0].name").description("골룸 이름"),
                                        fieldWithPath("data[0].currentMemberCount").description("현재 골룸에 참여한 인원 수"),
                                        fieldWithPath("data[0].limitedMemberCount").description("골룸에 참여할 수 있는 제한 인원 수"),
                                        fieldWithPath("data[0].createdAt").description("골룸 생성 날짜와 시간"),
                                        fieldWithPath("data[0].startDate").description("골룸의 시작 날짜"),
                                        fieldWithPath("data[0].endDate").description("골룸의 종료 날짜"),
                                        fieldWithPath("data[0].goalRoomLeader.id").description("골룸 리더의 아이디"),
                                        fieldWithPath("data[0].goalRoomLeader.name").description("골룸 리더의 닉네임"))
                        )
                )
                .andReturn().getResponse()
                .getContentAsString();

        // then
        final PageResponse<GoalRoomForListResponse> 응답값으로_생성한_골룸_페이지 = objectMapper.readValue(응답값,
                new TypeReference<>() {
                });

        final PageResponse<GoalRoomForListResponse> 예상되는_골룸_페이지_응답 = 골룸_페이지_응답을_생성한다();
        assertThat(응답값으로_생성한_골룸_페이지)
                .usingRecursiveComparison()
                .isEqualTo(예상되는_골룸_페이지_응답);
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

    private PageResponse<GoalRoomForListResponse> 골룸_페이지_응답을_생성한다() {
        final GoalRoomForListResponse goalRoomForListResponse1 = new GoalRoomForListResponse(1L, "골룸 이름1", 3, 6,
                LocalDateTime.of(2023, 7, 20, 13, 0, 0),
                LocalDate.now(), LocalDate.now().plusDays(100),
                new MemberResponse(1L, "황시진"));
        final GoalRoomForListResponse goalRoomForListResponse2 = new GoalRoomForListResponse(2L, "골룸 이름2", 4, 10,
                LocalDateTime.of(2023, 7, 10, 13, 0, 0),
                LocalDate.now(), LocalDate.now().plusDays(100),
                new MemberResponse(2L, "시진이"));
        return new PageResponse<>(1, 2, List.of(goalRoomForListResponse1, goalRoomForListResponse2));
    }
}
