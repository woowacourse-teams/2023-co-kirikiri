package co.kirikiri.controller;

import co.kirikiri.controller.helper.ControllerTestHelper;
import co.kirikiri.controller.helper.FieldDescriptionHelper.FieldDescription;
import co.kirikiri.exception.BadRequestException;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.service.AuthService;
import co.kirikiri.service.GoalRoomService;
import co.kirikiri.service.dto.ErrorResponse;
import co.kirikiri.service.dto.goalroom.request.GoalRoomCreateRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomRoadmapNodeRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomTodoRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GoalRoomController.class)
class GoalRoomCreateApiTest extends ControllerTestHelper {

    private static final String IDENTIFIER = "identifier1";
    private static final LocalDate TODAY = LocalDate.now();
    private static final LocalDate TEN_DAY_LATER = TODAY.plusDays(10);

    @MockBean
    private GoalRoomService goalRoomService;

    @MockBean
    private AuthService authService;

    @Test
    void 정상적으로_골룸을_생성한다() throws Exception {
        //given
        final GoalRoomCreateRequest request = new GoalRoomCreateRequest(1L, "name",
                20, new GoalRoomTodoRequest("content", TODAY, TEN_DAY_LATER),
                new ArrayList<>(List.of(new GoalRoomRoadmapNodeRequest(1L, 10, TODAY, TEN_DAY_LATER))));

        given(goalRoomService.create(any(), any()))
                .willReturn(1L);
        given(authService.findIdentifierByToken(anyString()))
                .willReturn(IDENTIFIER);
        final String jsonRequest = objectMapper.writeValueAsString(request);

        //when
        final List<FieldDescription> requestFieldDescription = makeSuccessRequestFieldDescription();

        final MvcResult mvcResult = 골룸_생성(jsonRequest, status().isCreated())
                .andDo(documentationResultHandler.document(
                        requestFields(makeFieldDescriptor(requestFieldDescription)),
                        requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token"))
                ))
                .andReturn();


        //then
        assertThat(mvcResult.getResponse().getHeader("Location")).isEqualTo("/api/goal-rooms/" + 1);
    }

    @Test
    void 골룸_생성_시_요청에_빈값이_있을_경우() throws Exception {
        //given
        final GoalRoomCreateRequest request = new GoalRoomCreateRequest(null, null,
                null, new GoalRoomTodoRequest(null, null, null),
                new ArrayList<>(List.of(new GoalRoomRoadmapNodeRequest(null, null, null, null))));
        final String jsonRequest = objectMapper.writeValueAsString(request);

        //when
        final MvcResult mvcResult = 골룸_생성(jsonRequest, status().isBadRequest())
                .andReturn();

        //then
        final ErrorResponse roadmapCheckCountIdErrorResponse = new ErrorResponse("인증 횟수는 빈 값일 수 없습니다.");
        final ErrorResponse roadmapNodeIdErrorResponse = new ErrorResponse("로드맵 노드 아이디는 빈 값일 수 없습니다.");
        final ErrorResponse goalRoomTodoContentErrorResponse = new ErrorResponse("투두의 컨텐츠는 빈 값일 수 없습니다.");
        final ErrorResponse limitedMemberCountErrorResponse = new ErrorResponse("골룸 제한 인원은 빈 값일 수 없습니다.");
        final ErrorResponse goalRoomNameErrorResponse = new ErrorResponse("골룸 이름을 빈 값일 수 없습니다.");
        final ErrorResponse roadmapContentIdErrorResponse = new ErrorResponse("로드맵 컨텐츠 아이디는 빈 값일 수 없습니다.");
        final List<ErrorResponse> responses = jsonToClass(mvcResult, new TypeReference<>() {
        });

        assertThat(responses).usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(List.of(roadmapCheckCountIdErrorResponse, roadmapNodeIdErrorResponse, goalRoomTodoContentErrorResponse,
                        limitedMemberCountErrorResponse, goalRoomNameErrorResponse, roadmapContentIdErrorResponse));
    }

    @Test
    void 골룸_생성_시_로드맵이_존재하지_않을_경우() throws Exception {
        //given
        final GoalRoomCreateRequest request = new GoalRoomCreateRequest(1L, "name",
                20, new GoalRoomTodoRequest("content", TODAY, TEN_DAY_LATER),
                new ArrayList<>(List.of(new GoalRoomRoadmapNodeRequest(1L, 10, TODAY, TEN_DAY_LATER))));
        final String jsonRequest = objectMapper.writeValueAsString(request);
        doThrow(new NotFoundException("존재하지 않는 로드맵입니다."))
                .when(goalRoomService)
                .create(any(), any());

        //when
        final MvcResult mvcResult = 골룸_생성(jsonRequest, status().isNotFound())
                .andReturn();

        //then
        final ErrorResponse expectedResponse = new ErrorResponse("존재하지 않는 로드맵입니다.");
        final ErrorResponse response = jsonToClass(mvcResult, new TypeReference<>() {
        });
        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    void 골룸_생성_시_로드맵의_노드_크기와_요청의_노드_크기가_일치하지_않을_경우() throws Exception {
        //given
        final GoalRoomCreateRequest request = new GoalRoomCreateRequest(1L, "name",
                20, new GoalRoomTodoRequest("content", TODAY, TEN_DAY_LATER),
                new ArrayList<>(List.of(new GoalRoomRoadmapNodeRequest(1L, 10, TODAY, TEN_DAY_LATER))));
        final String jsonRequest = objectMapper.writeValueAsString(request);
        doThrow(new BadRequestException("모든 노드에 대해 기간이 설정돼야 합니다."))
                .when(goalRoomService)
                .create(any(), any());

        //when
        final MvcResult mvcResult = 골룸_생성(jsonRequest, status().isBadRequest())
                .andReturn();

        //then
        final ErrorResponse expectedResponse = new ErrorResponse("모든 노드에 대해 기간이 설정돼야 합니다.");
        final ErrorResponse response = jsonToClass(mvcResult, new TypeReference<>() {
        });
        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    void 골룸_생성_시_로드맵에_존재하지_않는_노드일_경우() throws Exception {
        //given
        final GoalRoomCreateRequest request = new GoalRoomCreateRequest(1L, "name",
                20, new GoalRoomTodoRequest("content", TODAY, TEN_DAY_LATER),
                new ArrayList<>(List.of(new GoalRoomRoadmapNodeRequest(1L, 10, TODAY, TEN_DAY_LATER))));
        final String jsonRequest = objectMapper.writeValueAsString(request);
        doThrow(new NotFoundException("로드맵에 존재하지 않는 노드입니다."))
                .when(goalRoomService)
                .create(any(), any());

        //when
        final MvcResult mvcResult = 골룸_생성(jsonRequest, status().isNotFound())
                .andReturn();

        //then
        final ErrorResponse expectedResponse = new ErrorResponse("로드맵에 존재하지 않는 노드입니다.");
        final ErrorResponse response = jsonToClass(mvcResult, new TypeReference<>() {
        });
        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    void 골룸_생성_시_존재하지_않는_회원일_경우() throws Exception {
        //given
        final GoalRoomCreateRequest request = new GoalRoomCreateRequest(1L, "name",
                20, new GoalRoomTodoRequest("content", TODAY, TEN_DAY_LATER),
                new ArrayList<>(List.of(new GoalRoomRoadmapNodeRequest(1L, 10, TODAY, TEN_DAY_LATER))));
        final String jsonRequest = objectMapper.writeValueAsString(request);
        doThrow(new NotFoundException("존재하지 않는 회원입니다."))
                .when(goalRoomService)
                .create(any(), any());

        //when
        final MvcResult mvcResult = 골룸_생성(jsonRequest, status().isNotFound())
                .andReturn();

        //then
        final ErrorResponse expectedResponse = new ErrorResponse("존재하지 않는 회원입니다.");
        final ErrorResponse response = jsonToClass(mvcResult, new TypeReference<>() {
        });
        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    void 골룸_생성_시_골룸_투두의_시작_날짜보다_종료_날짜가_빠른_경우() throws Exception {
        //given
        final GoalRoomCreateRequest request = new GoalRoomCreateRequest(1L, "name",
                20, new GoalRoomTodoRequest("content", TEN_DAY_LATER, TODAY),
                new ArrayList<>(List.of(new GoalRoomRoadmapNodeRequest(1L, 10, TODAY, TEN_DAY_LATER))));
        final String jsonRequest = objectMapper.writeValueAsString(request);
        doThrow(new BadRequestException("시작일은 종료일보다 후일 수 없습니다."))
                .when(goalRoomService)
                .create(any(), any());

        //when
        final MvcResult mvcResult = 골룸_생성(jsonRequest, status().isBadRequest())
                .andReturn();

        //then
        final ErrorResponse expectedResponse = new ErrorResponse("시작일은 종료일보다 후일 수 없습니다.");
        final ErrorResponse response = jsonToClass(mvcResult, new TypeReference<>() {
        });
        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    void 골룸_생성_시_골룸_투두의_시작_날짜가_오늘보다_전일_경우() throws Exception {
        //given
        final GoalRoomCreateRequest request = new GoalRoomCreateRequest(1L, "name",
                20, new GoalRoomTodoRequest("content", TODAY.minusDays(10), TEN_DAY_LATER),
                new ArrayList<>(List.of(new GoalRoomRoadmapNodeRequest(1L, 10, TODAY, TEN_DAY_LATER))));
        final String jsonRequest = objectMapper.writeValueAsString(request);
        doThrow(new BadRequestException("시작일은 오늘보다 전일 수 없습니다."))
                .when(goalRoomService)
                .create(any(), any());

        //when
        final MvcResult mvcResult = 골룸_생성(jsonRequest, status().isBadRequest())
                .andReturn();

        //then
        final ErrorResponse expectedResponse = new ErrorResponse("시작일은 오늘보다 전일 수 없습니다.");
        final ErrorResponse response = jsonToClass(mvcResult, new TypeReference<>() {
        });
        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    void 골룸_생성_시_골룸_노드의_시작_날짜보다_종료_날짜가_빠른_경우() throws Exception {
        //given
        final GoalRoomCreateRequest request = new GoalRoomCreateRequest(1L, "name",
                20, new GoalRoomTodoRequest("content", TODAY, TEN_DAY_LATER),
                new ArrayList<>(List.of(new GoalRoomRoadmapNodeRequest(1L, 10, TEN_DAY_LATER, TODAY))));
        final String jsonRequest = objectMapper.writeValueAsString(request);
        doThrow(new BadRequestException("시작일은 종료일보다 후일 수 없습니다."))
                .when(goalRoomService)
                .create(any(), any());

        //when
        final MvcResult mvcResult = 골룸_생성(jsonRequest, status().isBadRequest())
                .andReturn();

        //then
        final ErrorResponse expectedResponse = new ErrorResponse("시작일은 종료일보다 후일 수 없습니다.");
        final ErrorResponse response = jsonToClass(mvcResult, new TypeReference<>() {
        });
        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    void 골룸_생성_시_골룸_노드의_시작_날짜가_오늘보다_전일_경우() throws Exception {
        //given
        final GoalRoomCreateRequest request = new GoalRoomCreateRequest(1L, "name",
                20, new GoalRoomTodoRequest("content", TODAY, TEN_DAY_LATER),
                new ArrayList<>(List.of(new GoalRoomRoadmapNodeRequest(1L, 10, TODAY.minusDays(10), TEN_DAY_LATER))));
        final String jsonRequest = objectMapper.writeValueAsString(request);
        doThrow(new BadRequestException("시작일은 오늘보다 전일 수 없습니다."))
                .when(goalRoomService)
                .create(any(), any());

        //when
        final MvcResult mvcResult = 골룸_생성(jsonRequest, status().isBadRequest())
                .andReturn();

        //then
        final ErrorResponse expectedResponse = new ErrorResponse("시작일은 오늘보다 전일 수 없습니다.");
        final ErrorResponse response = jsonToClass(mvcResult, new TypeReference<>() {
        });
        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    void 골룸_생성_시_골룸_노드의_인증_횟수가_0보다_작을_경우() throws Exception {
        //given
        final GoalRoomCreateRequest request = new GoalRoomCreateRequest(1L, "name",
                20, new GoalRoomTodoRequest("content", TODAY, TEN_DAY_LATER),
                new ArrayList<>(List.of(new GoalRoomRoadmapNodeRequest(1L, 0, TODAY, TEN_DAY_LATER))));
        final String jsonRequest = objectMapper.writeValueAsString(request);
        doThrow(new BadRequestException("골름 노드의 인증 횟수는 0보다 커야합니다."))
                .when(goalRoomService)
                .create(any(), any());

        //when
        final MvcResult mvcResult = 골룸_생성(jsonRequest, status().isBadRequest())
                .andReturn();

        //then
        final ErrorResponse expectedResponse = new ErrorResponse("골름 노드의 인증 횟수는 0보다 커야합니다.");
        final ErrorResponse response = jsonToClass(mvcResult, new TypeReference<>() {
        });
        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    void 골룸_생성_시_골룸_노드의_인증_횟수가_기간보다_클_경우() throws Exception {
        //given
        final GoalRoomCreateRequest request = new GoalRoomCreateRequest(1L, "name",
                20, new GoalRoomTodoRequest("content", TODAY, TEN_DAY_LATER),
                new ArrayList<>(List.of(new GoalRoomRoadmapNodeRequest(1L, 11, TODAY, TEN_DAY_LATER))));
        final String jsonRequest = objectMapper.writeValueAsString(request);
        doThrow(new BadRequestException("골름 노드의 인증 횟수가 설정 기간보다 클 수 없습니다."))
                .when(goalRoomService)
                .create(any(), any());

        //when
        final MvcResult mvcResult = 골룸_생성(jsonRequest, status().isBadRequest())
                .andReturn();

        //then
        final ErrorResponse expectedResponse = new ErrorResponse("골름 노드의 인증 횟수가 설정 기간보다 클 수 없습니다.");
        final ErrorResponse response = jsonToClass(mvcResult, new TypeReference<>() {
        });
        assertThat(response).isEqualTo(expectedResponse);
    }

    private ResultActions 골룸_생성(final String jsonRequest, final ResultMatcher result) throws Exception {
        return mockMvc.perform(post(API_PREFIX + "/goal-rooms")
                        .content(jsonRequest)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .contextPath(API_PREFIX))
                .andExpect(result)
                .andDo(print());
    }

    private List<FieldDescription> makeSuccessRequestFieldDescription() {
        return List.of(
                new FieldDescription("roadmapContentId", "로드맵 컨텐츠 id"),
                new FieldDescription("name", "골룸 이름", "- 길이 : 1 ~ 40"),
                new FieldDescription("limitedMemberCount", "최대 제한 인원", "- 길이 : 1 ~ 20"),
                new FieldDescription("goalRoomTodo", "최초 골룸 투두"),
                new FieldDescription("goalRoomTodo.content", "골룸 투두 컨텐츠", "- 길이 : 1 ~ 250"),
                new FieldDescription("goalRoomTodo.startDate", "골룸 투두 시작일", "- yyMMdd 형식"),
                new FieldDescription("goalRoomTodo.endDate", "골룸 투두 종료일", "- yyMMdd 형식"),
                new FieldDescription("goalRoomRoadmapNodeRequests", "골룸 노드 정보"),
                new FieldDescription("goalRoomRoadmapNodeRequests[].roadmapNodeId", "설정할 로드맵 노드의 id"),
                new FieldDescription("goalRoomRoadmapNodeRequests[].checkCount", "골룸 노드의 인증 횟수"),
                new FieldDescription("goalRoomRoadmapNodeRequests[].startDate", "골룸 노드의 시작일", "- yyMMdd 형식"),
                new FieldDescription("goalRoomRoadmapNodeRequests[].endDate", "골룸 노드의 종료일", "- yyMMdd 형식")
        );
    }
}
