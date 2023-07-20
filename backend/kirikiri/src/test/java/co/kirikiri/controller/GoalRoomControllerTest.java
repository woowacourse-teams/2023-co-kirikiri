package co.kirikiri.controller;

import co.kirikiri.controller.helper.ControllerTestHelper;
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
import org.springframework.restdocs.snippet.Attributes;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GoalRoomController.class)
class GoalRoomControllerTest extends ControllerTestHelper {

    @MockBean
    private GoalRoomService goalRoomService;
    @MockBean
    private AuthService authService;

    private static final String IDENTIFIER = "identifier1";

    @Test
    void 정상적으로_골룸을_생성한다() throws Exception {
        //given
        final GoalRoomCreateRequest request = new GoalRoomCreateRequest(1L, "name",
                20, new GoalRoomTodoRequest("content", LocalDate.MIN, LocalDate.MAX),
                new ArrayList<>(List.of(new GoalRoomRoadmapNodeRequest(1L, 10, LocalDate.MIN, LocalDate.MAX))));

        given(goalRoomService.create(any(), any()))
                .willReturn(1L);
        given(authService.findIdentifierByToken(anyString()))
                .willReturn(IDENTIFIER);
        final String jsonRequest = objectMapper.writeValueAsString(request);

        //when
        final MvcResult mvcResult = 골룸_생성(jsonRequest, status().isCreated())
                .andDo(
                        documentationResultHandler.document(
                                requestFields(
                                        fieldWithPath("roadmapContentId").description("로드맵 컨텐츠 id"),
                                        fieldWithPath("name").description("골룸 이름")
                                                .attributes(new Attributes.Attribute(RESTRICT, "- 길이 : 1 ~ 40  ")),
                                        fieldWithPath("limitedMemberCount").description("최대 제한 인원")
                                                .attributes(new Attributes.Attribute(RESTRICT, "- 길이 : 1 ~ 20  ")),
                                        fieldWithPath("goalRoomTodo").description("최초 골룸 투두"),
                                        fieldWithPath("goalRoomTodo.content").description("골룸 투두 컨텐츠")
                                                .attributes(new Attributes.Attribute(RESTRICT, "- 길이 : 1 ~ 250 ")),
                                        fieldWithPath("goalRoomTodo.startDate").description("골룸 투두 시작일")
                                                .attributes(new Attributes.Attribute(RESTRICT, "- yyMMdd 형식 ")),
                                        fieldWithPath("goalRoomTodo.endDate").description("골룸 투두 종료일")
                                                .attributes(new Attributes.Attribute(RESTRICT, "- yyMMdd 형식 ")),
                                        fieldWithPath("goalRoomRoadmapNodeRequests").description("골룸 노드 정보"),
                                        fieldWithPath("goalRoomRoadmapNodeRequests[].roadmapNodeId").description("설정할 로드맵 노드의 id"),
                                        fieldWithPath("goalRoomRoadmapNodeRequests[].checkCount").description("- 골룸 노드의 인증 횟수"),
                                        fieldWithPath("goalRoomRoadmapNodeRequests[].startDate").description("골룸 노드의 시작일")
                                                .attributes(new Attributes.Attribute(RESTRICT, "- yyMMdd 형식 ")),
                                        fieldWithPath("goalRoomRoadmapNodeRequests[].endDate").description("골룸 노드의 종료일")
                                                .attributes(new Attributes.Attribute(RESTRICT, "- yyMMdd 형식 "))
                                )
                        )
                )
                .andReturn();


        //then
        assertThat(mvcResult.getResponse().getHeader("Location")).isEqualTo("/api/goal-rooms/" + 1);
    }

    @Test
    void 골룸_생성_시_로드맵_컨텐츠_id가_null일_경우() throws Exception {
        //given
        final GoalRoomCreateRequest request = new GoalRoomCreateRequest(null, "name",
                20, new GoalRoomTodoRequest("content", LocalDate.MIN, LocalDate.MAX),
                new ArrayList<>(List.of(new GoalRoomRoadmapNodeRequest(1L, 10, LocalDate.MIN, LocalDate.MAX))));
        final String jsonRequest = objectMapper.writeValueAsString(request);

        //when
        final MvcResult mvcResult = 골룸_생성(jsonRequest, status().isBadRequest())
                .andReturn();

        //then
        final ErrorResponse expectedResponse = new ErrorResponse("로드맵 컨텐츠 아이디는 빈 값일 수 없습니다.");
        final List<ErrorResponse> responses = jsonToClass(mvcResult, new TypeReference<>() {
        });

        assertThat(responses).usingRecursiveComparison()
                .isEqualTo(List.of(expectedResponse));
    }

    @Test
    void 골룸_생성_시_골룸_이름이_공백일_경우() throws Exception {
        //given
        final GoalRoomCreateRequest request = new GoalRoomCreateRequest(1L, " ",
                20, new GoalRoomTodoRequest("content", LocalDate.MIN, LocalDate.MAX),
                new ArrayList<>(List.of(new GoalRoomRoadmapNodeRequest(1L, 10, LocalDate.MIN, LocalDate.MAX))));
        final String jsonRequest = objectMapper.writeValueAsString(request);

        //when
        final MvcResult mvcResult = 골룸_생성(jsonRequest, status().isBadRequest())
                .andReturn();

        //then
        final ErrorResponse expectedResponse = new ErrorResponse("골룸 이름을 빈 값일 수 없습니다.");
        final List<ErrorResponse> responses = jsonToClass(mvcResult, new TypeReference<>() {
        });

        assertThat(responses).usingRecursiveComparison()
                .isEqualTo(List.of(expectedResponse));
    }

    @Test
    void 골룸_생성_시_골룸_제한_인원이_null일_경우() throws Exception {
        //given
        final GoalRoomCreateRequest request = new GoalRoomCreateRequest(1L, "name",
                null, new GoalRoomTodoRequest("content", LocalDate.MIN, LocalDate.MAX),
                new ArrayList<>(List.of(new GoalRoomRoadmapNodeRequest(1L, 10, LocalDate.MIN, LocalDate.MAX))));
        final String jsonRequest = objectMapper.writeValueAsString(request);

        //when
        final MvcResult mvcResult = 골룸_생성(jsonRequest, status().isBadRequest())
                .andReturn();

        //then
        final ErrorResponse expectedResponse = new ErrorResponse("골룸 제한 인원은 빈 값일 수 없습니다.");
        final List<ErrorResponse> responses = jsonToClass(mvcResult, new TypeReference<>() {
        });

        assertThat(responses).usingRecursiveComparison()
                .isEqualTo(List.of(expectedResponse));
    }

    @Test
    void 골룸_생성_시_투두_컨텐츠가_공백일_경우() throws Exception {
        //given
        final GoalRoomCreateRequest request = new GoalRoomCreateRequest(1L, "name",
                20, new GoalRoomTodoRequest("  ", LocalDate.MIN, LocalDate.MAX),
                new ArrayList<>(List.of(new GoalRoomRoadmapNodeRequest(1L, 10, LocalDate.MIN, LocalDate.MAX))));
        final String jsonRequest = objectMapper.writeValueAsString(request);

        //when
        final MvcResult mvcResult = 골룸_생성(jsonRequest, status().isBadRequest())
                .andReturn();

        //then
        final ErrorResponse expectedResponse = new ErrorResponse("투두의 컨텐츠는 빈 값일 수 없습니다.");
        final List<ErrorResponse> responses = jsonToClass(mvcResult, new TypeReference<>() {
        });

        assertThat(responses).usingRecursiveComparison()
                .isEqualTo(List.of(expectedResponse));
    }

    @Test
    void 골룸_생성_시_로드맵_노드_아이디가_공백일_경우() throws Exception {
        //given
        final GoalRoomCreateRequest request = new GoalRoomCreateRequest(1L, "name",
                20, new GoalRoomTodoRequest("content", LocalDate.MIN, LocalDate.MAX),
                new ArrayList<>(List.of(new GoalRoomRoadmapNodeRequest(null, 10, LocalDate.MIN, LocalDate.MAX))));
        final String jsonRequest = objectMapper.writeValueAsString(request);

        //when
        final MvcResult mvcResult = 골룸_생성(jsonRequest, status().isBadRequest())
                .andReturn();

        //then
        final ErrorResponse expectedResponse = new ErrorResponse("로드맵 노드 아이디는 빈 값일 수 없습니다.");
        final List<ErrorResponse> responses = jsonToClass(mvcResult, new TypeReference<>() {
        });

        assertThat(responses).usingRecursiveComparison()
                .isEqualTo(List.of(expectedResponse));
    }

    @Test
    void 골룸_생성_시_로드맵_노드_인증_횟수가_공백일_경우() throws Exception {
        //given
        final GoalRoomCreateRequest request = new GoalRoomCreateRequest(1L, "name",
                20, new GoalRoomTodoRequest("content", LocalDate.MIN, LocalDate.MAX),
                new ArrayList<>(List.of(new GoalRoomRoadmapNodeRequest(1L, null, LocalDate.MIN, LocalDate.MAX))));
        final String jsonRequest = objectMapper.writeValueAsString(request);

        //when
        final MvcResult mvcResult = 골룸_생성(jsonRequest, status().isBadRequest())
                .andReturn();

        //then
        final ErrorResponse expectedResponse = new ErrorResponse("인증 횟수는 빈 값일 수 없습니다.");
        final List<ErrorResponse> responses = jsonToClass(mvcResult, new TypeReference<>() {
        });

        assertThat(responses).usingRecursiveComparison()
                .isEqualTo(List.of(expectedResponse));
    }

    @Test
    void 골룸_생성_시_로드맵이_존재하지_않을_경우() throws Exception {
        //given
        final GoalRoomCreateRequest request = new GoalRoomCreateRequest(1L, "name",
                20, new GoalRoomTodoRequest("content", LocalDate.MIN, LocalDate.MAX),
                new ArrayList<>(List.of(new GoalRoomRoadmapNodeRequest(1L, 20, LocalDate.MIN, LocalDate.MAX))));
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
                20, new GoalRoomTodoRequest("content", LocalDate.MIN, LocalDate.MAX),
                new ArrayList<>(List.of(new GoalRoomRoadmapNodeRequest(1L, 20, LocalDate.MIN, LocalDate.MAX))));
        final String jsonRequest = objectMapper.writeValueAsString(request);
        doThrow(new BadRequestException("모든 노드에 대해 기간이 설정 돼야합니다."))
                .when(goalRoomService)
                .create(any(), any());

        //when
        final MvcResult mvcResult = 골룸_생성(jsonRequest, status().isBadRequest())
                .andReturn();

        //then
        final ErrorResponse expectedResponse = new ErrorResponse("모든 노드에 대해 기간이 설정 돼야합니다.");
        final ErrorResponse response = jsonToClass(mvcResult, new TypeReference<>() {
        });
        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    void 골룸_생성_시_로드맵에_존재하지_않는_노드일_경우() throws Exception {
        //given
        final GoalRoomCreateRequest request = new GoalRoomCreateRequest(1L, "name",
                20, new GoalRoomTodoRequest("content", LocalDate.MIN, LocalDate.MAX),
                new ArrayList<>(List.of(new GoalRoomRoadmapNodeRequest(1L, 20, LocalDate.MIN, LocalDate.MAX))));
        final String jsonRequest = objectMapper.writeValueAsString(request);
        doThrow(new NotFoundException("로드맵에 존재하지 않는 노드입니다"))
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
                20, new GoalRoomTodoRequest("content", LocalDate.MIN, LocalDate.MAX),
                new ArrayList<>(List.of(new GoalRoomRoadmapNodeRequest(1L, 20, LocalDate.MIN, LocalDate.MAX))));
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

    private ResultActions 골룸_생성(final String jsonRequest, final ResultMatcher result) throws Exception {
        return mockMvc.perform(post(API_PREFIX + "/goal-rooms")
                        .content(jsonRequest)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .contextPath(API_PREFIX))
                .andExpect(result)
                .andDo(print());
    }
}
