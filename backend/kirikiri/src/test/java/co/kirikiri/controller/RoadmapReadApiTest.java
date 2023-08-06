package co.kirikiri.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
import co.kirikiri.domain.roadmap.RoadmapDifficulty;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.service.RoadmapCreateService;
import co.kirikiri.service.RoadmapReadService;
import co.kirikiri.service.dto.CustomScrollRequest;
import co.kirikiri.service.dto.ErrorResponse;
import co.kirikiri.service.dto.member.response.MemberResponse;
import co.kirikiri.service.dto.roadmap.request.RoadmapFilterTypeRequest;
import co.kirikiri.service.dto.roadmap.response.MemberRoadmapResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapCategoryResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapContentResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapForListResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapGoalRoomResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapNodeResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapTagResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(RoadmapController.class)
class RoadmapReadApiTest extends ControllerTestHelper {

    private final LocalDateTime 오늘 = LocalDateTime.now();

    @MockBean
    private RoadmapReadService roadmapReadService;

    @MockBean
    private RoadmapCreateService roadmapCreateService;

    @Test
    void 단일_로드맵_정보를_조회한다() throws Exception {
        //given
        final RoadmapResponse expectedResponse = 단일_로드맵_조회에_대한_응답();
        when(roadmapReadService.findRoadmap(anyLong()))
                .thenReturn(expectedResponse);

        //when
        final MvcResult response = mockMvc.perform(get(API_PREFIX + "/roadmaps/{roadmapId}", 1L)
                        .contextPath(API_PREFIX))
                .andExpect(status().isOk())
                .andDo(documentationResultHandler.document(
                        pathParameters(
                                parameterWithName("roadmapId").description("로드맵 아이디")
                        ),
                        responseFields(
                                fieldWithPath("roadmapId").description("로드맵 아이디"),
                                fieldWithPath("category.id").description("로드맵 카테고리 아이디"),
                                fieldWithPath("category.name").description("로드맵 카테고리 이름"),
                                fieldWithPath("roadmapTitle").description("로드맵 제목"),
                                fieldWithPath("introduction").description("로드맵 소개글"),
                                fieldWithPath("difficulty").description("로드맵 난이도"),
                                fieldWithPath("recommendedRoadmapPeriod").description("로드맵 추천 기간"),
                                fieldWithPath("createdAt").description("로드맵 생성 시간"),
                                fieldWithPath("creator.id").description("로드맵 크리에이터 아이디"),
                                fieldWithPath("creator.imageUrl").description("로드맵 크리에이터 프로필 이미지 주소"),
                                fieldWithPath("creator.name").description("로드맵 크리에이터 닉네임"),
                                fieldWithPath("content.id").description("로드맵 컨텐츠 아이디"),
                                fieldWithPath("content.content").description("로드맵 컨텐츠 본문"),
                                fieldWithPath("content.nodes[0].id").description("로드맵 노드 아이디"),
                                fieldWithPath("content.nodes[0].title").description("로드맵 노드 제목"),
                                fieldWithPath("content.nodes[0].description").description("로드맵 노드 본문"),
                                fieldWithPath("content.nodes[0].imageUrls[0]").description("로드맵 노드 이미지 파일 경로"),
                                fieldWithPath("tags[0].id").description("로드맵 태그 아이디"),
                                fieldWithPath("tags[0].name").description("로드맵 태그 이름"),
                                fieldWithPath("recruitedGoalRoomNumber").description("해당 로드맵에서 모집 중인 골룸 개수"),
                                fieldWithPath("runningGoalRoomNumber").description("해당 로드맵에서 진행 중인 골룸 개수"),
                                fieldWithPath("completedGoalRoomNumber").description("해당 로드맵에서 완료된 골룸 개수"))))
                .andReturn();

        //then
        final RoadmapResponse roadmapResponse = jsonToClass(response, new TypeReference<>() {
        });

        assertThat(roadmapResponse)
                .isEqualTo(expectedResponse);
    }

    @Test
    void 존재하지_않는_로드맵_아이디로_요청_시_예외를_반환한다() throws Exception {
        // given
        when(roadmapReadService.findRoadmap(anyLong())).thenThrow(
                new NotFoundException("존재하지 않는 로드맵입니다. roadmapId = 1"));

        // when
        // then
        mockMvc.perform(get(API_PREFIX + "/roadmaps/{roadmapId}", 1L)
                        .contextPath(API_PREFIX))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("존재하지 않는 로드맵입니다. roadmapId = 1"))
                .andDo(documentationResultHandler.document(
                        pathParameters(
                                parameterWithName("roadmapId").description("로드맵 아이디")),
                        responseFields(
                                fieldWithPath("message").description("예외 메세지")
                        )));
    }

    @Test
    void 로드맵_목록을_조건에_따라_조회한다() throws Exception {
        // given
        final List<RoadmapForListResponse> expected = 로드맵_리스트_응답을_생성한다();
        when(roadmapReadService.findRoadmapsByFilterType(any(), any(), any()))
                .thenReturn(expected);

        // when
        final String response = mockMvc.perform(
                        get(API_PREFIX + "/roadmaps")
                                .param("categoryId", "1")
                                .param("filterCond", RoadmapFilterTypeRequest.LATEST.name())
                                .param("lastCreatedAt", String.valueOf(LocalDateTime.now()))
                                .param("size", "10")
                                .contextPath(API_PREFIX))
                .andExpect(status().isOk())
                .andDo(
                        documentationResultHandler.document(
                                queryParameters(
                                        parameterWithName("categoryId").description("카테고리 아이디(미전송 시 전체 조회)")
                                                .optional(),
                                        parameterWithName("filterCond").description(
                                                        "필터 조건(GOAL_ROOM_COUNT, LATEST, PARTICIPANT_COUNT, REVIEW_RATE)")
                                                .optional(),
                                        parameterWithName("lastCreatedAt")
                                                .description("filterCond의 LATEST와 함께 사용하는 옵션 +" + "\n"
                                                        + "이전에 응답받은 createdAt의 값 중 가장 작은 값, 첫 요청의 경우 보내지 않음 +" + "\n")
                                                .optional(),
                                        parameterWithName("lastGoalRoomCount")
                                                .description("filterCond의 GOAL_ROOM_COUNT와 함께 사용하는 옵션 +" + "\n"
                                                        + "이전에 응답받은 goalRoomCount 값 중 가장 작은 값, 첫 요청의 경우 보내지 않음 +"
                                                        + "\n")
                                                .optional(),
                                        parameterWithName("lastParticipatedCount")
                                                .description("filterCond의 PARTICIPANT_COUNT와 함께 사용하는 옵션 +" + "\n"
                                                        + "이전에 응답받은 participantCount 값 중 가장 작은 값, 첫 요청의 경우 보내지 않음 +"
                                                        + "\n")
                                                .optional(),
                                        parameterWithName("lastReviewRate")
                                                .description("filterCond의 REVIEW_RATE와 함께 사용하는 옵션 +" + "\n"
                                                        + "이전에 응답받은 reviewRate 값 중 가장 작은 값, 첫 요청의 경우 보내지 않음 +" + "\n")
                                                .optional(),
                                        parameterWithName("size").description("한 페이지에서 받아올 로드맵의 수")),
                                responseFields(
                                        fieldWithPath("[0].roadmapId").description("로드맵 아이디"),
                                        fieldWithPath("[0].roadmapTitle").description("로드맵 제목"),
                                        fieldWithPath("[0].introduction").description("로드맵 소개글"),
                                        fieldWithPath("[0].difficulty").description("로드맵 난이도"),
                                        fieldWithPath("[0].recommendedRoadmapPeriod").description("로드맵 추천 기간"),
                                        fieldWithPath("[0].createdAt").description("로드맵 생성 시간"),
                                        fieldWithPath("[0].creator.id").description("로드맵 크리에이터 아이디"),
                                        fieldWithPath("[0].creator.name").description("로드맵 크리에이터 이름"),
                                        fieldWithPath("[0].category.id").description("로드맵 카테고리 아이디"),
                                        fieldWithPath("[0].category.name").description("로드맵 카테고리 이름"),
                                        fieldWithPath("[0].tags[0].id").description("로드맵 태그 아이디"),
                                        fieldWithPath("[0].tags[0].name").description("로드맵 태그 이름"))))
                .andReturn().getResponse()
                .getContentAsString();

        // then
        final List<RoadmapForListResponse> roadmapForListResponses = objectMapper.readValue(response,
                new TypeReference<>() {
                });

        assertThat(roadmapForListResponses)
                .isEqualTo(expected);
    }

    @Test
    void 로드맵_목록_조회시_유효하지_않은_카테고리_아이디를_보내면_예외가_발생한다() throws Exception {
        // given
        when(roadmapReadService.findRoadmapsByFilterType(any(), any(), any())).thenThrow(
                new NotFoundException("존재하지 않는 카테고리입니다. categoryId = 1L"));

        // when
        final String response = mockMvc.perform(
                        get(API_PREFIX + "/roadmaps")
                                .param("categoryId", "1")
                                .param("filterCond", RoadmapFilterTypeRequest.LATEST.name())
                                .param("size", "10")
                                .contextPath(API_PREFIX))
                .andExpectAll(
                        status().is4xxClientError(),
                        jsonPath("$.message").value("존재하지 않는 카테고리입니다. categoryId = 1L"))
                .andDo(documentationResultHandler.document(
                        queryParameters(
                                parameterWithName("categoryId").description("잘못된 카테고리 아이디"),
                                parameterWithName("filterCond").description(
                                                "필터 조건(GOAL_ROOM_COUNT, LATEST, PARTICIPANT_COUNT, REVIEW_RATE)")
                                        .optional(),
                                parameterWithName("size").description("한 페이지에서 받아올 로드맵의 수")),
                        responseFields(fieldWithPath("message").description("예외 메시지"))))
                .andReturn().getResponse()
                .getContentAsString();

        // then
        final ErrorResponse errorResponse = objectMapper.readValue(response, ErrorResponse.class);
        final ErrorResponse expected = new ErrorResponse("존재하지 않는 카테고리입니다. categoryId = 1L");
        assertThat(errorResponse)
                .isEqualTo(expected);
    }

    @Test
    void 로드맵_목록_조회시_사이즈_값을_전송하지_않으면_예외가_발생한다() throws Exception {
        // when
        final String response = mockMvc.perform(
                        get(API_PREFIX + "/roadmaps")
                                .contextPath(API_PREFIX))
                .andExpectAll(
                        status().is4xxClientError(),
                        jsonPath("$[0].message").value("사이즈를 입력해 주세요."))
                .andDo(documentationResultHandler.document(
                        responseFields(fieldWithPath("[0].message").description("예외 메시지"))))
                .andReturn().getResponse()
                .getContentAsString();

        // then
        final List<ErrorResponse> errorResponse = objectMapper.readValue(response, new TypeReference<>() {
        });
        final ErrorResponse expected = new ErrorResponse("사이즈를 입력해 주세요.");
        assertThat(errorResponse.get(0))
                .isEqualTo(expected);
    }

    @Test
    void 로드맵_카테고리_목록을_조회한다() throws Exception {
        // given
        final List<RoadmapCategoryResponse> expected = 로드맵_카테고리_응답_리스트를_반환한다();
        when(roadmapReadService.findAllRoadmapCategories())
                .thenReturn(expected);

        // when
        final String response = mockMvc.perform(
                        get("/api/roadmaps/categories")
                                .contextPath(API_PREFIX))
                .andDo(
                        documentationResultHandler.document(
                                responseFields(
                                        fieldWithPath("[0].id").description("카테고리 아이디"),
                                        fieldWithPath("[0].name").description("카테고리 이름")
                                )
                        )
                )
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        final List<RoadmapCategoryResponse> roadmapCategoryResponses = objectMapper.readValue(response,
                new TypeReference<>() {
                });

        assertThat(roadmapCategoryResponses)
                .isEqualTo(expected);
    }

    @Test
    void 로드맵을_조건별로_검색한다() throws Exception {
        // given
        final List<RoadmapForListResponse> expected = 로드맵_리스트_응답을_생성한다();
        when(roadmapReadService.search(any(), any(), any()))
                .thenReturn(expected);

        // when
        final String response = mockMvc.perform(
                        get(API_PREFIX + "/roadmaps/search")
                                .param("roadmapTitle", "roadmap")
                                .param("creatorId", "1")
                                .param("tagName", "Java")
                                .param("filterCond", RoadmapFilterTypeRequest.LATEST.name())
                                .param("size", "10")
                                .contextPath(API_PREFIX))
                .andExpect(status().isOk())
                .andDo(
                        documentationResultHandler.document(
                                queryParameters(
                                        parameterWithName("roadmapTitle").description("로드맵 제목 검색어")
                                                .attributes(new Attributes.Attribute(RESTRICT, "- 길이: 1자 이상"))
                                                .optional(),
                                        parameterWithName("creatorId").description("크리에이터 아이디")
                                                .optional(),
                                        parameterWithName("tagName").description("로드맵 태그 이름")
                                                .attributes(new Attributes.Attribute(RESTRICT, "- 길이: 1자 이상"))
                                                .optional(),
                                        parameterWithName("filterCond").description(
                                                        "필터 조건(GOAL_ROOM_COUNT, LATEST, PARTICIPANT_COUNT, REVIEW_RATE)")
                                                .optional(),
                                        parameterWithName("filterCond").description(
                                                        "필터 조건(GOAL_ROOM_COUNT, LATEST, PARTICIPANT_COUNT, REVIEW_RATE)")
                                                .optional(),
                                        parameterWithName("lastCreatedAt")
                                                .description("filterCond의 LATEST와 함께 사용하는 옵션 +" + "\n"
                                                        + "이전에 응답받은 createdAt의 값 중 가장 작은 값, 첫 요청의 경우 보내지 않음 +" + "\n")
                                                .optional(),
                                        parameterWithName("lastGoalRoomCount")
                                                .description("filterCond의 GOAL_ROOM_COUNT와 함께 사용하는 옵션 +" + "\n"
                                                        + "이전에 응답받은 goalRoomCount 값 중 가장 작은 값, 첫 요청의 경우 보내지 않음 +"
                                                        + "\n")
                                                .optional(),
                                        parameterWithName("lastParticipatedCount")
                                                .description("filterCond의 PARTICIPANT_COUNT와 함께 사용하는 옵션 +" + "\n"
                                                        + "이전에 응답받은 participantCount 값 중 가장 작은 값, 첫 요청의 경우 보내지 않음 +"
                                                        + "\n")
                                                .optional(),
                                        parameterWithName("lastReviewRate")
                                                .description("filterCond의 REVIEW_RATE와 함께 사용하는 옵션 +" + "\n"
                                                        + "이전에 응답받은 reviewRate 값 중 가장 작은 값, 첫 요청의 경우 보내지 않음 +" + "\n")
                                                .optional(),
                                        parameterWithName("size").description("한 페이지에서 받아올 로드맵의 수")),
                                responseFields(
                                        fieldWithPath("[0].roadmapId").description("로드맵 아이디"),
                                        fieldWithPath("[0].roadmapTitle").description("로드맵 제목"),
                                        fieldWithPath("[0].introduction").description("로드맵 소개글"),
                                        fieldWithPath("[0].difficulty").description("로드맵 난이도"),
                                        fieldWithPath("[0].recommendedRoadmapPeriod").description("로드맵 추천 기간"),
                                        fieldWithPath("[0].createdAt").description("로드맵 생성 시간"),
                                        fieldWithPath("[0].creator.id").description("로드맵 크리에이터 아이디"),
                                        fieldWithPath("[0].creator.name").description("로드맵 크리에이터 이름"),
                                        fieldWithPath("[0].category.id").description("로드맵 카테고리 아이디"),
                                        fieldWithPath("[0].category.name").description("로드맵 카테고리 이름"),
                                        fieldWithPath("[0].tags[0].id").description("로드맵 태그 아이디"),
                                        fieldWithPath("[0].tags[0].name").description("로드맵 태그 이름"))))
                .andReturn().getResponse()
                .getContentAsString();

        // then
        final List<RoadmapForListResponse> roadmapForListResponses = objectMapper.readValue(response,
                new TypeReference<>() {
                });

        assertThat(roadmapForListResponses)
                .isEqualTo(expected);
    }

    @Test
    void 로드맵_검색시_사이즈_값을_전송하지_않으면_예외가_발생한다() throws Exception {
        // when
        final String response = mockMvc.perform(
                        get(API_PREFIX + "/roadmaps/search")
                                .contextPath(API_PREFIX))
                .andExpectAll(
                        status().is4xxClientError(),
                        jsonPath("$[0].message").value("사이즈를 입력해 주세요."))
                .andDo(documentationResultHandler.document(
                        responseFields(fieldWithPath("[0].message").description("예외 메시지"))))
                .andReturn().getResponse()
                .getContentAsString();

        // then
        final List<ErrorResponse> errorResponse = objectMapper.readValue(response, new TypeReference<>() {
        });
        final ErrorResponse expected = new ErrorResponse("사이즈를 입력해 주세요.");
        assertThat(errorResponse.get(0))
                .isEqualTo(expected);
    }

    @Test
    void 사용자가_생성한_로드맵을_조회한다() throws Exception {
        // given
        final List<MemberRoadmapResponse> expected = 사용자_로드맵_조회에_대한_응답을_생성한다();

        when(roadmapReadService.findAllMemberRoadmaps(any(), any()))
                .thenReturn(expected);

        // when
        final String response = mockMvc.perform(
                        get(API_PREFIX + "/roadmaps/me")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer <accessToken>")
                                .param("lastCreatedAt", "2023-07-21T12:08:50.406142")
                                .param("size", "10")
                                .contextPath(API_PREFIX))
                .andExpect(status().isOk())
                .andDo(
                        documentationResultHandler.document(
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).description("액세스 토큰")),
                                queryParameters(
                                        parameterWithName("lastCreatedAt").description(
                                                        "이전에 받아온 목록에서 가장 마지막 createdAt (처음에는 null)")
                                                .optional(),
                                        parameterWithName("size").description("한 페이지에서 받아올 로드맵의 수")),
                                responseFields(
                                        fieldWithPath("[0].roadmapId").description("로드맵 아이디"),
                                        fieldWithPath("[0].roadmapTitle").description("로드맵 제목"),
                                        fieldWithPath("[0].difficulty").description("로드맵 난이도"),
                                        fieldWithPath("[0].createdAt").description("로드맵 생성날짜"),
                                        fieldWithPath("[0].category.id").description("로드맵 카테고리 아이디"),
                                        fieldWithPath("[0].category.name").description("로드맵 카테고리 이름"))))
                .andReturn().getResponse()
                .getContentAsString();

        // then
        final List<MemberRoadmapResponse> memberRoadmapRespons = objectMapper.readValue(response,
                new TypeReference<>() {
                });
        assertThat(memberRoadmapRespons)
                .isEqualTo(expected);
    }

    @Test
    void 사용자가_생성한_로드맵을_조회할_때_존재하지_않는_회원이면_예외가_발생한다() throws Exception {
        // given
        when(roadmapReadService.findAllMemberRoadmaps(any(), any()))
                .thenThrow(new NotFoundException("존재하지 않는 회원입니다."));

        // when
        final String response = mockMvc.perform(
                        get(API_PREFIX + "/roadmaps/me")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer <accessToken>")
                                .param("lastCreatedAt", "2023-07-21T12:08:50.406142")
                                .param("size", "10")
                                .contextPath(API_PREFIX))
                .andExpectAll(
                        status().is4xxClientError(),
                        jsonPath("$.message").value("존재하지 않는 회원입니다."))
                .andDo(
                        documentationResultHandler.document(
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).description("액세스 토큰")),
                                queryParameters(
                                        parameterWithName("lastCreatedAt").description(
                                                        "이전에 받아온 목록에서 가장 마지막 createdAt (처음에는 null)")
                                                .optional(),
                                        parameterWithName("size").description("한 페이지에서 받아올 로드맵의 수")),
                                responseFields(fieldWithPath("message").description("예외 메시지"))))
                .andReturn().getResponse()
                .getContentAsString();

        // then
        final ErrorResponse errorResponse = objectMapper.readValue(response, ErrorResponse.class);
        final ErrorResponse expected = new ErrorResponse("존재하지 않는 회원입니다.");
        assertThat(errorResponse)
                .isEqualTo(expected);
    }

    @Test
    void 로드맵의_골룸_목록을_조건에_따라_조회한다() throws Exception {
        // given
        final List<RoadmapGoalRoomResponse> 골룸_페이지_응답 = 골룸_응답들을_생성한다();
        given(roadmapReadService.findRoadmapGoalRoomsByFilterType(any(), any(), any(CustomScrollRequest.class)))
                .willReturn(골룸_페이지_응답);

        // when
        final String 응답값 = mockMvc.perform(
                        get(API_PREFIX + "/roadmaps/{roadmapId}/goal-rooms", 1L)
                                .param("filterCond", RoadmapFilterTypeRequest.LATEST.name())
                                .param("lastCreatedAt", "2023-07-21T12:08:50.406142")
                                .param("size", "10")
                                .contextPath(API_PREFIX))
                .andExpect(status().isOk())
                .andDo(
                        documentationResultHandler.document(
                                pathParameters(
                                        parameterWithName("roadmapId").description("로드맵 아이디")),
                                queryParameters(
                                        parameterWithName("filterCond").description(
                                                "필터 조건(LATEST, PARTICIPATION_RATE)").optional(),
                                        parameterWithName("lastCreatedAt").description(
                                                "이전에 받아온 목록에서 가장 마지막 createdAt (처음에는 null)").optional(),
                                        parameterWithName("size").description("받아올 골룸의 수")),
                                responseFields(
                                        fieldWithPath("[0].goalRoomId").description("골룸 아이디"),
                                        fieldWithPath("[0].name").description("골룸 이름"),
                                        fieldWithPath("[0].currentMemberCount").description("현재 골룸에 참여한 인원 수"),
                                        fieldWithPath("[0].limitedMemberCount").description("골룸에 참여할 수 있는 제한 인원 수"),
                                        fieldWithPath("[0].createdAt").description("골룸 생성 날짜와 시간"),
                                        fieldWithPath("[0].startDate").description("골룸의 시작 날짜"),
                                        fieldWithPath("[0].endDate").description("골룸의 종료 날짜"),
                                        fieldWithPath("[0].goalRoomLeader.id").description("골룸 리더의 아이디"),
                                        fieldWithPath("[0].goalRoomLeader.name").description("골룸 리더의 닉네임"))
                        )
                )
                .andReturn().getResponse()
                .getContentAsString();

        // then
        final List<RoadmapGoalRoomResponse> 응답값으로_생성한_골룸_페이지 = objectMapper.readValue(응답값,
                new TypeReference<>() {
                });

        final List<RoadmapGoalRoomResponse> 예상되는_골룸_페이지_응답 = 골룸_응답들을_생성한다();
        assertThat(응답값으로_생성한_골룸_페이지)
                .usingRecursiveComparison()
                .isEqualTo(예상되는_골룸_페이지_응답);
    }

    @Test
    void 로드맵의_골룸_목록을_조건에_따라_조회할_때_로드맵이_존재하지_않으면_예외가_발생한다() throws Exception {
        // given
        given(roadmapReadService.findRoadmapGoalRoomsByFilterType(any(), any(), any(CustomScrollRequest.class)))
                .willThrow(new NotFoundException("존재하지 않는 로드맵입니다. roadmapId = 1"));

        // when
        final MvcResult 응답값 = mockMvc.perform(
                        get(API_PREFIX + "/roadmaps/{roadmapId}/goal-rooms", 1L)
                                .param("filterCond", RoadmapFilterTypeRequest.LATEST.name())
                                .param("lastCreatedAt", "2023-07-21T12:08:50.406142")
                                .param("size", "10")
                                .contextPath(API_PREFIX))
                .andExpectAll(
                        status().is4xxClientError(),
                        jsonPath("$.message").value("존재하지 않는 로드맵입니다. roadmapId = 1"))
                .andDo(
                        documentationResultHandler.document(
                                pathParameters(
                                        parameterWithName("roadmapId").description("로드맵 아이디")),
                                queryParameters(
                                        parameterWithName("filterCond").description(
                                                "필터 조건(LATEST, PARTICIPATION_RATE)").optional(),
                                        parameterWithName("lastCreatedAt").description(
                                                "이전에 받아온 목록에서 가장 마지막 createdAt (처음에는 null)").optional(),
                                        parameterWithName("size").description("받아올 골룸의 수")),
                                responseFields(fieldWithPath("message").description("예외 메시지")))
                )
                .andReturn();
        // then
        final ErrorResponse errorResponse = jsonToClass(응답값, new TypeReference<>() {
        });
        final ErrorResponse expected = new ErrorResponse("존재하지 않는 로드맵입니다. roadmapId = 1");
        assertThat(errorResponse)
                .isEqualTo(expected);
    }

    private RoadmapResponse 단일_로드맵_조회에_대한_응답() {
        final RoadmapCategoryResponse category = new RoadmapCategoryResponse(1, "운동");
        final MemberResponse creator = new MemberResponse(1, "닉네임", "profile-image-filepath");
        final List<RoadmapNodeResponse> nodes = List.of(
                new RoadmapNodeResponse(1L, "1번 노드", "1번 노드 설명", List.of("image1-filepath", "image2-filepath")),
                new RoadmapNodeResponse(2L, "2번 노드", "2번 노드 설명", Collections.emptyList())
        );
        final List<RoadmapTagResponse> tags = List.of(
                new RoadmapTagResponse(1L, "태그1"),
                new RoadmapTagResponse(2L, "태그2")
        );
        return new RoadmapResponse(1L, category, "제목", "소개글", creator,
                new RoadmapContentResponse(1L, "본문", nodes), "EASY", 100,
                오늘, tags, 10L, 10L, 10L);
    }

    private List<RoadmapForListResponse> 로드맵_리스트_응답을_생성한다() {
        final List<RoadmapTagResponse> tags = List.of(
                new RoadmapTagResponse(1L, "태그1"),
                new RoadmapTagResponse(2L, "태그2")
        );

        final RoadmapForListResponse roadmapResponse1 = new RoadmapForListResponse(1L, "로드맵 제목1", "로드맵 소개글1", "NORMAL",
                10, 오늘, new MemberResponse(1L, "코끼리"), new RoadmapCategoryResponse(1L, "여행"), tags);
        final RoadmapForListResponse roadmapResponse2 = new RoadmapForListResponse(2L, "로드맵 제목2", "로드맵 소개글2",
                "DIFFICULT", 7, 오늘, new MemberResponse(2L, "끼리코"), new RoadmapCategoryResponse(2L, "IT"), tags);
        return List.of(roadmapResponse1, roadmapResponse2);
    }

    private List<RoadmapCategoryResponse> 로드맵_카테고리_응답_리스트를_반환한다() {
        final RoadmapCategoryResponse category1 = new RoadmapCategoryResponse(1L, "어학");
        final RoadmapCategoryResponse category2 = new RoadmapCategoryResponse(2L, "IT");
        final RoadmapCategoryResponse category3 = new RoadmapCategoryResponse(3L, "시험");
        final RoadmapCategoryResponse category4 = new RoadmapCategoryResponse(4L, "운동");
        final RoadmapCategoryResponse category5 = new RoadmapCategoryResponse(5L, "게임");
        final RoadmapCategoryResponse category6 = new RoadmapCategoryResponse(6L, "음악");
        final RoadmapCategoryResponse category7 = new RoadmapCategoryResponse(7L, "라이프");
        final RoadmapCategoryResponse category8 = new RoadmapCategoryResponse(8L, "여가");
        final RoadmapCategoryResponse category9 = new RoadmapCategoryResponse(9L, "기타");
        return List.of(category1, category2, category3, category4, category5, category6, category7, category8,
                category9);
    }

    private List<MemberRoadmapResponse> 사용자_로드맵_조회에_대한_응답을_생성한다() {
        return List.of(
                new MemberRoadmapResponse(3L, "세 번째 로드맵", RoadmapDifficulty.DIFFICULT.name(), LocalDateTime.now(),
                        new RoadmapCategoryResponse(2L, "게임")),
                new MemberRoadmapResponse(2L, "두 번째 로드맵", RoadmapDifficulty.DIFFICULT.name(), LocalDateTime.now(),
                        new RoadmapCategoryResponse(1L, "여행")),
                new MemberRoadmapResponse(1L, "첫 번째 로드맵", RoadmapDifficulty.DIFFICULT.name(), LocalDateTime.now(),
                        new RoadmapCategoryResponse(1L, "여행")));
    }

    private List<RoadmapGoalRoomResponse> 골룸_응답들을_생성한다() {
        final RoadmapGoalRoomResponse roadmapGoalRoomResponse1 = new RoadmapGoalRoomResponse(1L, "골룸 이름1", 3, 6,
                LocalDateTime.of(2023, 7, 20, 13, 0, 0),
                LocalDate.now(), LocalDate.now().plusDays(100),
                new MemberResponse(1L, "황시진"));
        final RoadmapGoalRoomResponse roadmapGoalRoomResponse2 = new RoadmapGoalRoomResponse(2L, "골룸 이름2", 4, 10,
                LocalDateTime.of(2023, 7, 10, 13, 0, 0),
                LocalDate.now(), LocalDate.now().plusDays(100),
                new MemberResponse(2L, "시진이"));
        return List.of(roadmapGoalRoomResponse1, roadmapGoalRoomResponse2);
    }
}
