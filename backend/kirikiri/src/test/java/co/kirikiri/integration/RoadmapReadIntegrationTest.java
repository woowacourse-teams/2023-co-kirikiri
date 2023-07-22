package co.kirikiri.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.domain.roadmap.RoadmapDifficulty;
import co.kirikiri.integration.helper.IntegrationTest;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.persistence.roadmap.RoadmapCategoryRepository;
import co.kirikiri.persistence.roadmap.RoadmapRepository;
import co.kirikiri.service.dto.PageResponse;
import co.kirikiri.service.dto.auth.request.LoginRequest;
import co.kirikiri.service.dto.auth.response.AuthenticationResponse;
import co.kirikiri.service.dto.member.request.GenderType;
import co.kirikiri.service.dto.member.request.MemberJoinRequest;
import co.kirikiri.service.dto.member.response.MemberResponse;
import co.kirikiri.service.dto.roadmap.request.RoadmapDifficultyType;
import co.kirikiri.service.dto.roadmap.request.RoadmapNodeSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapSaveRequest;
import co.kirikiri.service.dto.roadmap.response.RoadmapCategoryResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapContentResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapNodeResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapResponse;
import io.restassured.RestAssured;
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

class RoadmapReadIntegrationTest extends IntegrationTest {

    private static final String IDENTIFIER = "identifier1";
    private static final String PASSWORD = "password1!";
    private static final String NICKNAME = "nickname";
    private final RoadmapRepository roadmapRepository;
    private final RoadmapCategoryRepository roadmapCategoryRepository;
    private final MemberRepository memberRepository;
    private String accessToken;

    public RoadmapReadIntegrationTest(final MemberRepository memberRepository,
                                      final RoadmapRepository roadmapRepository,
                                      final RoadmapCategoryRepository roadmapCategoryRepository) {
        this.memberRepository = memberRepository;
        this.roadmapRepository = roadmapRepository;
        this.roadmapCategoryRepository = roadmapCategoryRepository;
    }

    @Test
    void 존재하는_로드맵_아이디로_요청했을_때_단일_로드맵_정보를_조회를_성공한다() {
        //given
        final Member 크리에이터 = 크리에이터를_생성한다();
        final RoadmapCategory 카테고리 = 로드맵_카테고리를_저장한다("운동");
        final Roadmap 로드맵 = 로드맵을_생성한다(카테고리);

        //when
        final ExtractableResponse<Response> 단일_로드맵_조회_요청에_대한_응답 = given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(API_PREFIX + "/roadmaps/{roadmapId}", 1)
                .then()
                .log().all()
                .extract();

        //then
        final RoadmapResponse 단일_로드맵_응답 = 단일_로드맵_조회_요청에_대한_응답.as(new TypeRef<>() {
        });

        final RoadmapResponse 예상되는_단일_로드맵_응답 = new RoadmapResponse(
                로드맵.getId(),
                new RoadmapCategoryResponse(카테고리.getId(), "운동"),
                "로드맵 제목",
                "로드맵 소개글",
                new MemberResponse(크리에이터.getId(), NICKNAME),
                new RoadmapContentResponse(
                        1L, "로드맵 본문",
                        List.of(
                                new RoadmapNodeResponse(1L, "노드 제목 1", "노드 내용 1", Collections.emptyList()),
                                new RoadmapNodeResponse(2L, "노드 제목 2", "노드 내용 2", Collections.emptyList())
                        )),
                RoadmapDifficultyType.NORMAL.name(),
                100
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
        final Member creator = 크리에이터를_생성한다();
        final RoadmapCategory travelCategory = 로드맵_카테고리를_저장한다("여행");
        final RoadmapCategory gameCategory = 로드맵_카테고리를_저장한다("게임");

        final Roadmap firstRoadmap = 제목별로_로드맵을_생성한다(creator, travelCategory, "첫 번째 로드맵");
        final Roadmap secondRoadmap = 제목별로_로드맵을_생성한다(creator, travelCategory, "두 번째 로드맵");
        final Roadmap thirdRoadmap = 제목별로_로드맵을_생성한다(creator, gameCategory, "세 번째 로드맵");
        final Roadmap savedFirstRoadmap = roadmapRepository.save(firstRoadmap);
        final Roadmap savedSecondRoadmap = roadmapRepository.save(secondRoadmap);
        roadmapRepository.save(thirdRoadmap);

        // when
        final PageResponse<RoadmapResponse> pageResponse = given()
                .log().all()
                .when()
                .get("/api/roadmaps?page=1&size=10&filterType=LATEST&categoryId=" + travelCategory.getId())
                .then().log().all()
                .extract()
                .response()
                .as(new TypeRef<>() {
                });

        // then
        final RoadmapResponse firstRoadmapResponse = new RoadmapResponse(savedFirstRoadmap.getId(), "첫 번째 로드맵",
                "로드맵 소개글", "NORMAL", 10,
                new MemberResponse(1L, "코끼리"), new RoadmapCategoryResponse(travelCategory.getId(), "여행"));
        final RoadmapResponse secondRoadmapResponse = new RoadmapResponse(savedSecondRoadmap.getId(), "두 번째 로드맵",
                "로드맵 소개글", "NORMAL", 10,
                new MemberResponse(1L, "코끼리"), new RoadmapCategoryResponse(travelCategory.getId(), "여행"));
        final PageResponse<RoadmapResponse> expected = new PageResponse<>(1, 1,
                List.of(secondRoadmapResponse, firstRoadmapResponse));

        assertThat(pageResponse)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void 카테고리_아이디에_따라_로드맵_목록을_조회한다() {
        // given
        final Member creator = 크리에이터를_생성한다();
        final RoadmapCategory travelCategory = 로드맵_카테고리를_저장한다("여행");
        final RoadmapCategory gameCategory = 로드맵_카테고리를_저장한다("게임");

        final Roadmap firstRoadmap = 제목별로_로드맵을_생성한다(creator, travelCategory, "첫 번째 로드맵");
        final Roadmap secondRoadmap = 제목별로_로드맵을_생성한다(creator, travelCategory, "두 번째 로드맵");
        final Roadmap thirdRoadmap = 제목별로_로드맵을_생성한다(creator, gameCategory, "세 번째 로드맵");
        final Roadmap savedFirstRoadmap = roadmapRepository.save(firstRoadmap);
        final Roadmap savedSecondRoadmap = roadmapRepository.save(secondRoadmap);
        roadmapRepository.save(thirdRoadmap);

        // when
        final PageResponse<RoadmapResponse> pageResponse = given()
                .log().all()
                .when()
                .get("/api/roadmaps?page=1&size=10&categoryId=" + travelCategory.getId())
                .then().log().all()
                .extract()
                .response()
                .as(new TypeRef<>() {
                });

        // then
        final RoadmapResponse firstRoadmapResponse = new RoadmapResponse(savedFirstRoadmap.getId(), "첫 번째 로드맵",
                "로드맵 소개글", "NORMAL", 10,
                new MemberResponse(1L, "코끼리"), new RoadmapCategoryResponse(travelCategory.getId(), "여행"));
        final RoadmapResponse secondRoadmapResponse = new RoadmapResponse(savedSecondRoadmap.getId(), "두 번째 로드맵",
                "로드맵 소개글", "NORMAL", 10,
                new MemberResponse(1L, "코끼리"), new RoadmapCategoryResponse(travelCategory.getId(), "여행"));
        final PageResponse<RoadmapResponse> expected = new PageResponse<>(1, 1,
                List.of(secondRoadmapResponse, firstRoadmapResponse));

        assertThat(pageResponse)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void 정렬_조건에_따라_로드맵_목록을_조회한다() {
        // given
        final Member creator = 크리에이터를_생성한다();
        final RoadmapCategory travelCategory = 로드맵_카테고리를_저장한다("여행");
        final RoadmapCategory gameCategory = 로드맵_카테고리를_저장한다("게임");

        final Roadmap firstRoadmap = 제목별로_로드맵을_생성한다(creator, travelCategory, "첫 번째 로드맵");
        final Roadmap secondRoadmap = 제목별로_로드맵을_생성한다(creator, travelCategory, "두 번째 로드맵");
        final Roadmap thirdRoadmap = 제목별로_로드맵을_생성한다(creator, gameCategory, "세 번째 로드맵");
        final Roadmap savedFirstRoadmap = roadmapRepository.save(firstRoadmap);
        final Roadmap savedSecondRoadmap = roadmapRepository.save(secondRoadmap);
        final Roadmap thirdSecondRoadmap = roadmapRepository.save(thirdRoadmap);

        // when
        final PageResponse<RoadmapResponse> pageResponse = given()
                .log().all()
                .when()
                .get("/api/roadmaps?page=1&size=10&filterType=LATEST")
                .then().log().all()
                .extract()
                .response()
                .as(new TypeRef<>() {
                });

        // then
        final RoadmapResponse firstRoadmapResponse = new RoadmapResponse(savedFirstRoadmap.getId(), "첫 번째 로드맵",
                "로드맵 소개글", "NORMAL", 10,
                new MemberResponse(1L, "코끼리"), new RoadmapCategoryResponse(travelCategory.getId(), "여행"));
        final RoadmapResponse secondRoadmapResponse = new RoadmapResponse(savedSecondRoadmap.getId(), "두 번째 로드맵",
                "로드맵 소개글", "NORMAL", 10,
                new MemberResponse(1L, "코끼리"), new RoadmapCategoryResponse(travelCategory.getId(), "여행"));
        final RoadmapResponse thirdRoadmapResponse = new RoadmapResponse(thirdSecondRoadmap.getId(), "세 번째 로드맵",
                "로드맵 소개글", "NORMAL", 10,
                new MemberResponse(1L, "코끼리"), new RoadmapCategoryResponse(gameCategory.getId(), "게임"));
        final PageResponse<RoadmapResponse> expected = new PageResponse<>(1, 1,
                List.of(thirdRoadmapResponse, secondRoadmapResponse, firstRoadmapResponse));

        assertThat(pageResponse)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void 아무_조건_없이_로드맵_목록을_조회한다() {
        // given
        final Member creator = 크리에이터를_생성한다();
        final RoadmapCategory travelCategory = 로드맵_카테고리를_저장한다("여행");
        final RoadmapCategory gameCategory = 로드맵_카테고리를_저장한다("게임");

        final Roadmap firstRoadmap = 제목별로_로드맵을_생성한다(creator, travelCategory, "첫 번째 로드맵");
        final Roadmap secondRoadmap = 제목별로_로드맵을_생성한다(creator, travelCategory, "두 번째 로드맵");
        final Roadmap thirdRoadmap = 제목별로_로드맵을_생성한다(creator, gameCategory, "세 번째 로드맵");
        final Roadmap savedFirstRoadmap = roadmapRepository.save(firstRoadmap);
        final Roadmap savedSecondRoadmap = roadmapRepository.save(secondRoadmap);
        final Roadmap thirdSecondRoadmap = roadmapRepository.save(thirdRoadmap);

        // when
        final PageResponse<RoadmapResponse> pageResponse = given()
                .log().all()
                .when()
                .get("/api/roadmaps?page=1&size=10")
                .then().log().all()
                .extract()
                .response()
                .as(new TypeRef<>() {
                });

        // then
        final RoadmapResponse firstRoadmapResponse = new RoadmapResponse(savedFirstRoadmap.getId(), "첫 번째 로드맵",
                "로드맵 소개글", "NORMAL", 10,
                new MemberResponse(1L, "코끼리"), new RoadmapCategoryResponse(travelCategory.getId(), "여행"));
        final RoadmapResponse secondRoadmapResponse = new RoadmapResponse(savedSecondRoadmap.getId(), "두 번째 로드맵",
                "로드맵 소개글", "NORMAL", 10,
                new MemberResponse(1L, "코끼리"), new RoadmapCategoryResponse(travelCategory.getId(), "여행"));
        final RoadmapResponse thirdRoadmapResponse = new RoadmapResponse(thirdSecondRoadmap.getId(), "세 번째 로드맵",
                "로드맵 소개글", "NORMAL", 10,
                new MemberResponse(1L, "코끼리"), new RoadmapCategoryResponse(gameCategory.getId(), "게임"));
        final PageResponse<RoadmapResponse> expected = new PageResponse<>(1, 1,
                List.of(thirdRoadmapResponse, secondRoadmapResponse, firstRoadmapResponse));

        assertThat(pageResponse)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void 로드맵_카테고리_리스트를_조회한다() {
        // given
        final List<RoadmapCategory> roadmapCategories = 로드맵_카테고리를_저장한다();

        // when
        final List<RoadmapCategoryResponse> roadmapCategoryResponses = given()
                .log().all()
                .when()
                .get("/api/roadmaps/categories")
                .then().log().all()
                .extract()
                .response()
                .as(new TypeRef<>() {
                });

        // then
        final List<RoadmapCategoryResponse> expected = 로드맵_카테고리_응답_리스트를_반환한다(roadmapCategories);

        assertThat(roadmapCategoryResponses)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    private Member 크리에이터를_생성한다() {
        회원가입();
        return memberRepository.findByIdentifier(new Identifier(IDENTIFIER)).get();
    }

    private RoadmapCategory 로드맵_카테고리를_저장한다(final String name) {
        final RoadmapCategory roadmapCategory = new RoadmapCategory(name);
        return roadmapCategoryRepository.save(roadmapCategory);
    }

    private List<RoadmapCategory> 로드맵_카테고리를_저장한다() {
        final RoadmapCategory category1 = new RoadmapCategory("어학");
        final RoadmapCategory category2 = new RoadmapCategory("IT");
        final RoadmapCategory category3 = new RoadmapCategory("시험");
        final RoadmapCategory category4 = new RoadmapCategory("운동");
        final RoadmapCategory category5 = new RoadmapCategory("게임");
        final RoadmapCategory category6 = new RoadmapCategory("음악");
        final RoadmapCategory category7 = new RoadmapCategory("라이프");
        final RoadmapCategory category8 = new RoadmapCategory("여가");
        final RoadmapCategory category9 = new RoadmapCategory("기타");
        return roadmapCategoryRepository.saveAll(
                List.of(category1, category2, category3, category4, category5, category6, category7, category8,
                        category9));
    }

    private List<RoadmapCategoryResponse> 로드맵_카테고리_응답_리스트를_반환한다(final List<RoadmapCategory> roadmapCategories) {
        final RoadmapCategoryResponse category1 = new RoadmapCategoryResponse(roadmapCategories.get(0).getId(),
                "어학");
        final RoadmapCategoryResponse category2 = new RoadmapCategoryResponse(roadmapCategories.get(1).getId(),
                "IT");
        final RoadmapCategoryResponse category3 = new RoadmapCategoryResponse(roadmapCategories.get(2).getId(),
                "시험");
        final RoadmapCategoryResponse category4 = new RoadmapCategoryResponse(roadmapCategories.get(3).getId(),
                "운동");
        final RoadmapCategoryResponse category5 = new RoadmapCategoryResponse(roadmapCategories.get(4).getId(),
                "게임");
        final RoadmapCategoryResponse category6 = new RoadmapCategoryResponse(roadmapCategories.get(5).getId(),
                "음악");
        final RoadmapCategoryResponse category7 = new RoadmapCategoryResponse(roadmapCategories.get(6).getId(),
                "라이프");
        final RoadmapCategoryResponse category8 = new RoadmapCategoryResponse(roadmapCategories.get(7).getId(),
                "여가");
        final RoadmapCategoryResponse category9 = new RoadmapCategoryResponse(roadmapCategories.get(8).getId(),
                "기타");
        return List.of(category1, category2, category3, category4, category5, category6, category7, category8,
                category9);
    }

    private Roadmap 제목별로_로드맵을_생성한다(final Member creator, final RoadmapCategory category, final String roadmapTitle) {
        return new Roadmap(roadmapTitle, "로드맵 소개글", 10, RoadmapDifficulty.NORMAL, creator, category);
    }

    private Roadmap 로드맵을_생성한다(final RoadmapCategory category) {
        final AuthenticationResponse authenticationResponse = 로그인(new LoginRequest(IDENTIFIER, PASSWORD))
                .as(AuthenticationResponse.class);
        accessToken = authenticationResponse.accessToken();

        final List<RoadmapNodeSaveRequest> nodes = List.of(
                new RoadmapNodeSaveRequest("노드 제목 1", "노드 내용 1"),
                new RoadmapNodeSaveRequest("노드 제목 2", "노드 내용 2")
        );

        final String locationHeader = 로드맵_생성_요청(
                category.getId(),
                "로드맵 제목",
                "로드맵 소개글",
                "로드맵 본문",
                RoadmapDifficultyType.NORMAL,
                100,
                nodes,
                accessToken
        ).header("Location");

        final Long roadmapId = Long.parseLong(locationHeader.substring(locationHeader.length() - 1));
        return roadmapRepository.findById(roadmapId).get();
    }

    private void 회원가입() {
        final MemberJoinRequest 회원가입_요청 = new MemberJoinRequest(IDENTIFIER, PASSWORD, NICKNAME, "010-1234-5678",
                GenderType.MALE, LocalDate.of(2023, Month.JULY, 12));

        given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(회원가입_요청)
                .post(API_PREFIX + "/members/join")
                .then()
                .log().all()
                .extract();
    }

    private ExtractableResponse<Response> 로그인(final LoginRequest 로그인_요청) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(로그인_요청)
                .post(API_PREFIX + "/auth/login")
                .then()
                .log().all()
                .extract();
    }

    private ExtractableResponse<Response> 로드맵_생성_요청(final Long 카테고리_ID, final String 로드맵_제목, final String 로드맵_소개글,
                                                    final String 로드맵_본문, final RoadmapDifficultyType 로드맵_난이도,
                                                    final Integer 추천_소요_기간, final List<RoadmapNodeSaveRequest> 로드맵_노드들,
                                                    final String accessToken) {
        final RoadmapSaveRequest request = new RoadmapSaveRequest(카테고리_ID, 로드맵_제목, 로드맵_소개글, 로드맵_본문, 로드맵_난이도,
                추천_소요_기간, 로드맵_노드들);

        return RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .body(request).log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .post(API_PREFIX + "/roadmaps")
                .then().log().all()
                .extract();
    }
}
