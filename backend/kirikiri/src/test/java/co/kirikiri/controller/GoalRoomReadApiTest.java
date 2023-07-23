package co.kirikiri.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.kirikiri.controller.helper.ControllerTestHelper;
import co.kirikiri.domain.goalroom.GoalRoomStatus;
import co.kirikiri.exception.AuthenticationException;
import co.kirikiri.service.GoalRoomService;
import co.kirikiri.service.dto.PageResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomForListResponse;
import co.kirikiri.service.dto.member.response.MemberResponse;
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
    private GoalRoomService goalRoomService;

    @Test
    void 사용자의_골룸_목록을_조회한다() throws Exception {
        // given
        final PageResponse<GoalRoomForListResponse> 골룸_페이지_응답 = 골룸_페이지_응답을_생성한다();
        given(goalRoomService.findMemberGoalRooms(anyLong(), any()))
                .willReturn(골룸_페이지_응답);

        // when
        final String 응답값 = mockMvc.perform(
                        get(API_PREFIX + "/goal-rooms")
                                .param("member", "1")
                                .param("page", "1")
                                .param("size", "10")
                                .header("Authorization", "Bearer AccessToken")
                                .contextPath(API_PREFIX))
                .andExpect(status().isOk())
                .andDo(
                        documentationResultHandler.document(
                                queryParameters(
                                        parameterWithName("member").description("멤버 아이디"),
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
                                        fieldWithPath("data[0].goalRoomLeader.name").description("골룸 리더의 닉네임"),
                                        fieldWithPath("data[0].status").description(
                                                "골룸의 상태(RECRUITING, RUNNING, COMPLETED"))
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

    @Test
    void 사용자_골룸_목록_조회시_유효하지_않은_사용자_아이디를_보내면_예외가_발생한다() throws Exception {
        // given
        when(goalRoomService.findMemberGoalRooms(anyLong(), any()))
                .thenThrow(new AuthenticationException("존재하지 않는 사용자입니다. memberId = 1L"));

        // when, then
        mockMvc.perform(
                        get(API_PREFIX + "/goal-rooms")
                                .param("member", "1")
                                .param("page", "1")
                                .param("size", "10")
                                .header("Authorization", "Bearer AccessToken")
                                .contextPath(API_PREFIX))
                .andExpectAll(
                        status().is4xxClientError(),
                        jsonPath("$.message").value("존재하지 않는 사용자입니다. memberId = 1L"))
                .andDo(documentationResultHandler.document(
                        queryParameters(
                                parameterWithName("member").description("잘못된 사용자 아이디"),
                                parameterWithName("page").description("타겟 페이지 (1부터 시작)"),
                                parameterWithName("size").description("한 페이지에서 받아올 로드맵의 수")),
                        responseFields(fieldWithPath("message").description("예외 메시지"))));
    }

    private PageResponse<GoalRoomForListResponse> 골룸_페이지_응답을_생성한다() {
        final GoalRoomForListResponse 첫번째_골룸_응담 = new GoalRoomForListResponse(1L, "골룸 이름1", 3, 6,
                LocalDateTime.of(2023, 7, 20, 13, 0, 0),
                LocalDate.of(2023, 8, 1), LocalDate.of(2023, 9, 1),
                new MemberResponse(1L, "황시진"), GoalRoomStatus.RECRUITING.name());

        final GoalRoomForListResponse 두번째_골룸_응답 = new GoalRoomForListResponse(2L, "골룸 이름2", 4, 10,
                LocalDateTime.of(2023, 7, 10, 13, 0, 0),
                LocalDate.of(2023, 7, 15), LocalDate.of(2023, 12, 31),
                new MemberResponse(2L, "시진이"), GoalRoomStatus.RUNNING.name());
        return new PageResponse<>(1, 2, List.of(첫번째_골룸_응담, 두번째_골룸_응답));
    }
}
