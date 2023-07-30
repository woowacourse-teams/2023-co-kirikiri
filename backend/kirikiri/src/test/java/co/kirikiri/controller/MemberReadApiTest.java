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
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.kirikiri.controller.helper.ControllerTestHelper;
import co.kirikiri.domain.goalroom.GoalRoomStatus;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.service.GoalRoomReadService;
import co.kirikiri.service.MemberService;
import co.kirikiri.service.dto.ErrorResponse;
import co.kirikiri.service.dto.goalroom.request.GoalRoomStatusTypeRequest;
import co.kirikiri.service.dto.goalroom.response.CheckFeedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomRoadmapNodeResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomRoadmapNodesResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomTodoResponse;
import co.kirikiri.service.dto.member.response.MemberGoalRoomForListResponse;
import co.kirikiri.service.dto.member.response.MemberGoalRoomResponse;
import co.kirikiri.service.dto.member.response.MemberResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@WebMvcTest(MemberController.class)
class MemberReadApiTest extends ControllerTestHelper {

    @MockBean
    MemberService memberService;

    @MockBean
    GoalRoomReadService goalRoomReadService;

    @Test
    void 사용자_단일_골룸을_조회한다() throws Exception {
        //given
        final MemberGoalRoomResponse expected = 사용자_골룸_조회_응답을_생성한다();
        when(goalRoomReadService.findMemberGoalRoom(any(), any()))
                .thenReturn(expected);

        //when
        final String response = mockMvc.perform(
                        get(API_PREFIX + "/members/goal-rooms/{goalRoomId}", 1L)
                                .header(AUTHORIZATION, String.format(BEARER_TOKEN_FORMAT, "access-token"))
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
                                        fieldWithPath("name").description("골룸 이름"),
                                        fieldWithPath("status").description("골룸 상태"),
                                        fieldWithPath("currentMemberCount").description("현재 골룸 참여자 수"),
                                        fieldWithPath("limitedMemberCount").description("골룸 참여 제한 인원 수"),
                                        fieldWithPath("period").description("골룸 진행 기간"),
                                        fieldWithPath("roadmapContentId").description("로드맵 컨텐츠 아이디"),
                                        fieldWithPath("goalRoomRoadmapNodes.hasFrontNode").description(
                                                "대시 보드에 표시된 골룸 노드 앞의 노드 존재 여부"),
                                        fieldWithPath("goalRoomRoadmapNodes.hasBackNode").description(
                                                "대시 보드에 표시된 골룸 노드 이후의 노드 존재 여부"),
                                        fieldWithPath("goalRoomRoadmapNodes.nodes[0].title").description(
                                                "골룸 로드맵 노드 제목"),
                                        fieldWithPath("goalRoomRoadmapNodes.nodes[0].startDate").description(
                                                "골룸 로드맵 노드 시작일"),
                                        fieldWithPath("goalRoomRoadmapNodes.nodes[0].endDate").description(
                                                "골룸 로드맵 노드 종료일"),
                                        fieldWithPath("goalRoomRoadmapNodes.nodes[0].checkCount").description(
                                                "골룸 로드맵 노드 최대 인증 횟수"),
                                        fieldWithPath("goalRoomTodos[0].id").description("골룸 투두 아이디"),
                                        fieldWithPath("goalRoomTodos[0].content").description("골룸 투두 내용"),
                                        fieldWithPath("goalRoomTodos[0].startDate").description("골룸 투두 시작일"),
                                        fieldWithPath("goalRoomTodos[0].endDate").description("골룸 투두 종료일"),
                                        fieldWithPath("checkFeeds[0].id").description("인증 피드 아이디"),
                                        fieldWithPath("checkFeeds[0].imageUrl").description("인증 피드 이미지 저장경로"),
                                        fieldWithPath("checkFeeds[0].description").description("인증 피드 본문")
                                )))
                .andReturn().getResponse()
                .getContentAsString();

        //then
        final MemberGoalRoomResponse memberGoalRoomResponses = objectMapper.readValue(response,
                new TypeReference<>() {
                });

        assertThat(memberGoalRoomResponses)
                .isEqualTo(expected);
    }

    @Test
    void 사용자_골룸_조회_시_유효하지_않은_골룸_아이디를_보내면_예외가_발생한다() throws Exception {
        //given
        when(goalRoomReadService.findMemberGoalRoom(any(), any()))
                .thenThrow(new NotFoundException("골룸 정보가 존재하지 않습니다. goalRoomId = 1"));

        //when
        final String response = mockMvc.perform(
                        get(API_PREFIX + "/members/goal-rooms/{goalRoomId}", 1L)
                                .header(AUTHORIZATION, String.format(BEARER_TOKEN_FORMAT, "access-token"))
                                .contextPath(API_PREFIX))
                .andExpect(status().isNotFound())
                .andDo(
                        documentationResultHandler.document(
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                                ),
                                pathParameters(
                                        parameterWithName("goalRoomId").description("골룸 아이디")
                                ),
                                responseFields(
                                        fieldWithPath("message").description("예외 메시지")
                                )))
                .andReturn().getResponse()
                .getContentAsString();

        //then
        final ErrorResponse errorResponse = objectMapper.readValue(response, new TypeReference<>() {
        });

        assertThat(errorResponse.message()).isEqualTo("골룸 정보가 존재하지 않습니다. goalRoomId = 1");
    }

    @Test
    void 사용자_참가_골룸_목록을_조회한다() throws Exception {
        //given
        final List<MemberGoalRoomForListResponse> expected = 사용자_골룸_목록_조회_응답을_생성한다();
        when(goalRoomReadService.findMemberGoalRoomsByStatusType(any(), any()))
                .thenReturn(expected);

        //when
        final String response = mockMvc.perform(
                        get(API_PREFIX + "/members/goal-rooms")
                                .param("statusCond", GoalRoomStatusTypeRequest.RUNNING.name())
                                .header(AUTHORIZATION, String.format(BEARER_TOKEN_FORMAT, "access-token"))
                                .contextPath(API_PREFIX))
                .andExpect(status().isOk())
                .andDo(
                        documentationResultHandler.document(
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                                ),
                                queryParameters(
                                        parameterWithName("statusCond").description("골룸 상태")
                                ),
                                responseFields(
                                        fieldWithPath("[0].goalRoomId").description("골룸 아이디"),
                                        fieldWithPath("[0].name").description("골룸 이름"),
                                        fieldWithPath("[0].goalRoomStatus").description("골룸 상태"),
                                        fieldWithPath("[0].currentMemberCount").description("현재 골룸 참여자 수"),
                                        fieldWithPath("[0].limitedMemberCount").description("골룸 참여 제한 인원 수"),
                                        fieldWithPath("[0].createdAt").description("골룸 생성 시간"),
                                        fieldWithPath("[0].startDate").description("골룸 시작 날짜"),
                                        fieldWithPath("[0].endDate").description("골룸 종료 날짜"),
                                        fieldWithPath("[0].goalRoomLeader.id").description("골룸 생성 사용자 아이디"),
                                        fieldWithPath("[0].goalRoomLeader.name").description("골룸 생성 사용자 닉네임")
                                )))
                .andReturn().getResponse()
                .getContentAsString();

        //then
        final List<MemberGoalRoomForListResponse> memberGoalRoomResponses = objectMapper.readValue(response,
                new TypeReference<>() {
                });

        assertThat(memberGoalRoomResponses)
                .isEqualTo(expected);
    }

    private MemberGoalRoomResponse 사용자_골룸_조회_응답을_생성한다() {
        return new MemberGoalRoomResponse("골룸 이름", "RUNNING", 15, 20, 100, 1L,
                new GoalRoomRoadmapNodesResponse(false, true, List.of(
                        new GoalRoomRoadmapNodeResponse("첫번째 골룸 노드 제목", LocalDate.of(2023, 1, 1),
                                LocalDate.of(2023, 1, 31), 15),
                        new GoalRoomRoadmapNodeResponse("두번째 골룸 노드 제목", LocalDate.of(2023, 2, 1),
                                LocalDate.of(2023, 2, 28), 14))),
                List.of(new GoalRoomTodoResponse(1L, "첫 번째 할일", LocalDate.of(2023, 1, 15), LocalDate.of(2023, 1, 31))),
                List.of(new CheckFeedResponse(1L, "imageUrl1", "인증 피드 설명 1"),
                        new CheckFeedResponse(2L, "imageUrl2", "인증 피드 설명 2"),
                        new CheckFeedResponse(3L, "imageUrl3", "인증 피드 설명 3"),
                        new CheckFeedResponse(4L, "imageUrl4", "인증 피드 설명 4")));

    }

    private List<MemberGoalRoomForListResponse> 사용자_골룸_목록_조회_응답을_생성한다() {
        return List.of(new MemberGoalRoomForListResponse(1L, "골룸 이름", GoalRoomStatus.RUNNING.name(),
                        15, 20, LocalDateTime.of(2023, 7, 1, 0, 0),
                        LocalDate.of(2023, 7, 15), LocalDate.of(2023, 8, 15),
                        new MemberResponse(1L, "황시진")),
                new MemberGoalRoomForListResponse(2L, "골룸 이름", GoalRoomStatus.RUNNING.name(),
                        15, 20, LocalDateTime.of(2023, 7, 5, 0, 0),
                        LocalDate.of(2023, 7, 8), LocalDate.of(2023, 8, 1),
                        new MemberResponse(2L, "시진이"))
        );
    }
}
