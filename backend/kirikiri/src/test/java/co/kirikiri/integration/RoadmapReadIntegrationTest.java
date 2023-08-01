package co.kirikiri.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.integration.helper.IntegrationTest;
import co.kirikiri.persistence.roadmap.RoadmapCategoryRepository;
import co.kirikiri.service.dto.auth.request.LoginRequest;
import co.kirikiri.service.dto.auth.response.AuthenticationResponse;
import co.kirikiri.service.dto.member.request.GenderType;
import co.kirikiri.service.dto.member.request.MemberJoinRequest;
import co.kirikiri.service.dto.member.response.MemberResponse;
import co.kirikiri.service.dto.roadmap.request.RoadmapDifficultyType;
import co.kirikiri.service.dto.roadmap.request.RoadmapNodeSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapTagSaveRequest;
import co.kirikiri.service.dto.roadmap.response.RoadmapCategoryResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapContentResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapForListResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapNodeResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapTagResponse;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class RoadmapReadIntegrationTest extends IntegrationTest {

    private static final String IDENTIFIER = "identifier1";
    private static final String PASSWORD = "password1!";
    private final RoadmapCategoryRepository roadmapCategoryRepository;

    public RoadmapReadIntegrationTest(final RoadmapCategoryRepository roadmapCategoryRepository) {
        this.roadmapCategoryRepository = roadmapCategoryRepository;
    }

    @Test
    void 존재하는_로드맵_아이디로_요청했을_때_단일_로드맵_정보를_조회를_성공한다() {
        //given
        final Long 저장된_크리에이터_아이디 = 크리에이터를_저장한다();
        final String 로그인_토큰_정보 = 로그인();
        final RoadmapCategory 카테고리 = 로드맵_카테고리를_저장한다("운동");
        final Long 로드맵_아이디 = 제목별로_로드맵을_생성한다(로그인_토큰_정보, 카테고리, "로드맵 제목");

        //when
        final ExtractableResponse<Response> 단일_로드맵_조회_요청에_대한_응답 = given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(API_PREFIX + "/roadmaps/{roadmapId}", 로드맵_아이디)
                .then()
                .log().all()
                .extract();

        //then
        final RoadmapResponse 단일_로드맵_응답 = 단일_로드맵_조회_요청에_대한_응답.as(new TypeRef<>() {
        });

        final RoadmapResponse 예상되는_단일_로드맵_응답 = new RoadmapResponse(
                로드맵_아이디,
                new RoadmapCategoryResponse(카테고리.getId(), "운동"),
                "로드맵 제목",
                "로드맵 소개글",
                new MemberResponse(저장된_크리에이터_아이디, "코끼리"),
                new RoadmapContentResponse(
                        1L, "로드맵 본문",
                        List.of(
                                new RoadmapNodeResponse(1L, "로드맵 1주차", "로드맵 1주차 내용", Collections.emptyList())
                        )),
                RoadmapDifficultyType.DIFFICULT.name(),
                30,
                List.of(
                        new RoadmapTagResponse(1L, "태그")
                )
        );

        assertThat(단일_로드맵_조회_요청에_대한_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(단일_로드맵_응답).isEqualTo(예상되는_단일_로드맵_응답);
    }

    @Test
    void 존재하지_않는_로드맵_아이디로_요청했을_때_조회를_실패한다() {
        //given
        final Long 존재하지_않는_로드맵_아이디 = 1L;

        //when
        final ExtractableResponse<Response> 요청에_대한_응답 = given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(API_PREFIX + "/roadmaps/{roadmapId}", 존재하지_않는_로드맵_아이디)
                .then()
                .log().all()
                .extract();

        //then
        final String 예외_메시지 = 요청에_대한_응답.asString();

        assertAll(
                () -> assertThat(요청에_대한_응답.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value()),
                () -> assertThat(예외_메시지).contains("존재하지 않는 로드맵입니다. roadmapId = " + 존재하지_않는_로드맵_아이디)
        );
    }

    @Test
    void 카테고리_아이디와_정렬_조건에_따라_로드맵_목록을_조회한다() {
        // given
        final Long 저장된_크리에이터_아이디 = 크리에이터를_저장한다();
        final String 로그인_토큰_정보 = 로그인();

        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");
        final RoadmapCategory 게임_카테고리 = 로드맵_카테고리를_저장한다("게임");

        final Long 첫번째_로드맵_아이디 = 제목별로_로드맵을_생성한다(로그인_토큰_정보, 여행_카테고리, "첫 번째 로드맵");
        final Long 두번째_로드맵_아이디 = 제목별로_로드맵을_생성한다(로그인_토큰_정보, 여행_카테고리, "두 번째 로드맵");
        제목별로_로드맵을_생성한다(로그인_토큰_정보, 게임_카테고리, "세 번째 로드맵");

        // when
        final List<RoadmapForListResponse> 로드맵_페이지_응답 = given()
                .log().all()
                .when()
                .get("/api/roadmaps?page=1&size=10&filterType=LATEST&categoryId=" + 여행_카테고리.getId())
                .then().log().all()
                .extract()
                .response()
                .as(new TypeRef<>() {
                });

        // then
        final RoadmapForListResponse 첫번째_로드맵_응답 = new RoadmapForListResponse(첫번째_로드맵_아이디, "첫 번째 로드맵",
                "로드맵 소개글", "DIFFICULT", 30,
                new MemberResponse(저장된_크리에이터_아이디, "코끼리"), new RoadmapCategoryResponse(여행_카테고리.getId(), "여행"),
                List.of(new RoadmapTagResponse(1L, "태그")));

        final RoadmapForListResponse 두번째_로드맵_응답 = new RoadmapForListResponse(두번째_로드맵_아이디, "두 번째 로드맵",
                "로드맵 소개글", "DIFFICULT", 30,
                new MemberResponse(저장된_크리에이터_아이디, "코끼리"), new RoadmapCategoryResponse(여행_카테고리.getId(), "여행"),
                List.of(new RoadmapTagResponse(2L, "태그")));

        final List<RoadmapForListResponse> 예상되는_로드맵_리스트_응답 = List.of(두번째_로드맵_응답, 첫번째_로드맵_응답);

        assertThat(로드맵_페이지_응답)
                .isEqualTo(예상되는_로드맵_리스트_응답);
    }

    @Test
    void 카테고리_아이디에_따라_로드맵_목록을_조회한다() {
        // given
        final Long 저장된_크리에이터_아이디 = 크리에이터를_저장한다();
        final String 로그인_토큰_정보 = 로그인();

        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");
        final RoadmapCategory 게임_카테고리 = 로드맵_카테고리를_저장한다("게임");

        final Long 첫번째_로드맵_아이디 = 제목별로_로드맵을_생성한다(로그인_토큰_정보, 여행_카테고리, "첫 번째 로드맵");
        final Long 두번째_로드맵_아이디 = 제목별로_로드맵을_생성한다(로그인_토큰_정보, 여행_카테고리, "두 번째 로드맵");
        제목별로_로드맵을_생성한다(로그인_토큰_정보, 게임_카테고리, "세 번째 로드맵");

        // when
        final List<RoadmapForListResponse> 로드맵_페이지_응답 = given()
                .log().all()
                .when()
                .get("/api/roadmaps?page=1&size=10&categoryId=" + 여행_카테고리.getId())
                .then().log().all()
                .extract()
                .response()
                .as(new TypeRef<>() {
                });

        // then
        final RoadmapForListResponse 첫번째_로드맵_응답 = new RoadmapForListResponse(첫번째_로드맵_아이디, "첫 번째 로드맵",
                "로드맵 소개글", "DIFFICULT", 30,
                new MemberResponse(저장된_크리에이터_아이디, "코끼리"), new RoadmapCategoryResponse(여행_카테고리.getId(), "여행"),
                List.of(new RoadmapTagResponse(1L, "태그")));

        final RoadmapForListResponse 두번째_로드맵_응답 = new RoadmapForListResponse(두번째_로드맵_아이디, "두 번째 로드맵",
                "로드맵 소개글", "DIFFICULT", 30,
                new MemberResponse(저장된_크리에이터_아이디, "코끼리"), new RoadmapCategoryResponse(여행_카테고리.getId(), "여행"),
                List.of(new RoadmapTagResponse(2L, "태그")));

        final List<RoadmapForListResponse> 예상되는_로드맵_리스트_응답 = List.of(두번째_로드맵_응답, 첫번째_로드맵_응답);

        assertThat(로드맵_페이지_응답)
                .isEqualTo(예상되는_로드맵_리스트_응답);
    }

    @Test
    void 정렬_조건에_따라_로드맵_목록을_조회한다() {
        // given
        final Long 저장된_크리에이터_아이디 = 크리에이터를_저장한다();
        final String 로그인_토큰_정보 = 로그인();

        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");
        final RoadmapCategory 게임_카테고리 = 로드맵_카테고리를_저장한다("게임");

        final Long 첫번째_로드맵_아이디 = 제목별로_로드맵을_생성한다(로그인_토큰_정보, 여행_카테고리, "첫 번째 로드맵");
        final Long 두번째_로드맵_아이디 = 제목별로_로드맵을_생성한다(로그인_토큰_정보, 여행_카테고리, "두 번째 로드맵");
        final Long 세번째_로드맵_아이디 = 제목별로_로드맵을_생성한다(로그인_토큰_정보, 게임_카테고리, "세 번째 로드맵");

        // when
        final List<RoadmapForListResponse> 로드맵_페이지_응답 = given()
                .log().all()
                .when()
                .get("/api/roadmaps?page=1&size=10&filterType=LATEST")
                .then().log().all()
                .extract()
                .response()
                .as(new TypeRef<>() {
                });

        // then
        final RoadmapForListResponse 첫번째_로드맵_응답 = new RoadmapForListResponse(첫번째_로드맵_아이디, "첫 번째 로드맵",
                "로드맵 소개글", "DIFFICULT", 30,
                new MemberResponse(저장된_크리에이터_아이디, "코끼리"), new RoadmapCategoryResponse(여행_카테고리.getId(), "여행"),
                List.of(new RoadmapTagResponse(1L, "태그")));

        final RoadmapForListResponse 두번째_로드맵_응답 = new RoadmapForListResponse(두번째_로드맵_아이디, "두 번째 로드맵",
                "로드맵 소개글", "DIFFICULT", 30,
                new MemberResponse(저장된_크리에이터_아이디, "코끼리"), new RoadmapCategoryResponse(여행_카테고리.getId(), "여행"),
                List.of(new RoadmapTagResponse(2L, "태그")));

        final RoadmapForListResponse 세번째_로드맵_응답 = new RoadmapForListResponse(세번째_로드맵_아이디, "세 번째 로드맵",
                "로드맵 소개글", "DIFFICULT", 30,
                new MemberResponse(저장된_크리에이터_아이디, "코끼리"), new RoadmapCategoryResponse(게임_카테고리.getId(), "게임"),
                List.of(new RoadmapTagResponse(3L, "태그")));

        final List<RoadmapForListResponse> 예상되는_로드맵_리스트_응답 = List.of(세번째_로드맵_응답, 두번째_로드맵_응답, 첫번째_로드맵_응답);

        assertThat(로드맵_페이지_응답)
                .isEqualTo(예상되는_로드맵_리스트_응답);
    }

    @Test
    void 아무_조건_없이_로드맵_목록을_조회한다() {
        // given
        final Long 저장된_크리에이터_아이디 = 크리에이터를_저장한다();
        final String 로그인_토큰_정보 = 로그인();

        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");
        final RoadmapCategory 게임_카테고리 = 로드맵_카테고리를_저장한다("게임");

        final Long 첫번째_로드맵_아이디 = 제목별로_로드맵을_생성한다(로그인_토큰_정보, 여행_카테고리, "첫 번째 로드맵");
        final Long 두번째_로드맵_아이디 = 제목별로_로드맵을_생성한다(로그인_토큰_정보, 여행_카테고리, "두 번째 로드맵");
        final Long 세번째_로드맵_아이디 = 제목별로_로드맵을_생성한다(로그인_토큰_정보, 게임_카테고리, "세 번째 로드맵");

        // when
        final List<RoadmapForListResponse> 로드맵_페이지_응답 = given()
                .log().all()
                .when()
                .get("/api/roadmaps?page=1&size=10")
                .then().log().all()
                .extract()
                .response()
                .as(new TypeRef<>() {
                });

        // then
        final RoadmapForListResponse 첫번째_로드맵_응답 = new RoadmapForListResponse(첫번째_로드맵_아이디, "첫 번째 로드맵",
                "로드맵 소개글", "DIFFICULT", 30,
                new MemberResponse(저장된_크리에이터_아이디, "코끼리"), new RoadmapCategoryResponse(여행_카테고리.getId(), "여행"),
                List.of(new RoadmapTagResponse(1L, "태그")));

        final RoadmapForListResponse 두번째_로드맵_응답 = new RoadmapForListResponse(두번째_로드맵_아이디, "두 번째 로드맵",
                "로드맵 소개글", "DIFFICULT", 30,
                new MemberResponse(저장된_크리에이터_아이디, "코끼리"), new RoadmapCategoryResponse(여행_카테고리.getId(), "여행"),
                List.of(new RoadmapTagResponse(2L, "태그")));

        final RoadmapForListResponse 세번째_로드맵_응답 = new RoadmapForListResponse(세번째_로드맵_아이디, "세 번째 로드맵",
                "로드맵 소개글", "DIFFICULT", 30,
                new MemberResponse(저장된_크리에이터_아이디, "코끼리"), new RoadmapCategoryResponse(게임_카테고리.getId(), "게임"),
                List.of(new RoadmapTagResponse(3L, "태그")));
        final List<RoadmapForListResponse> 예상되는_로드맵_리스트_응답 = List.of(세번째_로드맵_응답, 두번째_로드맵_응답, 첫번째_로드맵_응답);

        assertThat(로드맵_페이지_응답)
                .isEqualTo(예상되는_로드맵_리스트_응답);
    }

    @Test
    void 로드맵_카테고리_리스트를_조회한다() {
        // given
        final List<RoadmapCategory> 로드맵_카테고리_리스트 = 모든_로드맵_카테고리를_저장한다();

        // when
        final List<RoadmapCategoryResponse> 로드맵_카테고리_응답_리스트 = given()
                .log().all()
                .when()
                .get("/api/roadmaps/categories")
                .then().log().all()
                .extract()
                .response()
                .as(new TypeRef<>() {
                });

        // then
        final List<RoadmapCategoryResponse> 예상되는_로드맵_카테고리_응답_리스트 = 로드맵_카테고리_응답_리스트를_반환한다(로드맵_카테고리_리스트);

        assertThat(로드맵_카테고리_응답_리스트)
                .isEqualTo(예상되는_로드맵_카테고리_응답_리스트);
    }

    private Long 크리에이터를_저장한다() {
        final MemberJoinRequest 회원가입_요청 = new MemberJoinRequest(IDENTIFIER, PASSWORD, "코끼리", "010-1234-5678",
                GenderType.MALE, LocalDate.of(2023, Month.JULY, 12));

        final String 저장된_크리에이터_아이디 = given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(회원가입_요청)
                .post("/api/members/join")
                .then()
                .log().all()
                .extract()
                .response()
                .getHeader(LOCATION)
                .replace("/api/members/", "");

        return Long.valueOf(저장된_크리에이터_아이디);
    }

    private String 로그인() {
        final LoginRequest 로그인_요청 = new LoginRequest(IDENTIFIER, PASSWORD);

        final AuthenticationResponse 토큰_응답 = given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(로그인_요청)
                .post(API_PREFIX + "/auth/login")
                .then()
                .log().all()
                .extract()
                .as(new TypeRef<>() {
                });

        return String.format(BEARER_TOKEN_FORMAT, 토큰_응답.accessToken());
    }

    private Long 제목별로_로드맵을_생성한다(final String 로그인_토큰_정보, final RoadmapCategory 로드맵_카테고리, final String 로드맵_제목) {
        final RoadmapSaveRequest 로드맵_저장_요청 = new RoadmapSaveRequest(로드맵_카테고리.getId(), 로드맵_제목, "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30, List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용")),
                List.of(new RoadmapTagSaveRequest("태그")));

        final String 생성된_로드맵_아이디 = given()
                .header(AUTHORIZATION, 로그인_토큰_정보)
                .body(로드맵_저장_요청)
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .post("/api/roadmaps")
                .then()
                .log().all()
                .extract()
                .response()
                .getHeader(LOCATION)
                .replace("/api/roadmaps/", "");

        return Long.valueOf(생성된_로드맵_아이디);
    }

    // TODO 카테고리 추가 ADMIN API 생성 시 제거
    private RoadmapCategory 로드맵_카테고리를_저장한다(final String 카테고리_이름) {
        final RoadmapCategory 로드맵_카테고리 = new RoadmapCategory(카테고리_이름);
        return roadmapCategoryRepository.save(로드맵_카테고리);
    }

    private List<RoadmapCategory> 모든_로드맵_카테고리를_저장한다() {
        final RoadmapCategory 카테고리1 = new RoadmapCategory("어학");
        final RoadmapCategory 카테고리2 = new RoadmapCategory("IT");
        final RoadmapCategory 카테고리3 = new RoadmapCategory("시험");
        final RoadmapCategory 카테고리4 = new RoadmapCategory("운동");
        final RoadmapCategory 카테고리5 = new RoadmapCategory("게임");
        final RoadmapCategory 카테고리6 = new RoadmapCategory("음악");
        final RoadmapCategory 카테고리7 = new RoadmapCategory("라이프");
        final RoadmapCategory 카테고리8 = new RoadmapCategory("여가");
        final RoadmapCategory 카테고리9 = new RoadmapCategory("기타");
        return roadmapCategoryRepository.saveAll(
                List.of(카테고리1, 카테고리2, 카테고리3, 카테고리4, 카테고리5, 카테고리6, 카테고리7, 카테고리8,
                        카테고리9));
    }

    private List<RoadmapCategoryResponse> 로드맵_카테고리_응답_리스트를_반환한다(final List<RoadmapCategory> 로드맵_카테고리_리스트) {
        final RoadmapCategoryResponse 카테고리1 = new RoadmapCategoryResponse(로드맵_카테고리_리스트.get(0).getId(),
                "어학");
        final RoadmapCategoryResponse 카테고리2 = new RoadmapCategoryResponse(로드맵_카테고리_리스트.get(1).getId(),
                "IT");
        final RoadmapCategoryResponse 카테고리3 = new RoadmapCategoryResponse(로드맵_카테고리_리스트.get(2).getId(),
                "시험");
        final RoadmapCategoryResponse 카테고리4 = new RoadmapCategoryResponse(로드맵_카테고리_리스트.get(3).getId(),
                "운동");
        final RoadmapCategoryResponse 카테고리5 = new RoadmapCategoryResponse(로드맵_카테고리_리스트.get(4).getId(),
                "게임");
        final RoadmapCategoryResponse 카테고리6 = new RoadmapCategoryResponse(로드맵_카테고리_리스트.get(5).getId(),
                "음악");
        final RoadmapCategoryResponse 카테고리7 = new RoadmapCategoryResponse(로드맵_카테고리_리스트.get(6).getId(),
                "라이프");
        final RoadmapCategoryResponse 카테고리8 = new RoadmapCategoryResponse(로드맵_카테고리_리스트.get(7).getId(),
                "여가");
        final RoadmapCategoryResponse 카테고리9 = new RoadmapCategoryResponse(로드맵_카테고리_리스트.get(8).getId(),
                "기타");
        return List.of(카테고리1, 카테고리2, 카테고리3, 카테고리4, 카테고리5, 카테고리6, 카테고리7, 카테고리8,
                카테고리9);
    }
}
