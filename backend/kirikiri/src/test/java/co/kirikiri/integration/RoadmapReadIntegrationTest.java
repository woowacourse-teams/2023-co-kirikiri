package co.kirikiri.integration;

import static co.kirikiri.integration.fixture.RoadmapAPIFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import co.kirikiri.integration.helper.InitIntegrationTest;
import co.kirikiri.roadmap.domain.RoadmapCategory;
import co.kirikiri.roadmap.persistence.dto.RoadmapOrderType;
import co.kirikiri.roadmap.service.dto.request.RoadmapDifficultyType;
import co.kirikiri.roadmap.service.dto.request.RoadmapNodeSaveRequest;
import co.kirikiri.roadmap.service.dto.request.RoadmapSaveRequest;
import co.kirikiri.roadmap.service.dto.request.RoadmapTagSaveRequest;
import co.kirikiri.roadmap.service.dto.response.MemberRoadmapResponses;
import co.kirikiri.roadmap.service.dto.response.RoadmapCategoryResponse;
import co.kirikiri.roadmap.service.dto.response.RoadmapForListResponse;
import co.kirikiri.roadmap.service.dto.response.RoadmapForListResponses;
import co.kirikiri.roadmap.service.dto.response.RoadmapResponse;
import co.kirikiri.service.dto.ErrorResponse;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import java.io.IOException;
import java.util.List;

class RoadmapReadIntegrationTest extends InitIntegrationTest {

    @Test
    void 존재하는_로드맵_아이디로_요청했을_때_단일_로드맵_정보_조회를_성공한다() throws IOException {
        //given
        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapSaveRequest 다른_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "다른 로드맵 제목", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("다른 태그1")));
        로드맵_생성(다른_로드맵_생성_요청, 기본_로그인_토큰);

        //when
        final ExtractableResponse<Response> 단일_로드맵_조회_요청에_대한_응답 = 로드맵을_아이디로_조회한다(기본_로드맵_아이디);

        //then
        final RoadmapResponse 단일_로드맵_응답 = 단일_로드맵_조회_요청에_대한_응답.as(new TypeRef<>() {
        });

        assertAll(
                () -> assertThat(단일_로드맵_조회_요청에_대한_응답.statusCode())
                        .isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(단일_로드맵_응답.roadmapId())
                        .isEqualTo(기본_로드맵_아이디)
        );
    }

    @Test
    void 존재하지_않는_로드맵_아이디로_요청했을_때_조회를_실패한다() {
        //given
        final Long 존재하지_않는_로드맵_아이디 = 1L;

        //when
        final ExtractableResponse<Response> 요청에_대한_응답 = 로드맵을_아이디로_조회한다(존재하지_않는_로드맵_아이디);

        //then
        final String 예외_메시지 = 요청에_대한_응답.asString();

        assertAll(
                () -> assertThat(요청에_대한_응답.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value()),
                () -> assertThat(예외_메시지).contains("존재하지 않는 로드맵입니다. roadmapId = " + 존재하지_않는_로드맵_아이디)
        );
    }

    @Test
    void 사이즈_조건으로_로드맵_목록을_조회한다() throws IOException {
        // given
        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapSaveRequest 두번째_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "second roadmap", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("다른 태그1")));
        final Long 두번째_로드맵_아이디 = 로드맵_생성(두번째_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapCategory 다른_카테고리 = 카테고리_생성(기본_로그인_토큰, "여가");
        final RoadmapSaveRequest 세번째_로드맵_생성_요청 = new RoadmapSaveRequest(다른_카테고리.getId(), "thrid roadmap", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("다른 태그1")));
        final Long 세번째_로드맵_아이디 = 로드맵_생성(세번째_로드맵_생성_요청, 기본_로그인_토큰);

        // when
        final RoadmapForListResponses 로드맵_리스트_응답 = 사이즈별로_로드맵을_조회한다(10)
                .response()
                .as(new TypeRef<>() {
                });

        // then
        assertThat(로드맵_리스트_응답.hasNext()).isFalse();
        assertThat(로드맵_리스트_응답.responses().get(0).roadmapId()).isEqualTo(세번째_로드맵_아이디);
        assertThat(로드맵_리스트_응답.responses().get(1).roadmapId()).isEqualTo(두번째_로드맵_아이디);
        assertThat(로드맵_리스트_응답.responses().get(2).roadmapId()).isEqualTo(기본_로드맵_아이디);
    }

    @Test
    void 로드맵_태그가_여러개일_경우_로드맵을_조회한다() throws IOException {
        // given
        for (int i = 0; i < 10; i++) {
            final RoadmapSaveRequest 두번째_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "roadmap" + i,
                    "다른 로드맵 소개글", "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                    List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                    List.of(new RoadmapTagSaveRequest("다른 태그1"),
                            new RoadmapTagSaveRequest("다른 태그2"),
                            new RoadmapTagSaveRequest("다른 태그3")));
            로드맵_생성(두번째_로드맵_생성_요청, 기본_로그인_토큰);
        }

        // when
        final RoadmapForListResponses 로드맵_리스트_응답 = 사이즈별로_로드맵을_조회한다(10)
                .response()
                .as(new TypeRef<>() {
                });

        // then
        assertThat(로드맵_리스트_응답.hasNext()).isFalse();
        assertThat(로드맵_리스트_응답.responses().size()).isEqualTo(10);
        for (final RoadmapForListResponse response : 로드맵_리스트_응답.responses()) {
            assertThat(response.tags().size()).isEqualTo(3);
        }
    }

    @Test
    void 로드맵_조회시_사이즈_조건을_주지_않으면_예외가_발생한다() {
        // when
        final List<ErrorResponse> 예외_메시지 = 사이즈_없이_로드맵을_조회한다()
                .response()
                .as(new TypeRef<>() {
                });

        // then
        assertThat(예외_메시지.get(0))
                .isEqualTo(new ErrorResponse("사이즈를 입력해 주세요."));
    }

    @Test
    void 로드맵_카테고리_리스트를_조회한다() {
        // given
        final List<RoadmapCategory> 로드맵_카테고리_리스트 = 카테고리들_생성(기본_로그인_토큰, "IT", "여가", "운동", "시험",
                "게임");

        // when
        final List<RoadmapCategoryResponse> 로드맵_카테고리_응답_리스트 = 모든_카테고리를_조회한다()
                .response()
                .as(new TypeRef<>() {
                });

        // then
        assertThat(로드맵_카테고리_응답_리스트.get(0).name()).isEqualTo("여행");
        for (int index = 1; index < 로드맵_카테고리_응답_리스트.size(); index++) {
            assertThat(로드맵_카테고리_응답_리스트.get(index).name()).isEqualTo(로드맵_카테고리_리스트.get(index - 1).getName());
        }
    }

    @Test
    void 사용자가_생성한_로드맵을_조회한다() throws IOException {
        // given
        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapSaveRequest 두번째_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "second roadmap", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("다른 태그1")));
        final Long 두번째_로드맵_아이디 = 로드맵_생성(두번째_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapSaveRequest 세번쨰_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "세번쨰 로드맵", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("다른 태그1")));
        final Long 세번째_로드맵_아이디 = 로드맵_생성(세번쨰_로드맵_생성_요청, 기본_로그인_토큰);

        // when
        final MemberRoadmapResponses 사용자_로드맵_응답_리스트 = 로그인한_사용자가_생성한_로드맵을_조회한다(기본_로그인_토큰, 10)
                .response()
                .as(new TypeRef<>() {
                });

        // then
        assertThat(사용자_로드맵_응답_리스트.hasNext()).isFalse();
        assertThat(사용자_로드맵_응답_리스트.responses().get(0).roadmapId()).isEqualTo(세번째_로드맵_아이디);
        assertThat(사용자_로드맵_응답_리스트.responses().get(1).roadmapId()).isEqualTo(두번째_로드맵_아이디);
        assertThat(사용자_로드맵_응답_리스트.responses().get(2).roadmapId()).isEqualTo(기본_로드맵_아이디);
    }

    @Test
    void 로드맵_목록_조회시_다음_요소가_존재하면_hasNext가_true로_반환된다() throws IOException {
        // given
        로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapSaveRequest 두번째_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "second roadmap", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("다른 태그1")));
        final Long 두번째_로드맵_아이디 = 로드맵_생성(두번째_로드맵_생성_요청, 기본_로그인_토큰);

        // when
        final RoadmapForListResponses 로드맵_리스트_응답 = 정렬된_카테고리별_로드맵_리스트_조회(RoadmapOrderType.LATEST, 기본_카테고리.getId(), 1)
                .response()
                .as(new TypeRef<>() {
                });

        // then
        assertThat(로드맵_리스트_응답.hasNext()).isTrue();
        assertThat(로드맵_리스트_응답.responses().get(0).roadmapId()).isEqualTo(두번째_로드맵_아이디);
    }

    @Test
    void 사용자가_생성한_로드맵을_이전에_받아온_리스트_이후로_조회한다() throws IOException {
        // given
        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapSaveRequest 두번째_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "second roadmap", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("다른 태그1")));
        final Long 두번째_로드맵_아이디 = 로드맵_생성(두번째_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapSaveRequest 세번쨰_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "세번쨰 로드맵", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("다른 태그1")));
        final Long 세번째_로드맵_아이디 = 로드맵_생성(세번쨰_로드맵_생성_요청, 기본_로그인_토큰);

        // when
        final MemberRoadmapResponses 사용자_로드맵_응답_리스트 = 로그인한_사용자가_생성한_로드맵을_이전에_받은_로드맵의_제일마지막_아이디_이후의_조건으로_조회한다(
                기본_로그인_토큰, 10, 두번째_로드맵_아이디
        )
                .response()
                .as(new TypeRef<>() {
                });

        // then
        assertThat(사용자_로드맵_응답_리스트.hasNext()).isFalse();
        assertThat(사용자_로드맵_응답_리스트.responses().get(0).roadmapId()).isEqualTo(기본_로드맵_아이디);
    }
}
