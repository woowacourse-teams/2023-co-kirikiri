package co.kirikiri.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNodes;
import co.kirikiri.domain.goalroom.GoalRoomStatus;
import co.kirikiri.domain.goalroom.vo.GoalRoomName;
import co.kirikiri.domain.goalroom.vo.LimitedMemberCount;
import co.kirikiri.domain.goalroom.vo.Period;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.domain.roadmap.RoadmapDifficulty;
import co.kirikiri.domain.roadmap.RoadmapNode;
import co.kirikiri.domain.roadmap.RoadmapNodes;
import co.kirikiri.integration.helper.IntegrationTest;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.persistence.roadmap.RoadmapCategoryRepository;
import co.kirikiri.persistence.roadmap.RoadmapNodeRepository;
import co.kirikiri.persistence.roadmap.RoadmapRepository;
import co.kirikiri.service.dto.ErrorResponse;
import co.kirikiri.service.dto.auth.request.LoginRequest;
import co.kirikiri.service.dto.auth.response.AuthenticationResponse;
import co.kirikiri.service.dto.goalroom.request.GoalRoomCreateRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomRoadmapNodeRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomTodoRequest;
import co.kirikiri.service.dto.goalroom.response.GoalRoomResponse;
import co.kirikiri.service.dto.member.request.GenderType;
import co.kirikiri.service.dto.member.request.MemberJoinRequest;
import co.kirikiri.service.dto.member.response.MemberResponse;
import co.kirikiri.service.dto.roadmap.RoadmapGoalRoomsFilterTypeDto;
import co.kirikiri.service.dto.roadmap.request.RoadmapDifficultyType;
import co.kirikiri.service.dto.roadmap.request.RoadmapNodeSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapTagSaveRequest;
import co.kirikiri.service.dto.roadmap.response.MemberRoadmapResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapCategoryResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapContentResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapForListResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapGoalRoomResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapNodeResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapTagResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.Header;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class RoadmapReadIntegrationTest extends IntegrationTest {

    private static final String IDENTIFIER = "identifier1";
    private static final String PASSWORD = "password1!";
    private static final String NICKNAME = "코끼리";

    private static final LocalDate 오늘 = LocalDate.now();
    private static final LocalDate 십일_후 = 오늘.plusDays(10L);
    private static final String 정상적인_골룸_이름 = "GOAL_ROOM_NAME";
    private static final int 정상적인_골룸_제한_인원 = 20;
    private static final String 정상적인_골룸_투두_컨텐츠 = "GOAL_ROOM_TO_DO_CONTENT";

    private static final String BEARER = "Bearer ";
    private static final String 카테고리_이름 = "여가";

    private static final MemberJoinRequest 회원가입_요청 = new MemberJoinRequest("ab12", "password12!@#$%", "nickname",
            "010-1234-5678",
            GenderType.MALE, LocalDate.of(2023, Month.JULY, 12));
    private static final LoginRequest 로그인_요청 = new LoginRequest(회원가입_요청.identifier(), 회원가입_요청.password());

    private static final MemberJoinRequest 골룸_참여자1_회원가입_요청 = new MemberJoinRequest("identifier2", "password!2", "name2",
            "010-1111-2222", GenderType.FEMALE, LocalDate.now());
    private static final MemberJoinRequest 골룸_참여자2_회원가입_요청 = new MemberJoinRequest("identifier3", "password!3", "name3",
            "010-1111-3333", GenderType.FEMALE, LocalDate.now());
    private static final LoginRequest 골룸_참여자1_로그인_요청 = new LoginRequest(골룸_참여자1_회원가입_요청.identifier(),
            골룸_참여자1_회원가입_요청.password());
    private static final LoginRequest 골룸_참여자2_로그인_요청 = new LoginRequest(골룸_참여자2_회원가입_요청.identifier(),
            골룸_참여자2_회원가입_요청.password());

    private Long 골룸_목록_조회할_로드맵_아이디;
    private final MemberRepository memberRepository;
    private final RoadmapCategoryRepository roadmapCategoryRepository;
    private final RoadmapNodeRepository roadmapNodeRepository;
    private final RoadmapRepository roadmapRepository;
    private final GoalRoomRepository goalRoomRepository;

    public RoadmapReadIntegrationTest(final MemberRepository memberRepository,
                                      final RoadmapCategoryRepository roadmapCategoryRepository,
                                      final RoadmapNodeRepository roadmapNodeRepository,
                                      final RoadmapRepository roadmapRepository,
                                      final GoalRoomRepository goalRoomRepository) {
        this.memberRepository = memberRepository;
        this.roadmapCategoryRepository = roadmapCategoryRepository;
        this.roadmapNodeRepository = roadmapNodeRepository;
        this.roadmapRepository = roadmapRepository;
        this.goalRoomRepository = goalRoomRepository;
    }

    @Test
    void 존재하는_로드맵_아이디로_요청했을_때_단일_로드맵_정보_조회를_성공한다() {
        //given
        final Long 크리에이터_아이디 = 크리에이터를_저장한다(IDENTIFIER, NICKNAME);
        final String 로그인_토큰_정보 = 로그인(new LoginRequest(IDENTIFIER, PASSWORD));
        final Member 크리에이터 = memberRepository.findById(크리에이터_아이디).get();
        final RoadmapCategory 카테고리 = 로드맵_카테고리를_저장한다("운동");
        final Long 로드맵_아이디 = 제목별로_로드맵을_생성한다(로그인_토큰_정보, 카테고리, "로드맵 제목");
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회한다(로드맵_아이디);

        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_응답.content().id(), "골룸 이름", 20,
                new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후),
                List.of(new GoalRoomRoadmapNodeRequest(로드맵_응답.content().nodes().get(0).id(), 1, 오늘, 십일_후)));

        골룸_생성(골룸_생성_요청, 로그인_토큰_정보);
        골룸_생성(골룸_생성_요청, 로그인_토큰_정보);
        골룸_생성(골룸_생성_요청, 로그인_토큰_정보);
        골룸_생성(골룸_생성_요청, 로그인_토큰_정보);
        골룸_생성(골룸_생성_요청, 로그인_토큰_정보);
        골룸_생성(골룸_생성_요청, 로그인_토큰_정보);

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
                new MemberResponse(크리에이터_아이디, "코끼리", ""),
                new RoadmapContentResponse(
                        1L, "로드맵 본문",
                        List.of(
                                new RoadmapNodeResponse(1L, "로드맵 1주차", "로드맵 1주차 내용", Collections.emptyList())
                        )),
                RoadmapDifficultyType.DIFFICULT.name(),
                30,
                단일_로드맵_응답.createdAt(),
                List.of(
                        new RoadmapTagResponse(1L, "태그")
                ),
                6L, 0L, 0L
        );

        assertThat(단일_로드맵_조회_요청에_대한_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(단일_로드맵_응답)
                .usingRecursiveComparison()
                .ignoringFields("creator.imageUrl")
                .isEqualTo(예상되는_단일_로드맵_응답);
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
        final Long 저장된_크리에이터_아이디 = 크리에이터를_저장한다(IDENTIFIER, NICKNAME);
        final String 로그인_토큰_정보 = 로그인(new LoginRequest(IDENTIFIER, PASSWORD));

        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");
        final RoadmapCategory 게임_카테고리 = 로드맵_카테고리를_저장한다("게임");

        final Long 첫번째_로드맵_아이디 = 제목별로_로드맵을_생성한다(로그인_토큰_정보, 여행_카테고리, "첫 번째 로드맵");
        final Long 두번째_로드맵_아이디 = 제목별로_로드맵을_생성한다(로그인_토큰_정보, 여행_카테고리, "두 번째 로드맵");
        제목별로_로드맵을_생성한다(로그인_토큰_정보, 게임_카테고리, "세 번째 로드맵");

        final RoadmapResponse 첫번째_로드맵 = 로드맵을_아이디로_조회한다(첫번째_로드맵_아이디);
        final RoadmapResponse 두번째_로드맵 = 로드맵을_아이디로_조회한다(두번째_로드맵_아이디);

        // when
        final List<RoadmapForListResponse> 로드맵_리스트_응답 = given()
                .log().all()
                .when()
                .get("/api/roadmaps?size=10&filterType=LATEST&categoryId=" + 여행_카테고리.getId())
                .then().log().all()
                .extract()
                .response()
                .as(new TypeRef<>() {
                });

        // then
        final RoadmapForListResponse 첫번째_로드맵_응답 = new RoadmapForListResponse(첫번째_로드맵_아이디, "첫 번째 로드맵",
                "로드맵 소개글", "DIFFICULT", 30, 첫번째_로드맵.createdAt(),
                new MemberResponse(저장된_크리에이터_아이디, "코끼리"), new RoadmapCategoryResponse(여행_카테고리.getId(), "여행"),
                List.of(new RoadmapTagResponse(1L, "태그")));

        final RoadmapForListResponse 두번째_로드맵_응답 = new RoadmapForListResponse(두번째_로드맵_아이디, "두 번째 로드맵",
                "로드맵 소개글", "DIFFICULT", 30, 두번째_로드맵.createdAt(),
                new MemberResponse(저장된_크리에이터_아이디, "코끼리"), new RoadmapCategoryResponse(여행_카테고리.getId(), "여행"),
                List.of(new RoadmapTagResponse(2L, "태그")));

        final List<RoadmapForListResponse> 예상되는_로드맵_리스트_응답 = List.of(두번째_로드맵_응답, 첫번째_로드맵_응답);

        assertThat(로드맵_리스트_응답)
                .isEqualTo(예상되는_로드맵_리스트_응답);
    }

    @Test
    void 카테고리_아이디에_따라_로드맵_목록을_조회한다() {
        // given
        final Long 저장된_크리에이터_아이디 = 크리에이터를_저장한다(IDENTIFIER, NICKNAME);
        final String 로그인_토큰_정보 = 로그인(new LoginRequest(IDENTIFIER, PASSWORD));

        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");
        final RoadmapCategory 게임_카테고리 = 로드맵_카테고리를_저장한다("게임");

        final Long 첫번째_로드맵_아이디 = 제목별로_로드맵을_생성한다(로그인_토큰_정보, 여행_카테고리, "첫 번째 로드맵");
        final Long 두번째_로드맵_아이디 = 제목별로_로드맵을_생성한다(로그인_토큰_정보, 여행_카테고리, "두 번째 로드맵");
        제목별로_로드맵을_생성한다(로그인_토큰_정보, 게임_카테고리, "세 번째 로드맵");

        final RoadmapResponse 첫번째_로드맵 = 로드맵을_아이디로_조회한다(첫번째_로드맵_아이디);
        final RoadmapResponse 두번째_로드맵 = 로드맵을_아이디로_조회한다(두번째_로드맵_아이디);

        // when
        final List<RoadmapForListResponse> 로드맵_리스트_응답 = given()
                .log().all()
                .when()
                .get("/api/roadmaps?size=10&categoryId=" + 여행_카테고리.getId())
                .then().log().all()
                .extract()
                .response()
                .as(new TypeRef<>() {
                });

        // then
        final RoadmapForListResponse 첫번째_로드맵_응답 = new RoadmapForListResponse(첫번째_로드맵_아이디, "첫 번째 로드맵",
                "로드맵 소개글", "DIFFICULT", 30, 첫번째_로드맵.createdAt(),
                new MemberResponse(저장된_크리에이터_아이디, "코끼리"), new RoadmapCategoryResponse(여행_카테고리.getId(), "여행"),
                List.of(new RoadmapTagResponse(1L, "태그")));

        final RoadmapForListResponse 두번째_로드맵_응답 = new RoadmapForListResponse(두번째_로드맵_아이디, "두 번째 로드맵",
                "로드맵 소개글", "DIFFICULT", 30, 두번째_로드맵.createdAt(),
                new MemberResponse(저장된_크리에이터_아이디, "코끼리"), new RoadmapCategoryResponse(여행_카테고리.getId(), "여행"),
                List.of(new RoadmapTagResponse(2L, "태그")));

        final List<RoadmapForListResponse> 예상되는_로드맵_리스트_응답 = List.of(두번째_로드맵_응답, 첫번째_로드맵_응답);

        assertThat(로드맵_리스트_응답)
                .isEqualTo(예상되는_로드맵_리스트_응답);
    }

    @Test
    void 최신순으로_로드맵_목록을_조회한다() {
        // given
        final Long 저장된_크리에이터_아이디 = 크리에이터를_저장한다(IDENTIFIER, NICKNAME);
        final String 로그인_토큰_정보 = 로그인(new LoginRequest(IDENTIFIER, PASSWORD));

        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");
        final RoadmapCategory 게임_카테고리 = 로드맵_카테고리를_저장한다("게임");

        final Long 첫번째_로드맵_아이디 = 제목별로_로드맵을_생성한다(로그인_토큰_정보, 여행_카테고리, "첫 번째 로드맵");
        final Long 두번째_로드맵_아이디 = 제목별로_로드맵을_생성한다(로그인_토큰_정보, 여행_카테고리, "두 번째 로드맵");
        final Long 세번째_로드맵_아이디 = 제목별로_로드맵을_생성한다(로그인_토큰_정보, 게임_카테고리, "세 번째 로드맵");

        final RoadmapResponse 첫번째_로드맵 = 로드맵을_아이디로_조회한다(첫번째_로드맵_아이디);
        final RoadmapResponse 두번째_로드맵 = 로드맵을_아이디로_조회한다(두번째_로드맵_아이디);
        final RoadmapResponse 세번째_로드맵 = 로드맵을_아이디로_조회한다(세번째_로드맵_아이디);

        // when
        final List<RoadmapForListResponse> 로드맵_리스트_응답 = given()
                .log().all()
                .when()
                .get("/api/roadmaps?lastCreatedAt=" + 세번째_로드맵.createdAt() + "&size=10&filterType=LATEST")
                .then().log().all()
                .extract()
                .response()
                .as(new TypeRef<>() {
                });

        // then
        final RoadmapForListResponse 첫번째_로드맵_응답 = new RoadmapForListResponse(첫번째_로드맵_아이디, "첫 번째 로드맵",
                "로드맵 소개글", "DIFFICULT", 30, 첫번째_로드맵.createdAt(),
                new MemberResponse(저장된_크리에이터_아이디, "코끼리"), new RoadmapCategoryResponse(여행_카테고리.getId(), "여행"),
                List.of(new RoadmapTagResponse(1L, "태그")));

        final RoadmapForListResponse 두번째_로드맵_응답 = new RoadmapForListResponse(두번째_로드맵_아이디, "두 번째 로드맵",
                "로드맵 소개글", "DIFFICULT", 30, 두번째_로드맵.createdAt(),
                new MemberResponse(저장된_크리에이터_아이디, "코끼리"), new RoadmapCategoryResponse(여행_카테고리.getId(), "여행"),
                List.of(new RoadmapTagResponse(2L, "태그")));

        final List<RoadmapForListResponse> 예상되는_로드맵_리스트_응답 = List.of(두번째_로드맵_응답, 첫번째_로드맵_응답);

        assertThat(로드맵_리스트_응답)
                .isEqualTo(예상되는_로드맵_리스트_응답);
    }

    @Test
    void 사이즈_조건으로_로드맵_목록을_조회한다() {
        // given
        final Long 저장된_크리에이터_아이디 = 크리에이터를_저장한다(IDENTIFIER, NICKNAME);
        final String 로그인_토큰_정보 = 로그인(new LoginRequest(IDENTIFIER, PASSWORD));

        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");
        final RoadmapCategory 게임_카테고리 = 로드맵_카테고리를_저장한다("게임");

        final Long 첫번째_로드맵_아이디 = 제목별로_로드맵을_생성한다(로그인_토큰_정보, 여행_카테고리, "첫 번째 로드맵");
        final Long 두번째_로드맵_아이디 = 제목별로_로드맵을_생성한다(로그인_토큰_정보, 여행_카테고리, "두 번째 로드맵");
        final Long 세번째_로드맵_아이디 = 제목별로_로드맵을_생성한다(로그인_토큰_정보, 게임_카테고리, "세 번째 로드맵");

        final RoadmapResponse 첫번째_로드맵 = 로드맵을_아이디로_조회한다(첫번째_로드맵_아이디);
        final RoadmapResponse 두번째_로드맵 = 로드맵을_아이디로_조회한다(두번째_로드맵_아이디);
        final RoadmapResponse 세번째_로드맵 = 로드맵을_아이디로_조회한다(세번째_로드맵_아이디);

        // when
        final List<RoadmapForListResponse> 로드맵_리스트_응답 = given()
                .log().all()
                .when()
                .get("/api/roadmaps?size=10")
                .then().log().all()
                .extract()
                .response()
                .as(new TypeRef<>() {
                });

        // then
        final RoadmapForListResponse 첫번째_로드맵_응답 = new RoadmapForListResponse(첫번째_로드맵_아이디, "첫 번째 로드맵",
                "로드맵 소개글", "DIFFICULT", 30, 첫번째_로드맵.createdAt(),
                new MemberResponse(저장된_크리에이터_아이디, "코끼리"), new RoadmapCategoryResponse(여행_카테고리.getId(), "여행"),
                List.of(new RoadmapTagResponse(1L, "태그")));

        final RoadmapForListResponse 두번째_로드맵_응답 = new RoadmapForListResponse(두번째_로드맵_아이디, "두 번째 로드맵",
                "로드맵 소개글", "DIFFICULT", 30, 두번째_로드맵.createdAt(),
                new MemberResponse(저장된_크리에이터_아이디, "코끼리"), new RoadmapCategoryResponse(여행_카테고리.getId(), "여행"),
                List.of(new RoadmapTagResponse(2L, "태그")));

        final RoadmapForListResponse 세번째_로드맵_응답 = new RoadmapForListResponse(세번째_로드맵_아이디, "세 번째 로드맵",
                "로드맵 소개글", "DIFFICULT", 30, 세번째_로드맵.createdAt(),
                new MemberResponse(저장된_크리에이터_아이디, "코끼리"), new RoadmapCategoryResponse(게임_카테고리.getId(), "게임"),
                List.of(new RoadmapTagResponse(3L, "태그")));
        final List<RoadmapForListResponse> 예상되는_로드맵_리스트_응답 = List.of(세번째_로드맵_응답, 두번째_로드맵_응답, 첫번째_로드맵_응답);

        assertThat(로드맵_리스트_응답)
                .isEqualTo(예상되는_로드맵_리스트_응답);
    }

    @Test
    void 로드맵_조회시_사이즈_조건을_주지_않으면_예외가_발생한다() {
        // when
        final List<ErrorResponse> 예외_메시지 = given()
                .log().all()
                .when()
                .get("/api/roadmaps")
                .then().log().all()
                .extract()
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

    @Test
    void 로드맵을_제목을_기준으로_검색한다() {
        // given
        final Long 저장된_크리에이터_아이디 = 크리에이터를_저장한다(IDENTIFIER, NICKNAME);
        final String 로그인_토큰_정보 = 로그인(new LoginRequest(IDENTIFIER, PASSWORD));

        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");

        final Long 첫번째_로드맵_아이디 = 제목별로_로드맵을_생성한다(로그인_토큰_정보, 여행_카테고리, "첫 번째 로드맵");
        제목별로_로드맵을_생성한다(로그인_토큰_정보, 여행_카테고리, "두 번째 로드");
        final Long 세번째_로드맵_아이디 = 제목별로_로드맵을_생성한다(로그인_토큰_정보, 여행_카테고리, "세 번째 로드맵");

        final RoadmapResponse 첫번째_로드맵 = 로드맵을_아이디로_조회한다(첫번째_로드맵_아이디);
        final RoadmapResponse 세번째_로드맵 = 로드맵을_아이디로_조회한다(세번째_로드맵_아이디);

        // when
        final List<RoadmapForListResponse> 로드맵_리스트_응답 = given()
                .log().all()
                .when()
                .get("/api/roadmaps/search?size=10&filterType=LATEST&roadmapTitle=로드맵")
                .then().log().all()
                .extract()
                .response()
                .as(new TypeRef<>() {
                });

        // then
        final RoadmapForListResponse 첫번째_로드맵_응답 = new RoadmapForListResponse(첫번째_로드맵_아이디, "첫 번째 로드맵",
                "로드맵 소개글", "DIFFICULT", 30, 첫번째_로드맵.createdAt(),
                new MemberResponse(저장된_크리에이터_아이디, "코끼리"), new RoadmapCategoryResponse(여행_카테고리.getId(), "여행"),
                List.of(new RoadmapTagResponse(1L, "태그")));

        final RoadmapForListResponse 세번째_로드맵_응답 = new RoadmapForListResponse(세번째_로드맵_아이디, "세 번째 로드맵",
                "로드맵 소개글", "DIFFICULT", 30, 세번째_로드맵.createdAt(),
                new MemberResponse(저장된_크리에이터_아이디, "코끼리"), new RoadmapCategoryResponse(여행_카테고리.getId(), "여행"),
                List.of(new RoadmapTagResponse(3L, "태그")));

        final List<RoadmapForListResponse> 예상되는_로드맵_리스트_응답 = List.of(세번째_로드맵_응답, 첫번째_로드맵_응답);

        assertThat(로드맵_리스트_응답)
                .isEqualTo(예상되는_로드맵_리스트_응답);
    }

    @Test
    void 로드맵을_크리에이터_기준으로_검색한다() {
        // given
        final Long 저장된_첫번째_크리에이터_아이디 = 크리에이터를_저장한다(IDENTIFIER, NICKNAME);
        final String 첫번째_크리에이터_토큰_정보 = 로그인(new LoginRequest(IDENTIFIER, PASSWORD));

        크리에이터를_저장한다("cocokirikiri", "코코끼리끼리");
        final String 두번째_크리에이터_토큰_정보 = 로그인(new LoginRequest("cocokirikiri", PASSWORD));

        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");

        final Long 첫번째_로드맵_아이디 = 제목별로_로드맵을_생성한다(첫번째_크리에이터_토큰_정보, 여행_카테고리, "첫 번째 로드맵");
        final Long 두번째_로드맵_아이디 = 제목별로_로드맵을_생성한다(첫번째_크리에이터_토큰_정보, 여행_카테고리, "두 번째 로드맵");
        제목별로_로드맵을_생성한다(두번째_크리에이터_토큰_정보, 여행_카테고리, "세 번째 로드맵");

        final RoadmapResponse 첫번째_로드맵 = 로드맵을_아이디로_조회한다(첫번째_로드맵_아이디);
        final RoadmapResponse 두번째_로드맵 = 로드맵을_아이디로_조회한다(두번째_로드맵_아이디);

        // when
        final List<RoadmapForListResponse> 로드맵_리스트_응답 = given()
                .log().all()
                .when()
                .get("/api/roadmaps/search?size=10&filterType=LATEST&creatorId=" + 저장된_첫번째_크리에이터_아이디)
                .then().log().all()
                .extract()
                .response()
                .as(new TypeRef<>() {
                });

        // then
        final RoadmapForListResponse 첫번째_로드맵_응답 = new RoadmapForListResponse(첫번째_로드맵_아이디, "첫 번째 로드맵",
                "로드맵 소개글", "DIFFICULT", 30, 첫번째_로드맵.createdAt(),
                new MemberResponse(저장된_첫번째_크리에이터_아이디, "코끼리"), new RoadmapCategoryResponse(여행_카테고리.getId(), "여행"),
                List.of(new RoadmapTagResponse(1L, "태그")));

        final RoadmapForListResponse 두번째_로드맵_응답 = new RoadmapForListResponse(두번째_로드맵_아이디, "두 번째 로드맵",
                "로드맵 소개글", "DIFFICULT", 30, 두번째_로드맵.createdAt(),
                new MemberResponse(저장된_첫번째_크리에이터_아이디, "코끼리"), new RoadmapCategoryResponse(여행_카테고리.getId(), "여행"),
                List.of(new RoadmapTagResponse(2L, "태그")));

        final List<RoadmapForListResponse> 예상되는_로드맵_리스트_응답 = List.of(두번째_로드맵_응답, 첫번째_로드맵_응답);

        assertThat(로드맵_리스트_응답)
                .isEqualTo(예상되는_로드맵_리스트_응답);
    }

    @Test
    void 로드맵을_태그_이름을_기준으로_검색한다() {
        // given
        final Long 저장된_크리에이터_아이디 = 크리에이터를_저장한다(IDENTIFIER, NICKNAME);
        final String 로그인_토큰_정보 = 로그인(new LoginRequest(IDENTIFIER, PASSWORD));

        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");

        태그없이_로드맵을_생성한다(로그인_토큰_정보, 여행_카테고리, "첫 번째 로드맵");
        final Long 두번째_로드맵_아이디 = 제목별로_로드맵을_생성한다(로그인_토큰_정보, 여행_카테고리, "두 번째 로드맵");
        final Long 세번째_로드맵_아이디 = 제목별로_로드맵을_생성한다(로그인_토큰_정보, 여행_카테고리, "세 번째 로드맵");

        final RoadmapResponse 두번째_로드맵 = 로드맵을_아이디로_조회한다(두번째_로드맵_아이디);
        final RoadmapResponse 세번째_로드맵 = 로드맵을_아이디로_조회한다(세번째_로드맵_아이디);

        // when
        final List<RoadmapForListResponse> 로드맵_리스트_응답 = given()
                .log().all()
                .when()
                .get("/api/roadmaps/search?size=10&filterType=LATEST&tagName=태그")
                .then().log().all()
                .extract()
                .response()
                .as(new TypeRef<>() {
                });

        // then
        final RoadmapForListResponse 두번째_로드맵_응답 = new RoadmapForListResponse(두번째_로드맵_아이디, "두 번째 로드맵",
                "로드맵 소개글", "DIFFICULT", 30, 두번째_로드맵.createdAt(),
                new MemberResponse(저장된_크리에이터_아이디, "코끼리"), new RoadmapCategoryResponse(여행_카테고리.getId(), "여행"),
                List.of(new RoadmapTagResponse(1L, "태그")));

        final RoadmapForListResponse 세번째_로드맵_응답 = new RoadmapForListResponse(세번째_로드맵_아이디, "세 번째 로드맵",
                "로드맵 소개글", "DIFFICULT", 30, 세번째_로드맵.createdAt(),
                new MemberResponse(저장된_크리에이터_아이디, "코끼리"), new RoadmapCategoryResponse(여행_카테고리.getId(), "여행"),
                List.of(new RoadmapTagResponse(2L, "태그")));

        final List<RoadmapForListResponse> 예상되는_로드맵_리스트_응답 = List.of(세번째_로드맵_응답, 두번째_로드맵_응답);

        assertThat(로드맵_리스트_응답)
                .isEqualTo(예상되는_로드맵_리스트_응답);
    }

    @Test
    void 사용자가_생성한_로드맵을_조회한다() {
        // given
        final Long 저장된_크리에이터_아이디 = 크리에이터를_저장한다(IDENTIFIER, NICKNAME);
        final String 로그인_토큰_정보 = 로그인(new LoginRequest(IDENTIFIER, PASSWORD));

        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");
        final RoadmapCategory 게임_카테고리 = 로드맵_카테고리를_저장한다("게임");

        제목별로_로드맵을_생성한다(로그인_토큰_정보, 여행_카테고리, "첫 번째 로드맵");
        제목별로_로드맵을_생성한다(로그인_토큰_정보, 여행_카테고리, "두 번째 로드맵");
        제목별로_로드맵을_생성한다(로그인_토큰_정보, 게임_카테고리, "세 번째 로드맵");

        // when
        final List<MemberRoadmapResponse> 사용자_로드맵_응답_리스트 = given()
                .log().all()
                .when()
                .header(HttpHeaders.AUTHORIZATION, 로그인_토큰_정보)
                .get("/api/roadmaps/me?size=10")
                .then().log().all()
                .extract()
                .response()
                .as(new TypeRef<>() {
                });

        // then
        final List<MemberRoadmapResponse> 예상되는_사용자_로드맵_응답_리스트 = List.of(
                new MemberRoadmapResponse(3L, "세 번째 로드맵", RoadmapDifficulty.DIFFICULT.name(), LocalDateTime.now(),
                        new RoadmapCategoryResponse(2L, "게임")),
                new MemberRoadmapResponse(2L, "두 번째 로드맵", RoadmapDifficulty.DIFFICULT.name(), LocalDateTime.now(),
                        new RoadmapCategoryResponse(1L, "여행")),
                new MemberRoadmapResponse(1L, "첫 번째 로드맵", RoadmapDifficulty.DIFFICULT.name(), LocalDateTime.now(),
                        new RoadmapCategoryResponse(1L, "여행")));

        assertThat(사용자_로드맵_응답_리스트).usingRecursiveComparison()
                .ignoringFields("createdAt")
                .isEqualTo(예상되는_사용자_로드맵_응답_리스트);
    }

    @Test
    void 사용자가_생성한_로드맵을_이전에_받아온_리스트_이후로_조회한다() {
        // given
        final Long 저장된_크리에이터_아이디 = 크리에이터를_저장한다(IDENTIFIER, NICKNAME);
        final String 로그인_토큰_정보 = 로그인(new LoginRequest(IDENTIFIER, PASSWORD));

        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");
        final RoadmapCategory 게임_카테고리 = 로드맵_카테고리를_저장한다("게임");

        final Long 첫번째_로드맵_아이디 = 제목별로_로드맵을_생성한다(로그인_토큰_정보, 여행_카테고리, "첫 번째 로드맵");
        final Long 두번째_로드맵_아이디 = 제목별로_로드맵을_생성한다(로그인_토큰_정보, 여행_카테고리, "두 번째 로드맵");
        final Long 세번째_로드맵_아이디 = 제목별로_로드맵을_생성한다(로그인_토큰_정보, 게임_카테고리, "세 번째 로드맵");

        final RoadmapResponse 첫번째_로드맵 = 로드맵을_아이디로_조회한다(첫번째_로드맵_아이디);
        final RoadmapResponse 두번째_로드맵 = 로드맵을_아이디로_조회한다(두번째_로드맵_아이디);
        final RoadmapResponse 세번째_로드맵 = 로드맵을_아이디로_조회한다(세번째_로드맵_아이디);

        // when
        final List<MemberRoadmapResponse> 사용자_로드맵_응답_리스트 = given()
                .log().all()
                .when()
                .header(HttpHeaders.AUTHORIZATION, 로그인_토큰_정보)
                .get("/api/roadmaps/me?lastCreatedAt=" + 두번째_로드맵.createdAt() + "&size=10")
                .then().log().all()
                .extract()
                .response()
                .as(new TypeRef<>() {
                });

        // then
        final List<MemberRoadmapResponse> 예상되는_사용자_로드맵_응답_리스트 = List.of(
                new MemberRoadmapResponse(1L, "첫 번째 로드맵", RoadmapDifficulty.DIFFICULT.name(), LocalDateTime.now(),
                        new RoadmapCategoryResponse(1L, "여행")));

        assertThat(사용자_로드맵_응답_리스트).usingRecursiveComparison()
                .ignoringFields("createdAt")
                .isEqualTo(예상되는_사용자_로드맵_응답_리스트);
    }

    @Test
    void 골룸을_최신순으로_조회한다() throws JsonProcessingException {
        // given
        final GoalRoomCreateRequest 골룸_생성_요청 = 로드맵을_생성하고_그에_따른_골룸을_생성할_요청을_만든다();
        final String 골룸1_리더_액세스_토큰 = 회원을_생성하고_로그인을_한다(골룸_참여자1_회원가입_요청, 골룸_참여자1_로그인_요청);
        final String 골룸2_리더_액세스_토큰 = 회원을_생성하고_로그인을_한다(골룸_참여자2_회원가입_요청, 골룸_참여자2_로그인_요청);

        final Long 골룸1_아이디 = 골룸을_생성하고_아이디를_반환한다(골룸_생성_요청, 골룸1_리더_액세스_토큰);
        final GoalRoom 골룸1 = goalRoomRepository.findById(골룸1_아이디).get();
        final ExtractableResponse<Response> 골룸1_단일_조회_응답 = 골룸_단일_조회_요청(골룸1_리더_액세스_토큰, 골룸1_아이디);
        final GoalRoomResponse 골룸1_단일_조회_응답값 = jsonToClass(골룸1_단일_조회_응답.asString(), new TypeReference<>() {
        });

        final Long 골룸2_아이디 = 골룸을_생성하고_아이디를_반환한다(골룸_생성_요청, 골룸2_리더_액세스_토큰);
        final GoalRoom 골룸2 = goalRoomRepository.findById(골룸2_아이디).get();
        final ExtractableResponse<Response> 골룸2_단일_조회_응답 = 골룸_단일_조회_요청(골룸2_리더_액세스_토큰, 골룸2_아이디);
        final GoalRoomResponse 골룸2_단일_조회_응답값 = jsonToClass(골룸2_단일_조회_응답.asString(), new TypeReference<>() {
        });

        final Member 골룸1에_참여한_사용자 = memberRepository.findByIdentifier(new Identifier("identifier2")).get();
        final Member 골룸2에_참여한_사용자 = memberRepository.findByIdentifier(new Identifier("identifier3")).get();

        final RoadmapGoalRoomResponse 골룸1_예상_응답값 = new RoadmapGoalRoomResponse(1L, 골룸1.getName().getValue(),
                골룸1_단일_조회_응답값.currentMemberCount(), 골룸1.getLimitedMemberCount().getValue(), 골룸1.getCreatedAt(),
                골룸1.getStartDate(), 골룸1.getEndDate(),
                new MemberResponse(골룸1에_참여한_사용자.getId(), 골룸1에_참여한_사용자.getNickname().getValue()));
        final RoadmapGoalRoomResponse 골룸2_예상_응답값 = new RoadmapGoalRoomResponse(2L, 골룸2.getName().getValue(),
                골룸2_단일_조회_응답값.currentMemberCount(), 골룸2.getLimitedMemberCount().getValue(), 골룸2.getCreatedAt(),
                골룸2.getStartDate(), 골룸2.getEndDate(),
                new MemberResponse(골룸2에_참여한_사용자.getId(), 골룸2에_참여한_사용자.getNickname().getValue()));
        final List<RoadmapGoalRoomResponse> 최신순_골룸_목록_조회_예상_응답값 = List.of(골룸2_예상_응답값, 골룸1_예상_응답값);

        // when
        final ExtractableResponse<Response> 최신순_골룸_목록_조회_응답 = 골룸_목록을_조회한다(골룸_목록_조회할_로드맵_아이디, null, 10,
                RoadmapGoalRoomsFilterTypeDto.LATEST.name());

        // then
        final List<RoadmapGoalRoomResponse> 최신순_골룸_목록_조회_응답값 = jsonToClass(최신순_골룸_목록_조회_응답.asString(),
                new TypeReference<>() {
                });

        assertThat(최신순_골룸_목록_조회_응답값).isEqualTo(최신순_골룸_목록_조회_예상_응답값);
    }

    @Test
    void 골룸을_참가율_순으로_조회한다() throws JsonProcessingException {
        // given
        final GoalRoomCreateRequest 골룸_생성_요청 = 로드맵을_생성하고_그에_따른_골룸을_생성할_요청을_만든다();
        final String 골룸1_리더_액세스_토큰 = 회원을_생성하고_로그인을_한다(골룸_참여자1_회원가입_요청, 골룸_참여자1_로그인_요청);
        final String 골룸2_리더_액세스_토큰 = 회원을_생성하고_로그인을_한다(골룸_참여자2_회원가입_요청, 골룸_참여자2_로그인_요청);

        final Long 골룸1_아이디 = 골룸을_생성하고_아이디를_반환한다(골룸_생성_요청, 골룸1_리더_액세스_토큰);
        골룸_참가_요청(골룸1_아이디, 골룸2_리더_액세스_토큰);
        final GoalRoom 골룸1 = goalRoomRepository.findById(골룸1_아이디).get();
        final ExtractableResponse<Response> 골룸1_단일_조회_응답 = 골룸_단일_조회_요청(골룸1_리더_액세스_토큰, 골룸1_아이디);
        final GoalRoomResponse 골룸1_단일_조회_응답값 = jsonToClass(골룸1_단일_조회_응답.asString(), new TypeReference<>() {
        });

        final Long 골룸2_아이디 = 골룸을_생성하고_아이디를_반환한다(골룸_생성_요청, 골룸2_리더_액세스_토큰);
        final GoalRoom 골룸2 = goalRoomRepository.findById(골룸2_아이디).get();
        final ExtractableResponse<Response> 골룸2_단일_조회_응답 = 골룸_단일_조회_요청(골룸2_리더_액세스_토큰, 골룸2_아이디);
        final GoalRoomResponse 골룸2_단일_조회_응답값 = jsonToClass(골룸2_단일_조회_응답.asString(), new TypeReference<>() {
        });

        final Member 골룸1에_참여한_사용자 = memberRepository.findByIdentifier(new Identifier("identifier2")).get();
        final Member 골룸2에_참여한_사용자 = memberRepository.findByIdentifier(new Identifier("identifier3")).get();

        final RoadmapGoalRoomResponse 골룸1_예상_응답값 = new RoadmapGoalRoomResponse(1L, 골룸1.getName().getValue(),
                골룸1_단일_조회_응답값.currentMemberCount(), 골룸1.getLimitedMemberCount().getValue(), 골룸1.getCreatedAt(),
                골룸1.getStartDate(), 골룸1.getEndDate(),
                new MemberResponse(골룸1에_참여한_사용자.getId(), 골룸1에_참여한_사용자.getNickname().getValue()));
        final RoadmapGoalRoomResponse 골룸2_예상_응답값 = new RoadmapGoalRoomResponse(2L, 골룸2.getName().getValue(),
                골룸2_단일_조회_응답값.currentMemberCount(), 골룸2.getLimitedMemberCount().getValue(), 골룸2.getCreatedAt(),
                골룸2.getStartDate(), 골룸2.getEndDate(),
                new MemberResponse(골룸2에_참여한_사용자.getId(), 골룸2에_참여한_사용자.getNickname().getValue()));
        final List<RoadmapGoalRoomResponse> 참가율순_골룸_목록_조회_예상_응답값 = List.of(골룸1_예상_응답값, 골룸2_예상_응답값);

        // when
        final ExtractableResponse<Response> 참가율순_골룸_목록_조회_응답 = 골룸_목록을_조회한다(골룸_목록_조회할_로드맵_아이디, null, 10,
                RoadmapGoalRoomsFilterTypeDto.PARTICIPATION_RATE.name());

        // then
        final List<RoadmapGoalRoomResponse> 참가율순_골룸_목록_조회_응답값 = jsonToClass(참가율순_골룸_목록_조회_응답.asString(),
                new TypeReference<>() {
                });

        assertThat(참가율순_골룸_목록_조회_응답값).isEqualTo(참가율순_골룸_목록_조회_예상_응답값);
    }

    private Long 크리에이터를_저장한다(final String 아이디, final String 닉네임) {
        final MemberJoinRequest 회원가입_요청 = new MemberJoinRequest(아이디, PASSWORD, 닉네임, "010-1234-5678",
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

    private String 회원을_생성하고_로그인을_한다(final MemberJoinRequest memberRequest, final LoginRequest loginRequest) {
        회원가입(memberRequest);
        return 로그인(loginRequest);
    }

    private ExtractableResponse<Response> 회원가입(final MemberJoinRequest 회원가입_요청) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(회원가입_요청)
                .post(API_PREFIX + "/members/join")
                .then()
                .log().all()
                .extract();
    }

    private String 로그인(final LoginRequest 로그인_요청) {
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

    private Long 태그없이_로드맵을_생성한다(final String 로그인_토큰_정보, final RoadmapCategory 로드맵_카테고리, final String 로드맵_제목) {
        final RoadmapSaveRequest 로드맵_저장_요청 = new RoadmapSaveRequest(로드맵_카테고리.getId(), 로드맵_제목, "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30, List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용")),
                Collections.emptyList());

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

    private RoadmapResponse 로드맵을_아이디로_조회한다(final Long 로드맵_아이디) {
        return given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(API_PREFIX + "/roadmaps/{roadmapId}", 로드맵_아이디)
                .then()
                .log().all()
                .extract()
                .as(new TypeRef<>() {
                });
    }

    private GoalRoomCreateRequest 로드맵을_생성하고_그에_따른_골룸을_생성할_요청을_만든다() throws JsonProcessingException {
        final String 액세스_토큰 = 회원을_생성하고_로그인을_한다(회원가입_요청, 로그인_요청);
        final RoadmapCategory 카테고리 = 로드맵_카테고리를_저장한다(카테고리_이름);
        final RoadmapSaveRequest 로드맵_생성_요청 = new RoadmapSaveRequest(카테고리.getId(), "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30, List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용")),
                Collections.emptyList());
        final Long 로드맵_id = 로드맵을_생성하고_id를_알아낸다(액세스_토큰, 로드맵_생성_요청);
        골룸_목록_조회할_로드맵_아이디 = 로드맵_id;
        final RoadmapNode 로드맵_노드 = 로드맵_노드();

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_노드.getId(), 1, 오늘, 십일_후));

        final ExtractableResponse<Response> 단일_로드맵_조회_응답 = 단일_로드맵_조회_요청(로드맵_id);
        final RoadmapResponse 단일_로드맵_조회_응답_바디 = jsonToClass(단일_로드맵_조회_응답.asString(), new TypeReference<>() {
        });
        final Long 로드맵_본문_아이디 = 단일_로드맵_조회_응답_바디.content().id();
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_본문_아이디, 정상적인_골룸_이름, 정상적인_골룸_제한_인원,
                골룸_투두_요청,
                골룸_노드_별_기간_요청);

        return 골룸_생성_요청;
    }

    private Long 로드맵을_생성하고_id를_알아낸다(final String 액세스_토큰, final RoadmapSaveRequest 로드맵_생성_요청) {
        final ExtractableResponse<Response> 로드맵_응답 = 로드맵_생성(로드맵_생성_요청, 액세스_토큰);
        final String Location_헤더 = 로드맵_응답.response().header("Location");
        final Long 로드맵_아이디 = Long.parseLong(Location_헤더.substring(14));
        return 로드맵_아이디;
    }

    private RoadmapNode 로드맵_노드() {
        return roadmapNodeRepository.findAll().get(0);
    }

    private ExtractableResponse<Response> 로드맵_생성(final RoadmapSaveRequest 로드맵_생성_요청, final String 액세스_토큰) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(로드맵_생성_요청)
                .header(new Header(HttpHeaders.AUTHORIZATION, 액세스_토큰))
                .post(API_PREFIX + "/roadmaps")
                .then()
                .log().all()
                .extract();
    }

    private ExtractableResponse<Response> 단일_로드맵_조회_요청(final Long 로드맵_아이디) {
        return given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(API_PREFIX + "/roadmaps/{roadmapId}", 로드맵_아이디)
                .then()
                .log().all()
                .extract();
    }

    private Long 골룸을_생성하고_아이디를_반환한다(final GoalRoomCreateRequest 골룸_생성_요청, final String 액세스_토큰) {
        final String 골룸_생성_응답_Location_헤더 = 골룸_생성(골룸_생성_요청, 액세스_토큰).response().getHeader(LOCATION);
        return Long.parseLong(골룸_생성_응답_Location_헤더.substring(16));
    }

    private ExtractableResponse<Response> 골룸_생성(final GoalRoomCreateRequest 골룸_생성_요청, final String 액세스_토큰) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(골룸_생성_요청)
                .header(new Header(HttpHeaders.AUTHORIZATION, 액세스_토큰))
                .post(API_PREFIX + "/goal-rooms")
                .then()
                .log().all()
                .extract();
    }

    private ExtractableResponse<Response> 골룸_참가_요청(final Long 골룸_아이디, final String 팔로워_액세스_토큰) {
        return given()
                .log().all()
                .header(AUTHORIZATION, 팔로워_액세스_토큰)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .post(API_PREFIX + "/goal-rooms/{goalRoomId}/join", 골룸_아이디)
                .then()
                .log().all()
                .extract();
    }

    private ExtractableResponse<Response> 골룸_단일_조회_요청(final String 로그인_토큰_정보, final Long 골룸_아이디) {
        return given()
                .header(AUTHORIZATION, 로그인_토큰_정보)
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(API_PREFIX + "/goal-rooms/{goalRoomId}", 골룸_아이디)
                .then()
                .log().all()
                .extract();
    }

    private ExtractableResponse<Response> 골룸_목록을_조회한다(final Long roadmapId, final LocalDateTime lastValue,
                                                      final int size, final String filterCond) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("roadmapId", roadmapId)
                .param("lastCreatedAt", lastValue)
                .param("size", size)
                .param("filterCond", filterCond)
                .when()
                .get(API_PREFIX + "/roadmaps/{roadmapId}/goal-rooms", roadmapId)
                .then().log().all()
                .extract();
    }

    private Roadmap 로드맵_응답으로부터_로드맵_본문을_생성한다(final Member 크리에이터, final RoadmapCategory 카테고리,
                                            final RoadmapResponse 로드맵_응답) {
        final Roadmap 로드맵 = new Roadmap(로드맵_응답.roadmapId(), 로드맵_응답.roadmapTitle(), 로드맵_응답.introduction(),
                로드맵_응답.recommendedRoadmapPeriod(), RoadmapDifficulty.valueOf(로드맵_응답.difficulty()), 크리에이터, 카테고리);
        final RoadmapContentResponse 로드맵_본문_응답 = 로드맵_응답.content();
        final RoadmapContent 로드맵_본문 = new RoadmapContent(로드맵_본문_응답.content());
        final List<RoadmapNodeResponse> 로드맵_본문_노드_응답 = 로드맵_본문_응답.nodes();
        final List<RoadmapNode> 로드맵_노드_리스트 = 로드맵_본문_노드_응답.stream()
                .map(response -> new RoadmapNode(response.title(), response.description()))
                .toList();

        로드맵_본문.addNodes(new RoadmapNodes(로드맵_노드_리스트));
        로드맵.addContent(로드맵_본문);

        return roadmapRepository.save(로드맵);
    }

    private GoalRoom 상태별_골룸을_생성한다(final List<RoadmapContent> 로드맵_본문_리스트, final Member 리더, final GoalRoomStatus 골룸_상태) {
        final RoadmapContent 로드맵_본문 = 로드맵_본문_리스트.get(0);
        final GoalRoom 골룸 = new GoalRoom(new GoalRoomName("골룸"), new LimitedMemberCount(10), 로드맵_본문, 리더);
        final List<RoadmapNode> 로드맵_노드_리스트 = 로드맵_본문.getNodes().getValues();

        final RoadmapNode 첫번째_로드맵_노드 = 로드맵_노드_리스트.get(0);
        final GoalRoomRoadmapNode 첫번째_골룸_노드 = new GoalRoomRoadmapNode(
                new Period(LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(5)), 3, 첫번째_로드맵_노드);

        final GoalRoomRoadmapNodes 골룸_노드들 = new GoalRoomRoadmapNodes(List.of(첫번째_골룸_노드));
        골룸.addAllGoalRoomRoadmapNodes(골룸_노드들);
        골룸.updateStatus(골룸_상태);
        return goalRoomRepository.save(골룸);
    }
}
