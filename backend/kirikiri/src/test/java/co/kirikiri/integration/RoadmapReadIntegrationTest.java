package co.kirikiri.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.persistence.roadmap.RoadmapCategoryRepository;
import co.kirikiri.service.dto.ErrorResponse;
import co.kirikiri.service.dto.roadmap.request.RoadmapDifficultyType;
import co.kirikiri.service.dto.roadmap.request.RoadmapNodeSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapTagSaveRequest;
import co.kirikiri.service.dto.roadmap.response.MemberRoadmapResponses;
import co.kirikiri.service.dto.roadmap.response.RoadmapCategoryResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapForListResponses;
import co.kirikiri.service.dto.roadmap.response.RoadmapResponse;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class RoadmapReadIntegrationTest extends RoadmapCreateIntegrationTest {

    public RoadmapReadIntegrationTest(final RoadmapCategoryRepository roadmapCategoryRepository) {
        super(roadmapCategoryRepository);
    }

    @Override
    @BeforeEach
    void init() {
        super.init();
    }

    @Test
    void 존재하는_로드맵_아이디로_요청했을_때_단일_로드맵_정보_조회를_성공한다() throws IOException {
        //given
        final Long 기본_로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);
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
    void 카테고리_아이디와_정렬_조건에_따라_로드맵_목록을_조회한다() throws IOException {
        // given
        final Long 기본_로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);
        final RoadmapSaveRequest 두번째_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "second roadmap", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("다른 태그1")));
        final Long 두번째_로드맵_아이디 = 로드맵_생성(두번째_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapCategory 다른_카테고리 = 로드맵_카테고리를_저장한다("여가");
        final RoadmapSaveRequest 세번째_로드맵_생성_요청 = new RoadmapSaveRequest(다른_카테고리.getId(), "thrid roadmap", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("다른 태그1")));
        로드맵_생성(세번째_로드맵_생성_요청, 기본_로그인_토큰);

        // when
        final RoadmapForListResponses 로드맵_리스트_응답 = 최신순으로_정렬된_카테고리별_로드맵_리스트_조회(기본_카테고리.getId(), 10)
                .response()
                .as(new TypeRef<>() {
                });

        // then
        assertThat(로드맵_리스트_응답.hasNext()).isFalse();
        assertThat(로드맵_리스트_응답.responses().get(0).roadmapId()).isEqualTo(두번째_로드맵_아이디);
        assertThat(로드맵_리스트_응답.responses().get(1).roadmapId()).isEqualTo(기본_로드맵_아이디);
    }

    @Test
    void 로드맵_목록_조회시_다음_요소가_존재하면_hasNext가_true로_반환된다() throws IOException {
        // given
        기본_로드맵_생성(기본_로그인_토큰);
        final RoadmapSaveRequest 두번째_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "second roadmap", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("다른 태그1")));
        final Long 두번째_로드맵_아이디 = 로드맵_생성(두번째_로드맵_생성_요청, 기본_로그인_토큰);

        // when
        final RoadmapForListResponses 로드맵_리스트_응답 = 최신순으로_정렬된_카테고리별_로드맵_리스트_조회(기본_카테고리.getId(), 1)
                .response()
                .as(new TypeRef<>() {
                });

        // then
        assertThat(로드맵_리스트_응답.hasNext()).isTrue();
        assertThat(로드맵_리스트_응답.responses().get(0).roadmapId()).isEqualTo(두번째_로드맵_아이디);
    }

    @Test
    void 카테고리_아이디에_따라_로드맵_목록을_조회한다() throws IOException {
        // given
        final Long 기본_로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);
        final RoadmapSaveRequest 두번째_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "second roadmap", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("다른 태그1")));
        final Long 두번째_로드맵_아이디 = 로드맵_생성(두번째_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapCategory 다른_카테고리 = 로드맵_카테고리를_저장한다("여가");
        final RoadmapSaveRequest 세번째_로드맵_생성_요청 = new RoadmapSaveRequest(다른_카테고리.getId(), "thrid roadmap", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("다른 태그1")));
        로드맵_생성(세번째_로드맵_생성_요청, 기본_로그인_토큰);

        // when
        final RoadmapForListResponses 로드맵_리스트_응답 = 최신순으로_정렬된_카테고리별_로드맵_리스트_조회(기본_카테고리.getId(), 10)
                .response()
                .as(new TypeRef<>() {
                });

        // then
        assertThat(로드맵_리스트_응답.hasNext()).isFalse();
        assertThat(로드맵_리스트_응답.responses().get(0).roadmapId()).isEqualTo(두번째_로드맵_아이디);
        assertThat(로드맵_리스트_응답.responses().get(1).roadmapId()).isEqualTo(기본_로드맵_아이디);
    }

    private ExtractableResponse<Response> 로드맵을_아이디로_조회한다(final Long 로드맵_아이디) {
        return given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(API_PREFIX + "/roadmaps/{roadmapId}", 로드맵_아이디)
                .then()
                .log().all()
                .extract();
    }

    protected RoadmapResponse 로드맵을_아이디로_조회하고_응답객체를_반환한다(final Long 로드맵_아이디) {
        return 로드맵을_아이디로_조회한다(로드맵_아이디)
                .response()
                .as(new TypeRef<>() {
                });
    }

    private ExtractableResponse<Response> 최신순으로_정렬된_카테고리별_로드맵_리스트_조회(final Long 검색할_카테고리_아이디, final int 페이지_크기) {
        return given()
                .log().all()
                .when()
                .get("/api/roadmaps?size=" + 페이지_크기 + "&filterType=LATEST&categoryId=" + 검색할_카테고리_아이디)
                .then().log().all()
                .extract();
    }

    @Test
    void 사이즈_조건으로_로드맵_목록을_조회한다() throws IOException {
        // given
        final Long 기본_로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);
        final RoadmapSaveRequest 두번째_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "second roadmap", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("다른 태그1")));
        final Long 두번째_로드맵_아이디 = 로드맵_생성(두번째_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapCategory 다른_카테고리 = 로드맵_카테고리를_저장한다("여가");
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
        final List<RoadmapCategory> 로드맵_카테고리_리스트 = 모든_로드맵_카테고리를_저장한다("IT", "여가", "운동", "시험", "게임");

        // when
        final List<RoadmapCategoryResponse> 로드맵_카테고리_응답_리스트 = 모든_카테고리를_조회한다()
                .response()
                .as(new TypeRef<>() {
                });

        // then
        assertThat(로드맵_카테고리_응답_리스트.get(0).id()).isEqualTo(1L);
        assertThat(로드맵_카테고리_응답_리스트.get(0).name()).isEqualTo("여행");
        for (int index = 1; index < 로드맵_카테고리_응답_리스트.size(); index++) {
            assertThat(로드맵_카테고리_응답_리스트.get(index).id()).isEqualTo(로드맵_카테고리_리스트.get(index - 1).getId());
            assertThat(로드맵_카테고리_응답_리스트.get(index).name()).isEqualTo(로드맵_카테고리_리스트.get(index - 1).getName());
        }
    }

    @Test
    void 로드맵을_제목을_기준으로_검색한다() throws IOException {
        // given
        final Long 기본_로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);
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
    void 로드맵을_크리에이터_기준으로_검색한다() throws IOException {
        // given
        final Long 기본_로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);
        final RoadmapSaveRequest 두번째_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "second roadmap", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("다른 태그1")));
        final Long 두번째_로드맵_아이디 = 로드맵_생성(두번째_로드맵_생성_요청, 기본_로그인_토큰);

        // when
        final RoadmapForListResponses 로드맵_리스트_응답 = 회원_아이디로_최신순_정렬된_로드맵을_검색한다(10, 기본_회원_아이디)
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

        final Long 기본_로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);
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

    @Test
    void 사용자가_생성한_로드맵을_조회한다() throws IOException {
        // given
        final Long 기본_로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);
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
    void 사용자가_생성한_로드맵을_이전에_받아온_리스트_이후로_조회한다() throws IOException {
        // given
        final Long 기본_로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);
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

    private ExtractableResponse<Response> 로그인한_사용자가_생성한_로드맵을_이전에_받은_로드맵의_제일마지막_아이디_이후의_조건으로_조회한다(
            final String 로그인_토큰_정보, final int 페이지_사이즈, final Long 마지막_로드맵_아이디) {
        return given()
                .log().all()
                .when()
                .header(HttpHeaders.AUTHORIZATION, 로그인_토큰_정보)
                .get("/api/roadmaps/me?lastId=" + 마지막_로드맵_아이디 + "&size=" + 페이지_사이즈)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 로그인한_사용자가_생성한_로드맵을_조회한다(final String 로그인_토큰_정보, final int 페이지_사이즈) {
        return given()
                .log().all()
                .when()
                .header(HttpHeaders.AUTHORIZATION, 로그인_토큰_정보)
                .get("/api/roadmaps/me?size=" + 페이지_사이즈)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 태그_이름으로_최신순_정렬된_로드맵을_검색한다(final int 페이지_사이즈, final String 태그) {
        return given()
                .log().all()
                .when()
                .get("/api/roadmaps/search?size=" + 페이지_사이즈 + "&filterType=LATEST&tagName=" + 태그)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 회원_아이디로_최신순_정렬된_로드맵을_검색한다(final int 페이지_사이즈, final Long 회원_아이디) {
        return given()
                .log().all()
                .when()
                .get("/api/roadmaps/search?size=" + 페이지_사이즈 + "&filterType=LATEST&creatorId=" + 회원_아이디)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 제목으로_최신순_정렬된_로드맵을_검색한다(final int 페이지_사이즈, final String 로드맵_제목) {
        return given()
                .log().all()
                .when()
                .get("/api/roadmaps/search?size=" + 페이지_사이즈 + "&filterType=LATEST&roadmapTitle=" + 로드맵_제목)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 모든_카테고리를_조회한다() {
        return given()
                .log().all()
                .when()
                .get("/api/roadmaps/categories")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 사이즈별로_로드맵을_조회한다(final Integer size) {
        return given()
                .log().all()
                .when()
                .get("/api/roadmaps?size=" + size)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 사이즈_없이_로드맵을_조회한다() {
        return given()
                .log().all()
                .when()
                .get("/api/roadmaps")
                .then().log().all()
                .extract();
    }

    private List<RoadmapCategory> 모든_로드맵_카테고리를_저장한다(final String... 카테고리_이름_목록) {
        final List<RoadmapCategory> roadmapCategories = new ArrayList<>();
        for (final String 카테고리_이름 : 카테고리_이름_목록) {
            final RoadmapCategory 로드맵_카테고리 = 로드맵_카테고리를_저장한다(카테고리_이름);
            roadmapCategories.add(new RoadmapCategory(로드맵_카테고리.getId(), 카테고리_이름));
        }
        roadmapCategoryRepository.saveAll(roadmapCategories);
        return roadmapCategories;
    }
}
