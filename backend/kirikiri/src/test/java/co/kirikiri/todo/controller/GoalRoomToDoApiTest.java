package co.kirikiri.todo.controller;

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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.kirikiri.common.exception.BadRequestException;
import co.kirikiri.common.exception.ForbiddenException;
import co.kirikiri.common.exception.NotFoundException;
import co.kirikiri.common.helper.ControllerTestHelper;
import co.kirikiri.common.helper.FieldDescriptionHelper.FieldDescription;
import co.kirikiri.common.service.dto.ErrorResponse;
import co.kirikiri.todo.service.GoalRoomToDoService;
import co.kirikiri.todo.service.dto.request.GoalRoomTodoRequest;
import co.kirikiri.todo.service.dto.response.GoalRoomToDoCheckResponse;
import co.kirikiri.todo.service.dto.response.GoalRoomTodoResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(GoalRoomToDoController.class)
class GoalRoomToDoApiTest extends ControllerTestHelper {

    private static final LocalDate TODAY = LocalDate.now();
    private static final LocalDate TEN_DAY_LATER = TODAY.plusDays(10);

    @MockBean
    private GoalRoomToDoService goalRoomToDoService;

    @Test
    void 정상적으로_골룸에_투두리스트를_추가한다() throws Exception {
        //given
        final GoalRoomTodoRequest goalRoomTodoRequest = new GoalRoomTodoRequest("content", TODAY, TEN_DAY_LATER);
        final String jsonRequest = objectMapper.writeValueAsString(goalRoomTodoRequest);
        given(goalRoomToDoService.addGoalRoomTodo(anyLong(), anyString(), any()))
                .willReturn(1L);

        //when
        final MvcResult mvcResult = mockMvc.perform(post(API_PREFIX + "/goal-rooms/{goalRoomId}/todos", 1L)
                        .content(jsonRequest)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .contextPath(API_PREFIX))
                .andExpect(status().isCreated())
                .andDo(print())
                .andDo(documentationResultHandler.document(
                        requestFields(makeFieldDescriptor(makeAddTodoSuccessRequestFieldDescription())),
                        requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")),
                        responseHeaders(headerWithName(HttpHeaders.LOCATION).description("골룸 투두 단일 조회 api 경로")),
                        pathParameters(parameterWithName("goalRoomId").description("골룸 아이디"))))
                .andReturn();

        //then
        assertThat(mvcResult.getResponse().getHeader(HttpHeaders.LOCATION)).isEqualTo(
                API_PREFIX + "/goal-rooms/1/todos/1");
    }

    @Test
    void 골룸_투두_추가시_존재하지_않는_회원일_경우() throws Exception {
        //given
        final GoalRoomTodoRequest goalRoomTodoRequest = new GoalRoomTodoRequest("content", TODAY, TEN_DAY_LATER);
        final String jsonRequest = objectMapper.writeValueAsString(goalRoomTodoRequest);
        doThrow(new NotFoundException("존재하지 않는 회원입니다."))
                .when(goalRoomToDoService)
                .addGoalRoomTodo(anyLong(), anyString(), any());

        //when
        final MvcResult mvcResult = mockMvc.perform(post(API_PREFIX + "/goal-rooms/{goalRoomId}/todos", 1)
                        .content(jsonRequest)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .contextPath(API_PREFIX))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andDo(documentationResultHandler.document(
                        requestFields(makeFieldDescriptor(makeAddTodoSuccessRequestFieldDescription())),
                        requestHeaders(headerWithName(AUTHORIZATION).description("액세스 토큰")),
                        pathParameters(parameterWithName("goalRoomId").description("골룸 아이디")),
                        responseFields(fieldWithPath("message").description("예외 메세지"))))
                .andReturn();

        //then
        final ErrorResponse response = jsonToClass(mvcResult, new TypeReference<>() {
        });
        assertThat(response).isEqualTo(new ErrorResponse("존재하지 않는 회원입니다."));
    }

    @Test
    void 골룸_투두_추가시_존재하지_않는_골룸일_경우() throws Exception {
        //given
        final GoalRoomTodoRequest goalRoomTodoRequest = new GoalRoomTodoRequest("content", TODAY, TEN_DAY_LATER);
        final String jsonRequest = objectMapper.writeValueAsString(goalRoomTodoRequest);
        doThrow(new NotFoundException("존재하지 않는 골룸입니다. goalRoomId = 1"))
                .when(goalRoomToDoService)
                .addGoalRoomTodo(anyLong(), anyString(), any());

        //when
        final MvcResult mvcResult = mockMvc.perform(post(API_PREFIX + "/goal-rooms/{goalRoomId}/todos", 1)
                        .content(jsonRequest)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .contextPath(API_PREFIX))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andDo(documentationResultHandler.document(
                        requestFields(makeFieldDescriptor(makeAddTodoSuccessRequestFieldDescription())),
                        requestHeaders(headerWithName(AUTHORIZATION).description("액세스 토큰")),
                        pathParameters(parameterWithName("goalRoomId").description("골룸 아이디")),
                        responseFields(fieldWithPath("message").description("예외 메세지"))))
                .andReturn();

        //then
        final ErrorResponse response = jsonToClass(mvcResult, new TypeReference<>() {
        });
        assertThat(response).isEqualTo(new ErrorResponse("존재하지 않는 골룸입니다. goalRoomId = 1"));
    }

    @Test
    void 골룸_투두_추가시_이미_종료된_골룸일_경우() throws Exception {
        //given
        final GoalRoomTodoRequest goalRoomTodoRequest = new GoalRoomTodoRequest("content", TODAY, TEN_DAY_LATER);
        final String jsonRequest = objectMapper.writeValueAsString(goalRoomTodoRequest);
        doThrow(new BadRequestException("이미 종료된 골룸입니다."))
                .when(goalRoomToDoService)
                .addGoalRoomTodo(anyLong(), anyString(), any());

        //when
        final MvcResult mvcResult = mockMvc.perform(post(API_PREFIX + "/goal-rooms/{goalRoomId}/todos", 1L)
                        .content(jsonRequest)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .contextPath(API_PREFIX))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andDo(documentationResultHandler.document(
                        requestFields(makeFieldDescriptor(makeAddTodoSuccessRequestFieldDescription())),
                        requestHeaders(headerWithName(AUTHORIZATION).description("액세스 토큰")),
                        pathParameters(parameterWithName("goalRoomId").description("골룸 아이디")),
                        responseFields(fieldWithPath("message").description("예외 메세지"))))
                .andReturn();

        //then
        final ErrorResponse response = jsonToClass(mvcResult, new TypeReference<>() {
        });
        assertThat(response).isEqualTo(new ErrorResponse("이미 종료된 골룸입니다."));
    }

    @Test
    void 골룸_투두_추가시_리더가_아닌_경우() throws Exception {
        //given
        final GoalRoomTodoRequest goalRoomTodoRequest = new GoalRoomTodoRequest("content", TODAY, TEN_DAY_LATER);
        final String jsonRequest = objectMapper.writeValueAsString(goalRoomTodoRequest);
        doThrow(new BadRequestException("골룸의 리더만 투두리스트를 추가할 수 있습니다."))
                .when(goalRoomToDoService)
                .addGoalRoomTodo(anyLong(), anyString(), any());

        //when
        final MvcResult mvcResult = mockMvc.perform(post(API_PREFIX + "/goal-rooms/{goalRoomId}/todos", 1L)
                        .content(jsonRequest)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .contextPath(API_PREFIX))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andDo(documentationResultHandler.document(
                        requestFields(makeFieldDescriptor(makeAddTodoSuccessRequestFieldDescription())),
                        requestHeaders(headerWithName(AUTHORIZATION).description("액세스 토큰")),
                        pathParameters(parameterWithName("goalRoomId").description("골룸 아이디")),
                        responseFields(fieldWithPath("message").description("예외 메세지"))))
                .andReturn();

        //then
        final ErrorResponse response = jsonToClass(mvcResult, new TypeReference<>() {
        });
        assertThat(response).isEqualTo(new ErrorResponse("골룸의 리더만 투두리스트를 추가할 수 있습니다."));
    }

    @Test
    void 골룸_투두_추가시_컨텐츠가_250글자가_넘을_경우() throws Exception {
        //given
        final String content = "a".repeat(251);
        final GoalRoomTodoRequest goalRoomTodoRequest = new GoalRoomTodoRequest(content, TODAY, TEN_DAY_LATER);
        final String jsonRequest = objectMapper.writeValueAsString(goalRoomTodoRequest);
        doThrow(new BadRequestException("투두 컨텐츠의 길이가 적절하지 않습니다."))
                .when(goalRoomToDoService)
                .addGoalRoomTodo(anyLong(), anyString(), any());

        //when
        final MvcResult mvcResult = mockMvc.perform(post(API_PREFIX + "/goal-rooms/{goalRoomId}/todos", 1L)
                        .content(jsonRequest)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .contextPath(API_PREFIX))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andDo(documentationResultHandler.document(
                        requestFields(makeFieldDescriptor(makeAddTodoSuccessRequestFieldDescription())),
                        requestHeaders(headerWithName(AUTHORIZATION).description("액세스 토큰")),
                        pathParameters(parameterWithName("goalRoomId").description("골룸 아이디")),
                        responseFields(fieldWithPath("message").description("예외 메세지"))))
                .andReturn();

        //then
        final ErrorResponse response = jsonToClass(mvcResult, new TypeReference<>() {
        });
        assertThat(response).isEqualTo(new ErrorResponse("투두 컨텐츠의 길이가 적절하지 않습니다."));
    }

    @Test
    void 골룸_투두리스트에_대해_체크한다() throws Exception {
        // given
        final GoalRoomToDoCheckResponse expected = new GoalRoomToDoCheckResponse(true);
        when(goalRoomToDoService.checkGoalRoomTodo(anyLong(), anyLong(), anyString()))
                .thenReturn(expected);

        // when
        final MvcResult mvcResult = mockMvc.perform(post(API_PREFIX + "/goal-rooms/{goalRoomId}/todos/{todoId}", 1L, 1L)
                        .header(AUTHORIZATION, String.format(BEARER_TOKEN_FORMAT, "test-token"))
                        .contextPath(API_PREFIX))
                .andExpect(status().isOk())
                .andDo(
                        documentationResultHandler.document(
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).description("액세스 토큰")),
                                pathParameters(
                                        parameterWithName("goalRoomId").description("골룸 아이디"),
                                        parameterWithName("todoId").description("골룸 투두 아이디")),
                                responseFields(
                                        fieldWithPath("isChecked").description(
                                                "투두 체크 현황 (true: 체크됨, false: 체크되지 않음)"))))
                .andReturn();

        // then
        final GoalRoomToDoCheckResponse response = jsonToClass(mvcResult, new TypeReference<>() {
        });

        assertThat(response)
                .isEqualTo(expected);
    }

    @Test
    void 골룸_투두리스트_체크시_체크_이력이_있으면_제거한다() throws Exception {
        // given
        final GoalRoomToDoCheckResponse expected = new GoalRoomToDoCheckResponse(false);
        when(goalRoomToDoService.checkGoalRoomTodo(anyLong(), anyLong(), anyString()))
                .thenReturn(expected);

        // when
        final MvcResult mvcResult = mockMvc.perform(post(API_PREFIX + "/goal-rooms/{goalRoomId}/todos/{todoId}", 1L, 1L)
                        .header(AUTHORIZATION, String.format(BEARER_TOKEN_FORMAT, "test-token"))
                        .contextPath(API_PREFIX))
                .andExpect(status().isOk())
                .andDo(
                        documentationResultHandler.document(
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).description("액세스 토큰")),
                                pathParameters(
                                        parameterWithName("goalRoomId").description("골룸 아이디"),
                                        parameterWithName("todoId").description("골룸 투두 아이디")),
                                responseFields(
                                        fieldWithPath("isChecked").description(
                                                "투두 체크 현황 (true: 체크됨, false: 체크되지 않음)"))))
                .andReturn();

        // then
        final GoalRoomToDoCheckResponse response = jsonToClass(mvcResult, new TypeReference<>() {
        });

        assertThat(response)
                .isEqualTo(expected);
    }

    @Test
    void 골룸_투두리스트_체크시_골룸이_존재하지_않으면_예외가_발생한다() throws Exception {
        //given
        doThrow(new NotFoundException("골룸이 존재하지 않습니다. goalRoomId = 1"))
                .when(goalRoomToDoService)
                .checkGoalRoomTodo(anyLong(), anyLong(), anyString());

        //when
        final MvcResult mvcResult = mockMvc.perform(post(API_PREFIX + "/goal-rooms/{goalRoomId}/todos/{todoId}", 1L, 1L)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .contextPath(API_PREFIX))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andDo(documentationResultHandler.document(
                        requestHeaders(headerWithName(AUTHORIZATION).description("액세스 토큰")),
                        pathParameters(
                                parameterWithName("goalRoomId").description("골룸 아이디"),
                                parameterWithName("todoId").description("골룸 투두 아이디")),
                        responseFields(fieldWithPath("message").description("예외 메세지"))))
                .andReturn();

        //then
        final ErrorResponse response = jsonToClass(mvcResult, new TypeReference<>() {
        });
        assertThat(response).isEqualTo(new ErrorResponse("골룸이 존재하지 않습니다. goalRoomId = 1"));
    }

    @Test
    void 골룸_투두리스트_체크시_해당_투두가_존재하지_않으면_예외가_발생한다() throws Exception {
        //given
        doThrow(new NotFoundException("존재하지 않는 투두입니다. todoId = 1"))
                .when(goalRoomToDoService)
                .checkGoalRoomTodo(anyLong(), anyLong(), anyString());

        //when
        final MvcResult mvcResult = mockMvc.perform(post(API_PREFIX + "/goal-rooms/{goalRoomId}/todos/{todoId}", 1L, 1L)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .contextPath(API_PREFIX))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andDo(documentationResultHandler.document(
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("액세스 토큰")),
                        pathParameters(
                                parameterWithName("goalRoomId").description("골룸 아이디"),
                                parameterWithName("todoId").description("골룸 투두 아이디")),
                        responseFields(
                                fieldWithPath("message").description("예외 메세지"))))
                .andReturn();

        //then
        final ErrorResponse response = jsonToClass(mvcResult, new TypeReference<>() {
        });
        assertThat(response)
                .isEqualTo(new ErrorResponse("존재하지 않는 투두입니다. todoId = 1"));
    }

    @Test
    void 골룸_투두리스트_체크시_사용자가_없으면_예외가_발생한다() throws Exception {
        //given
        doThrow(new NotFoundException("골룸에 사용자가 존재하지 않습니다. goalRoomId = 1 memberIdentifier = cokirikiri"))
                .when(goalRoomToDoService)
                .checkGoalRoomTodo(anyLong(), anyLong(), anyString());

        //when
        final MvcResult mvcResult = mockMvc.perform(post(API_PREFIX + "/goal-rooms/{goalRoomId}/todos/{todoId}", 1L, 1L)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .contextPath(API_PREFIX))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andDo(documentationResultHandler.document(
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("액세스 토큰")),
                        pathParameters(
                                parameterWithName("goalRoomId").description("골룸 아이디"),
                                parameterWithName("todoId").description("골룸 투두 아이디")),
                        responseFields(
                                fieldWithPath("message").description("예외 메세지"))))
                .andReturn();

        //then
        final ErrorResponse response = jsonToClass(mvcResult, new TypeReference<>() {
        });
        assertThat(response)
                .isEqualTo(new ErrorResponse("골룸에 사용자가 존재하지 않습니다. goalRoomId = 1 memberIdentifier = cokirikiri"));
    }

    @Test
    void 골룸의_투두리스트를_조회한다() throws Exception {
        // given
        final LocalDate today = LocalDate.now();
        final List<GoalRoomTodoResponse> goalRoomTodoResponses = List.of(
                new GoalRoomTodoResponse(1L, "투두 1", today, today.plusDays(10), new GoalRoomToDoCheckResponse(true)),
                new GoalRoomTodoResponse(2L, "투두 2", today.plusDays(20), today.plusDays(30),
                        new GoalRoomToDoCheckResponse(false)));

        when(goalRoomToDoService.findAllGoalRoomTodo(any(), any()))
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
                .when(goalRoomToDoService)
                .findAllGoalRoomTodo(any(), any());

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
                .when(goalRoomToDoService)
                .findAllGoalRoomTodo(any(), any());

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

    private List<FieldDescription> makeAddTodoSuccessRequestFieldDescription() {
        return List.of(
                new FieldDescription("content", "골룸 투두 컨텐츠", "- 길이 : 1 ~ 250"),
                new FieldDescription("startDate", "골룸 투두 시작일", "- yyyyMMdd 형식"),
                new FieldDescription("endDate", "골룸 투두 종료일", "- yyyyMMdd 형식")
        );
    }
}
