package co.kirikiri.integration;

import static co.kirikiri.integration.fixture.MemberAPIFixture.요청을_받는_사용자_자신의_정보_조회_요청;
import static co.kirikiri.integration.fixture.RoadmapAPIFixture.로드맵_생성;
import static co.kirikiri.integration.fixture.RoadmapAPIFixture.제목으로_최신순_정렬된_로드맵을_검색한다;
import static co.kirikiri.integration.fixture.RoadmapAPIFixture.크리에이터_닉네임으로_정렬된_로드맵을_생성한다;
import static co.kirikiri.integration.fixture.RoadmapAPIFixture.태그_이름으로_최신순_정렬된_로드맵을_검색한다;
import static org.assertj.core.api.Assertions.assertThat;

import co.kirikiri.integration.helper.InitIntegrationTest;
import co.kirikiri.service.dto.member.response.MemberInformationResponse;
import co.kirikiri.service.dto.roadmap.request.RoadmapDifficultyType;
import co.kirikiri.service.dto.roadmap.request.RoadmapNodeSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapTagSaveRequest;
import co.kirikiri.service.dto.roadmap.response.RoadmapForListResponses;
import io.restassured.common.mapper.TypeRef;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.List;

class RoadmapSearchIntegrationTest extends InitIntegrationTest {

    @Test
    void 로드맵을_제목을_기준으로_검색한다() throws IOException {
        // given
        로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapSaveRequest 두번째_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "second roadmap", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("다른 태그1")));
        final Long 두번째_로드맵_아이디 = 로드맵_생성(두번째_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapSaveRequest 세번쨰_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "third roadmap", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("다른 태그1")));
        final Long 세번째_로드맵_아이디 = 로드맵_생성(세번쨰_로드맵_생성_요청, 기본_로그인_토큰);

        // when
        final RoadmapForListResponses 로드맵_리스트_응답 = 제목으로_최신순_정렬된_로드맵을_검색한다(10, "roadmap")
                .response()
                .as(new TypeRef<>() {
                });

        // then
        assertThat(로드맵_리스트_응답.hasNext()).isFalse();
        assertThat(로드맵_리스트_응답.responses().get(0).roadmapId()).isEqualTo(세번째_로드맵_아이디);
        assertThat(로드맵_리스트_응답.responses().get(1).roadmapId()).isEqualTo(두번째_로드맵_아이디);
    }

    @Test
    void 로드맵을_크리에이터_닉네임_기준으로_검색한다() throws IOException {
        // given
        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final MemberInformationResponse 사용자_정보 = 요청을_받는_사용자_자신의_정보_조회_요청(기본_로그인_토큰).as(new TypeRef<>() {
        });
        final RoadmapSaveRequest 두번째_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "second roadmap", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("다른 태그1")));
        final Long 두번째_로드맵_아이디 = 로드맵_생성(두번째_로드맵_생성_요청, 기본_로그인_토큰);

        // when
        final RoadmapForListResponses 로드맵_리스트_응답 = 크리에이터_닉네임으로_정렬된_로드맵을_생성한다(10, 사용자_정보.nickname())
                .response()
                .as(new TypeRef<>() {
                });

        // then
        assertThat(로드맵_리스트_응답.hasNext()).isFalse();
        assertThat(로드맵_리스트_응답.responses().get(0).roadmapId()).isEqualTo(두번째_로드맵_아이디);
        assertThat(로드맵_리스트_응답.responses().get(1).roadmapId()).isEqualTo(기본_로드맵_아이디);
    }

    @Test
    void 로드맵을_태그_이름을_기준으로_검색한다() throws IOException {
        // given
        final String 태그_이름 = "tag name";

        로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapSaveRequest 두번째_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "second roadmap", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest(태그_이름)));
        final Long 두번째_로드맵_아이디 = 로드맵_생성(두번째_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapSaveRequest 세번쨰_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "세번쨰 로드맵", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest(태그_이름)));
        final Long 세번째_로드맵_아이디 = 로드맵_생성(세번쨰_로드맵_생성_요청, 기본_로그인_토큰);

        // when
        final RoadmapForListResponses 로드맵_리스트_응답 = 태그_이름으로_최신순_정렬된_로드맵을_검색한다(10, 태그_이름)
                .response()
                .as(new TypeRef<>() {
                });

        // then
        assertThat(로드맵_리스트_응답.hasNext()).isFalse();
        assertThat(로드맵_리스트_응답.responses().get(0).roadmapId()).isEqualTo(세번째_로드맵_아이디);
        assertThat(로드맵_리스트_응답.responses().get(1).roadmapId()).isEqualTo(두번째_로드맵_아이디);
    }
}
