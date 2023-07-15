package co.kirikiri.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.kirikiri.controller.helper.RestDocsHelper;
import co.kirikiri.exception.BadRequestException;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.persistence.MemberRepository;
import co.kirikiri.service.RoadmapService;
import co.kirikiri.service.dto.roadmap.RoadmapDifficultyType;
import co.kirikiri.service.dto.roadmap.RoadmapNodesSaveRequest;
import co.kirikiri.service.dto.roadmap.RoadmapSaveRequest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultMatcher;

@WebMvcTest(value = RoadmapController.class)
class RoadmapControllerTest extends RestDocsHelper {

    @MockBean
    private RoadmapService roadmapService;

    // TODO: 제거
    @MockBean
    private MemberRepository memberRepository;

    @Test
    void 정상적으로_로드맵을_생성한다() throws Exception {
        // given
        final RoadmapSaveRequest request = makeRoadmapSaveRequest(1L, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodesSaveRequest("로드맵 1주차", "로드맵 1주차에는 알고리즘을 배울거에요.")));
        final String jsonRequest = objectMapper.writeValueAsString(request);

        given(roadmapService.create(any(), any()))
                .willReturn(1L);

        // expect
        requestCreateRoadmap(jsonRequest, status().isCreated());
    }

    @Test
    void 로드맵_생성시_유효하지_않은_카테고리_아이디를_입력하면_예외가_발생한다() throws Exception {
        // given
        final Long categoryId = 10L;
        final RoadmapSaveRequest request = makeRoadmapSaveRequest(categoryId, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodesSaveRequest("로드맵 1주차", "로드맵 1주차에는 알고리즘을 배울거에요.")));
        final String jsonRequest = objectMapper.writeValueAsString(request);

        given(roadmapService.create(any(), any()))
                .willThrow(new NotFoundException("존재하지 않는 카테고리입니다. categoryId=10"));

        // expect
        requestCreateRoadmap(jsonRequest, status().isNotFound());
    }

    @Test
    void 로드맵_생성시_카테고리_아이디를_입력하지_않으면_예외가_발생한다() throws Exception {
        // given
        final Long categoryId = null;
        final RoadmapSaveRequest request = makeRoadmapSaveRequest(categoryId, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodesSaveRequest("로드맵 1주차", "로드맵 1주차에는 알고리즘을 배울거에요.")));
        final String jsonRequest = objectMapper.writeValueAsString(request);

        // expect
        requestCreateRoadmap(jsonRequest, status().isBadRequest());
    }

    @Test
    void 로드맵_생성시_로드맵_제목의_길이가_1보다_작으면_예외가_발생한다() throws Exception {
        // given
        final String title = "a".repeat(0);
        final RoadmapSaveRequest request = makeRoadmapSaveRequest(1L, title, "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodesSaveRequest("로드맵 1주차", "로드맵 1주차에는 알고리즘을 배울거에요.")));
        final String jsonRequest = objectMapper.writeValueAsString(request);

        given(roadmapService.create(any(), any()))
                .willThrow(new BadRequestException("로드맵 제목의 길이는 최소 1글자, 최대 40글자입니다."));

        // expect
        requestCreateRoadmap(jsonRequest, status().isBadRequest());
    }

    @Test
    void 로드맵_생성시_로드맵_제목의_길이가_40보다_크면_예외가_발생한다() throws Exception {
        // given
        final String title = "a".repeat(41);
        final RoadmapSaveRequest request = makeRoadmapSaveRequest(1L, title, "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodesSaveRequest("로드맵 1주차", "로드맵 1주차에는 알고리즘을 배울거에요.")));
        final String jsonRequest = objectMapper.writeValueAsString(request);

        given(roadmapService.create(any(), any()))
                .willThrow(new BadRequestException("로드맵 제목의 길이는 최소 1글자, 최대 40글자입니다."));

        // expect
        requestCreateRoadmap(jsonRequest, status().isBadRequest());
    }

    @Test
    void 로드맵_생성시_로드맵_제목을_입력하지_않으면_예외가_발생한다() throws Exception {
        // given
        final String title = null;
        final RoadmapSaveRequest request = makeRoadmapSaveRequest(1L, title, "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodesSaveRequest("로드맵 1주차", "로드맵 1주차에는 알고리즘을 배울거에요.")));
        final String jsonRequest = objectMapper.writeValueAsString(request);

        // expect
        requestCreateRoadmap(jsonRequest, status().isBadRequest());
    }

    @Test
    void 로드맵_생성시_로드맵_소개글의_길이가_1보다_작으면_예외가_발생한다() throws Exception {
        // given
        final String introduction = "a".repeat(0);
        final RoadmapSaveRequest request = makeRoadmapSaveRequest(1L, "로드맵 제목", introduction, "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodesSaveRequest("로드맵 1주차", "로드맵 1주차에는 알고리즘을 배울거에요.")));
        final String jsonRequest = objectMapper.writeValueAsString(request);

        given(roadmapService.create(any(), any()))
                .willThrow(new BadRequestException("로드맵 소개글의 길이는 최소 1글자, 최대 150글자입니다."));

        // expect
        requestCreateRoadmap(jsonRequest, status().isBadRequest());
    }

    @Test
    void 로드맵_생성시_로드맵_소개글의_길이가_150보다_크면_예외가_발생한다() throws Exception {
        // given
        final String introduction = "a".repeat(151);
        final RoadmapSaveRequest request = makeRoadmapSaveRequest(1L, "로드맵 제목", introduction, "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodesSaveRequest("로드맵 1주차", "로드맵 1주차에는 알고리즘을 배울거에요.")));
        final String jsonRequest = objectMapper.writeValueAsString(request);

        given(roadmapService.create(any(), any()))
                .willThrow(new BadRequestException("로드맵 소개글의 길이는 최소 1글자, 최대 150글자입니다."));

        // expect
        requestCreateRoadmap(jsonRequest, status().isBadRequest());
    }

    @Test
    void 로드맵_생성시_로드맵_소개글을_입력하지_않으면_예외가_발생한다() throws Exception {
        // given
        final String introduction = null;
        final RoadmapSaveRequest request = makeRoadmapSaveRequest(1L, "로드맵 제목", introduction, "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodesSaveRequest("로드맵 1주차", "로드맵 1주차에는 알고리즘을 배울거에요.")));
        final String jsonRequest = objectMapper.writeValueAsString(request);

        // expect
        requestCreateRoadmap(jsonRequest, status().isBadRequest());
    }

    @Test
    void 로드맵_생성시_로드맵_본문의_길이가_150보다_크면_예외가_발생한다() throws Exception {
        // given
        final String content = "a".repeat(151);
        final RoadmapSaveRequest request = makeRoadmapSaveRequest(1L, "로드맵 제목", "로드맵 소개글", content,
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodesSaveRequest("로드맵 1주차", "로드맵 1주차에는 알고리즘을 배울거에요.")));
        final String jsonRequest = objectMapper.writeValueAsString(request);

        given(roadmapService.create(any(), any()))
                .willThrow(new BadRequestException("로드맵 본문의 길이는 최대 150글자 입니다."));

        // expect
        requestCreateRoadmap(jsonRequest, status().isBadRequest());
    }

    @Test
    void 로드맵_생성시_로드맵_난이도를_입력하지_않으면_예외가_발생한다() throws Exception {
        // given
        final RoadmapDifficultyType difficulty = null;
        final RoadmapSaveRequest request = makeRoadmapSaveRequest(1L, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                difficulty, 30,
                List.of(new RoadmapNodesSaveRequest("로드맵 1주차", "로드맵 1주차에는 알고리즘을 배울거에요.")));
        final String jsonRequest = objectMapper.writeValueAsString(request);

        // expect
        requestCreateRoadmap(jsonRequest, status().isBadRequest());
    }

    @Test
    void 로드맵_생성시_로드맵_추천_소요기간이_0보다_작으면_예외가_발생한다() throws Exception {
        // given
        final Integer requiredPeriod = -1;
        final RoadmapSaveRequest request = makeRoadmapSaveRequest(1L, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, requiredPeriod,
                List.of(new RoadmapNodesSaveRequest("로드맵 1주차", "로드맵 1주차에는 알고리즘을 배울거에요.")));
        final String jsonRequest = objectMapper.writeValueAsString(request);

        // expect
        requestCreateRoadmap(jsonRequest, status().isBadRequest());
    }

    @Test
    void 로드맵_생성시_로드맵_추천_소요기간이_1000보다_크면_예외가_발생한다() throws Exception {
        // given
        final Integer requiredPeriod = 1001;
        final RoadmapSaveRequest request = makeRoadmapSaveRequest(1L, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, requiredPeriod,
                List.of(new RoadmapNodesSaveRequest("로드맵 1주차", "로드맵 1주차에는 알고리즘을 배울거에요.")));
        final String jsonRequest = objectMapper.writeValueAsString(request);

        given(roadmapService.create(any(), any()))
                .willThrow(new BadRequestException("로드맵 추천 소요 기간은 최소 0일, 최대 1000일입니다."));

        // expect
        requestCreateRoadmap(jsonRequest, status().isBadRequest());
    }

    @Test
    void 로드맵_생성시_로드맵_추천_소요기간을_입력하지_않으면_예외가_발생한다() throws Exception {
        // given
        final Integer requiredPeriod = null;
        final RoadmapSaveRequest request = makeRoadmapSaveRequest(1L, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, requiredPeriod,
                List.of(new RoadmapNodesSaveRequest("로드맵 1주차", "로드맵 1주차에는 알고리즘을 배울거에요.")));
        final String jsonRequest = objectMapper.writeValueAsString(request);

        // expect
        requestCreateRoadmap(jsonRequest, status().isBadRequest());
    }

    @Test
    void 로드맵_생성시_로드맵_노드의_제목의_길이가_1보다_작으면_예외가_발생한다() throws Exception {
        // given
        final String nodeTitle = "a".repeat(0);
        final RoadmapSaveRequest request = makeRoadmapSaveRequest(1L, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodesSaveRequest(nodeTitle, "로드맵 1주차에는 알고리즘을 배울거에요.")));
        final String jsonRequest = objectMapper.writeValueAsString(request);

        given(roadmapService.create(any(), any()))
                .willThrow(new BadRequestException("로드맵 노드의 제목의 길이는 최소 1글자, 최대 40글자입니다."));

        // expect
        requestCreateRoadmap(jsonRequest, status().isBadRequest());
    }

    @Test
    void 로드맵_생성시_로드맵_노드의_제목의_길이가_40보다_크면_예외가_발생한다() throws Exception {
        // given
        final String nodeTitle = "a".repeat(41);
        final RoadmapSaveRequest request = makeRoadmapSaveRequest(1L, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodesSaveRequest(nodeTitle, "로드맵 1주차에는 알고리즘을 배울거에요.")));
        final String jsonRequest = objectMapper.writeValueAsString(request);

        given(roadmapService.create(any(), any()))
                .willThrow(new BadRequestException("로드맵 노드의 제목의 길이는 최소 1글자, 최대 40글자입니다."));

        // expect
        requestCreateRoadmap(jsonRequest, status().isBadRequest());
    }

    @Test
    void 로드맵_생성시_로드맵_노드의_제목을_입력하지_않으면_예외가_발생한다() throws Exception {
        // given
        final String nodeTitle = null;
        final RoadmapSaveRequest request = makeRoadmapSaveRequest(1L, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodesSaveRequest(nodeTitle, "로드맵 1주차에는 알고리즘을 배울거에요.")));
        final String jsonRequest = objectMapper.writeValueAsString(request);

        // expect
        requestCreateRoadmap(jsonRequest, status().isBadRequest());
    }

    @Test
    void 로드맵_생성시_로드맵_노드의_설명의_길이가_1보다_작으면_예외가_발생한다() throws Exception {
        // given
        final String nodeContent = "a".repeat(0);
        final RoadmapSaveRequest request = makeRoadmapSaveRequest(1L, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodesSaveRequest("로드맵 1주차", nodeContent)));
        final String jsonRequest = objectMapper.writeValueAsString(request);

        given(roadmapService.create(any(), any()))
                .willThrow(new BadRequestException("로드맵 노드의 설명의 길이는 최소 1글자, 최대 200글자입니다."));

        // expect
        requestCreateRoadmap(jsonRequest, status().isBadRequest());
    }

    @Test
    void 로드맵_생성시_로드맵_노드의_설명의_길이가_200보다_크면_예외가_발생한다() throws Exception {
        // given
        final String nodeContent = "a".repeat(201);
        final RoadmapSaveRequest request = makeRoadmapSaveRequest(1L, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodesSaveRequest("로드맵 1주차", nodeContent)));
        final String jsonRequest = objectMapper.writeValueAsString(request);

        given(roadmapService.create(any(), any()))
                .willThrow(new BadRequestException("로드맵 노드의 설명의 길이는 최소 1글자, 최대 200글자입니다."));

        // expect
        requestCreateRoadmap(jsonRequest, status().isBadRequest());
    }

    @Test
    void 로드맵_생성시_로드맵_노드의_설명을_입력하지_않으면_예외가_발생한다() throws Exception {
        // given
        final String nodeContent = null;
        final RoadmapSaveRequest request = makeRoadmapSaveRequest(1L, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodesSaveRequest("로드맵 1주차", nodeContent)));
        final String jsonRequest = objectMapper.writeValueAsString(request);

        // expect
        requestCreateRoadmap(jsonRequest, status().isBadRequest());
    }

    @Test
    void 로드맵_생성시_로드맵_노드를_입력하지_않으면_예외가_발생한다() throws Exception {
        // given
        final List<RoadmapNodesSaveRequest> roadmapNodes = null;
        final RoadmapSaveRequest request = makeRoadmapSaveRequest(1L, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30, roadmapNodes);
        final String jsonRequest = objectMapper.writeValueAsString(request);

        // expect
        requestCreateRoadmap(jsonRequest, status().isBadRequest());
    }

    private void requestCreateRoadmap(final String jsonRequest, final ResultMatcher request) throws Exception {
        mockMvc.perform(post(API_PREFIX + "/roadmaps")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(jsonRequest)
                        .contextPath(API_PREFIX))
                .andExpect(request)
                .andDo(print());
    }

    private RoadmapSaveRequest makeRoadmapSaveRequest(final Long categoryId, final String roadmapTitle,
                                                      final String roadmapIntroduction, final String roadmapContent,
                                                      final RoadmapDifficultyType roadmapDifficulty,
                                                      final Integer requiredPeriod,
                                                      final List<RoadmapNodesSaveRequest> roadmapNodesSaveRequests) {
        return new RoadmapSaveRequest(categoryId, roadmapTitle, roadmapIntroduction, roadmapContent, roadmapDifficulty,
                requiredPeriod, roadmapNodesSaveRequests);
    }
}
