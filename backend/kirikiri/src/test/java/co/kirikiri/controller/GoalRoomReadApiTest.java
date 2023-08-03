package co.kirikiri.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
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
import co.kirikiri.exception.ForbiddenException;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.service.GoalRoomCreateService;
import co.kirikiri.service.GoalRoomReadService;
import co.kirikiri.service.dto.ErrorResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomCertifiedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomMemberResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomNodeResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomToDoCheckResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomTodoResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MvcResult;

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
    void 정상적으로_골룸_멤버를_조회한다() throws Exception {
        // given
        final GoalRoomMemberResponse goalRoomMemberResponse1 = new GoalRoomMemberResponse(1L, "nickname1", "imagePath1",
                50D);
        final GoalRoomMemberResponse goalRoomMemberResponse2 = new GoalRoomMemberResponse(2L, "nickname2", "imagePath2",
                40D);
        given(goalRoomReadService.findGoalRoomMembers(anyLong()))
                .willReturn(List.of(goalRoomMemberResponse1, goalRoomMemberResponse2));

        // when
        final MvcResult mvcResult = mockMvc.perform(get(API_PREFIX + "/goal-rooms/{goalRoomId}/members", 1L)
                        .header(AUTHORIZATION, String.format(BEARER_TOKEN_FORMAT, "test-token"))
                        .contextPath(API_PREFIX))
                .andExpect(status().isOk())
                .andDo(
                        documentationResultHandler.document(
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).description("Bearer 액세스 토큰")
                                ),
                                pathParameters(
                                        parameterWithName("goalRoomId").description("골룸 아이디")
                                ),
                                responseFields(
                                        fieldWithPath("[0].memberId").description("회원 id"),
                                        fieldWithPath("[0].nickname").description("회원 닉네임"),
                                        fieldWithPath("[0].imagePath").description("회원 이미지 경로"),
                                        fieldWithPath("[0].accomplishmentRate").description("회원 달성률"))))
                .andReturn();

        // then
        final List<GoalRoomMemberResponse> response = jsonToClass(mvcResult, new TypeReference<>() {
        });

        assertThat(response)
                .isEqualTo(List.of(goalRoomMemberResponse1, goalRoomMemberResponse2));
    }

    @Test
    void 골룸_멤버_조회_시_존재하지_않는_골룸일_경우() throws Exception {
        //given
        doThrow(new NotFoundException("존재하지 않는 골룸입니다. goalRoomId = 1"))
                .when(goalRoomReadService)
                .findGoalRoomMembers(1L);

        //when
        final MvcResult mvcResult = mockMvc.perform(get(API_PREFIX + "/goal-rooms/{goalRoomId}/members", 1L)
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
    void 골룸의_투두리스트를_조회한다() throws Exception {
        // given
        final LocalDate today = LocalDate.now();
        final List<GoalRoomTodoResponse> goalRoomTodoResponses = List.of(
                new GoalRoomTodoResponse(1L, "투두 1", today, today.plusDays(10), new GoalRoomToDoCheckResponse(true)),
                new GoalRoomTodoResponse(2L, "투두 2", today.plusDays(20), today.plusDays(30),
                        new GoalRoomToDoCheckResponse(false)));

        when(goalRoomReadService.getAllGoalRoomTodo(any(), any()))
                .thenReturn(goalRoomTodoResponses);

        // when
        final MvcResult mvcResult = mockMvc.perform(get(API_PREFIX + "/goal-rooms/{goalRoomId}/todos", 1L)
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
                                        fieldWithPath("[0].id").description("투두 아이디"),
                                        fieldWithPath("[0].content").description("투두 내용"),
                                        fieldWithPath("[0].startDate").description("투두 시작 날짜"),
                                        fieldWithPath("[0].endDate").description("투두 종료 날짜"),
                                        fieldWithPath("[0].check.isChecked").description("투두 체크 여부")
                                )))
                .andReturn();

        // then
        final List<GoalRoomTodoResponse> response = jsonToClass(mvcResult, new TypeReference<>() {
        });

        assertThat(response)
                .isEqualTo(goalRoomTodoResponses);
    }

    @Test
    void 골룸_투두리스트_조회시_존재하지_않은_골룸일_경우() throws Exception {
        // given
        doThrow(new NotFoundException("존재하지 않는 골룸입니다. goalRoomId = 1"))
                .when(goalRoomReadService)
                .getAllGoalRoomTodo(any(), any());

        // when
        final MvcResult mvcResult = mockMvc.perform(get(API_PREFIX + "/goal-rooms/{goalRoomId}/todos", 1L)
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

        assertThat(responses)
                .isEqualTo(new ErrorResponse("존재하지 않는 골룸입니다. goalRoomId = 1"));
    }

    @Test
    void 골룸_투두리스트_조회시_참여하지_않은_사용자일_경우() throws Exception {
        // given
        doThrow(new ForbiddenException("골룸에 참여하지 않은 사용자입니다. goalRoomId = 1 memberIdentifier = identifier"))
                .when(goalRoomReadService)
                .getAllGoalRoomTodo(any(), any());

        // when
        final MvcResult mvcResult = mockMvc.perform(get(API_PREFIX + "/goal-rooms/{goalRoomId}/todos", 1L)
                        .header(AUTHORIZATION, String.format(BEARER_TOKEN_FORMAT, "test-token"))
                        .contextPath(API_PREFIX))
                .andExpect(status().isForbidden())
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

        assertThat(responses)
                .isEqualTo(new ErrorResponse("골룸에 참여하지 않은 사용자입니다. goalRoomId = 1 memberIdentifier = identifier"));
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
