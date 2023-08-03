package co.kirikiri.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import co.kirikiri.domain.ImageContentType;
import co.kirikiri.domain.member.EncryptedPassword;
import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberImage;
import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.integration.helper.IntegrationTest;
import co.kirikiri.persistence.goalroom.GoalRoomPendingMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.persistence.roadmap.RoadmapCategoryRepository;
import co.kirikiri.persistence.roadmap.RoadmapContentRepository;
import co.kirikiri.persistence.roadmap.RoadmapNodeRepository;
import co.kirikiri.persistence.roadmap.RoadmapRepository;
import co.kirikiri.service.GoalRoomCreateService;
import co.kirikiri.service.dto.auth.request.LoginRequest;
import co.kirikiri.service.dto.auth.response.AuthenticationResponse;
import co.kirikiri.service.dto.goalroom.request.GoalRoomCreateRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomRoadmapNodeRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomTodoRequest;
import co.kirikiri.service.dto.goalroom.response.GoalRoomCertifiedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomNodeResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomToDoCheckResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomTodoResponse;
import co.kirikiri.service.dto.member.request.GenderType;
import co.kirikiri.service.dto.member.request.MemberJoinRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapDifficultyType;
import co.kirikiri.service.dto.roadmap.request.RoadmapNodeSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapTagSaveRequest;
import co.kirikiri.service.dto.roadmap.response.RoadmapResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.Header;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.io.File;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

class GoalRoomReadIntegrationTest extends IntegrationTest {

    private static final int 정상적인_골룸_제한_인원 = 20;
    private static final String 정상적인_골룸_이름 = "GOAL_ROOM_NAME";
    private static final String 정상적인_골룸_투두_컨텐츠 = "GOAL_ROOM_TO_DO_CONTENT";
    private static final String BEARER = "Bearer ";
    private static final String IDENTIFIER = "identifier1";
    private static final String PASSWORD = "password1!";
    private static final MemberJoinRequest 회원가입_요청 = new MemberJoinRequest("ab12", "password12!@#$%", "nickname",
            "010-1234-5678",
            GenderType.MALE, LocalDate.of(2023, Month.JULY, 12));
    private static final LoginRequest 로그인_요청 = new LoginRequest(회원가입_요청.identifier(), 회원가입_요청.password());
    private static final LocalDate 오늘 = LocalDate.now();
    private static final LocalDate 십일_후 = 오늘.plusDays(10L);
    private static final LocalDate 이십일_후 = 오늘.plusDays(20);
    private static final LocalDate 삼십일_후 = 오늘.plusDays(30);
    private static final int 정상적인_골룸_노드_인증_횟수 = (int) ChronoUnit.DAYS.between(오늘, 십일_후);

    private final MemberRepository memberRepository;
    private final RoadmapRepository roadmapRepository;
    private final GoalRoomRepository goalRoomRepository;
    private final RoadmapNodeRepository roadmapNodeRepository;
    private final GoalRoomCreateService goalRoomCreateService;
    private final RoadmapContentRepository roadmapContentRepository;
    private final RoadmapCategoryRepository roadmapCategoryRepository;
    private final GoalRoomPendingMemberRepository goalRoomPendingMemberRepository;

    public GoalRoomReadIntegrationTest(final RoadmapRepository roadmapRepository,
                                       final GoalRoomRepository goalRoomRepository,
                                       final RoadmapNodeRepository roadmapNodeRepository,
                                       final GoalRoomCreateService goalRoomCreateService,
                                       final RoadmapContentRepository roadmapContentRepository,
                                       final RoadmapCategoryRepository roadmapCategoryRepository,
                                       final GoalRoomPendingMemberRepository goalRoomPendingMemberRepository,
                                       final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
        this.roadmapRepository = roadmapRepository;
        this.goalRoomRepository = goalRoomRepository;
        this.goalRoomCreateService = goalRoomCreateService;
        this.roadmapNodeRepository = roadmapNodeRepository;
        this.roadmapContentRepository = roadmapContentRepository;
        this.roadmapCategoryRepository = roadmapCategoryRepository;
        this.goalRoomPendingMemberRepository = goalRoomPendingMemberRepository;
    }

    @Test
    void 골룸_아이디로_골룸_정보를_조회한다() {
        // given
        크리에이터를_저장한다();
        final String 로그인_토큰_정보 = 로그인();
        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");
        final Long 로드맵_아이디 = 제목별로_로드맵을_생성한다(로그인_토큰_정보, 여행_카테고리, "첫 번째 로드맵");
        final RoadmapResponse 로드맵_응답 = 로드맵을_조회한다(로드맵_아이디);
        final Long 로드맵_첫번째_노드_아이디 = 로드맵_응답.content().nodes().get(0).id();
        final Long 로드맵_두번째_노드_아이디 = 로드맵_응답.content().nodes().get(1).id();

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest("투두 1", 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_첫번째_노드_아이디, (int) ChronoUnit.DAYS.between(오늘, 십일_후), 오늘, 십일_후),
                new GoalRoomRoadmapNodeRequest(로드맵_두번째_노드_아이디, (int) ChronoUnit.DAYS.between(이십일_후, 삼십일_후), 이십일_후,
                        삼십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_아이디, "골룸", 10, 골룸_투두_요청, 골룸_노드_별_기간_요청);
        final Long 골룸_아이디 = 골룸_생성(골룸_생성_요청, 로그인_토큰_정보);

        // when
        final GoalRoomResponse 골룸_응답값 = given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(API_PREFIX + "/goal-rooms/{goalRoomId}", 골룸_아이디)
                .then()
                .log().all()
                .extract()
                .as(new TypeRef<>() {
                });

        // then
        final GoalRoomResponse 예상하는_골룸_응답값 = 예상하는_골룸_응답을_생성한다();
        assertThat(골룸_응답값)
                .isEqualTo(예상하는_골룸_응답값);
    }

    @Test
    void 골룸_아이디와_사용자_아이디로_골룸_정보를_조회한다() {
        // given
        크리에이터를_저장한다();
        final String 로그인_토큰_정보 = 로그인();
        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");
        final Long 로드맵_아이디 = 제목별로_로드맵을_생성한다(로그인_토큰_정보, 여행_카테고리, "첫 번째 로드맵");
        final RoadmapResponse 로드맵_응답 = 로드맵을_조회한다(로드맵_아이디);
        final Long 로드맵_첫번째_노드_아이디 = 로드맵_응답.content().nodes().get(0).id();
        final Long 로드맵_두번째_노드_아이디 = 로드맵_응답.content().nodes().get(1).id();

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest("투두 1", 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_첫번째_노드_아이디, (int) ChronoUnit.DAYS.between(오늘, 십일_후), 오늘, 십일_후),
                new GoalRoomRoadmapNodeRequest(로드맵_두번째_노드_아이디, (int) ChronoUnit.DAYS.between(이십일_후, 삼십일_후), 이십일_후,
                        삼십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_아이디, "골룸", 10, 골룸_투두_요청, 골룸_노드_별_기간_요청);
        final Long 골룸_아이디 = 골룸_생성(골룸_생성_요청, 로그인_토큰_정보);

        // when
        final GoalRoomCertifiedResponse 골룸_응답값 = given()
                .header(AUTHORIZATION, 로그인_토큰_정보)
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(API_PREFIX + "/goal-rooms/{goalRoomId}", 골룸_아이디)
                .then()
                .log().all()
                .extract()
                .as(new TypeRef<>() {
                });

        // then
        final GoalRoomCertifiedResponse 예상하는_골룸_응답값 = 로그인후_예상하는_골룸_응답을_생성한다();
        assertThat(골룸_응답값)
                .isEqualTo(예상하는_골룸_응답값);
    }

    @Test
    void 골룸_투두리스트를_조회한다() {
        // given
        크리에이터를_저장한다();
        final String 로그인_토큰_정보 = 로그인();
        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");
        final Long 로드맵_아이디 = 제목별로_로드맵을_생성한다(로그인_토큰_정보, 여행_카테고리, "첫 번째 로드맵");
        final RoadmapResponse 로드맵_응답 = 로드맵을_조회한다(로드맵_아이디);
        final Long 로드맵_첫번째_노드_아이디 = 로드맵_응답.content().nodes().get(0).id();
        final Long 로드맵_두번째_노드_아이디 = 로드맵_응답.content().nodes().get(1).id();

        final GoalRoomTodoRequest 골룸_첫번째_투두_요청 = new GoalRoomTodoRequest("투두 1", 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_첫번째_노드_아이디, (int) ChronoUnit.DAYS.between(오늘, 십일_후), 오늘, 십일_후),
                new GoalRoomRoadmapNodeRequest(로드맵_두번째_노드_아이디, (int) ChronoUnit.DAYS.between(이십일_후, 삼십일_후), 이십일_후,
                        삼십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_아이디, "골룸", 10, 골룸_첫번째_투두_요청,
                골룸_노드_별_기간_요청);
        final Long 골룸_아이디 = 골룸_생성(골룸_생성_요청, 로그인_토큰_정보);

        goalRoomCreateService.startGoalRooms();

        final GoalRoomTodoRequest 골룸_두번째_투두_요청 = new GoalRoomTodoRequest("투두 2", 십일_후, 이십일_후);
        골룸_투두리스트_추가(로그인_토큰_정보, 골룸_아이디, 골룸_두번째_투두_요청);

        // TODO 아이디 값에 대한 변환 필요
        골룸_투두리스트를_체크한다(로그인_토큰_정보, 골룸_아이디, 1L);

        // when
        final List<GoalRoomTodoResponse> 골룸_투두리스트_응답값 = given()
                .header(AUTHORIZATION, 로그인_토큰_정보)
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(API_PREFIX + "/goal-rooms/{goalRoomId}/todos", 골룸_아이디)
                .then()
                .log().all()
                .extract()
                .as(new TypeRef<>() {
                });

        // then
        final List<GoalRoomTodoResponse> 예상하는_골룸_투두리스트_응답값 = 예상하는_골룸_투두리스트_응답을_생성한다();
        assertThat(골룸_투두리스트_응답값)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(예상하는_골룸_투두리스트_응답값);
    }

    private Member 크리에이터를_저장한다() {
        final String 닉네임 = "코끼리";
        final String 전화번호 = "010-1234-5678";
        final LocalDate 생년월일 = LocalDate.of(2023, Month.JULY, 12);
        final MemberJoinRequest 회원가입_요청 = new MemberJoinRequest(IDENTIFIER, PASSWORD, 닉네임, 전화번호, GenderType.MALE, 생년월일);

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

        return new Member(Long.valueOf(저장된_크리에이터_아이디), new Identifier(IDENTIFIER),
                new EncryptedPassword(new Password(PASSWORD)), new Nickname(닉네임),
                new MemberImage("originalFileName", "serverFilePath", ImageContentType.JPEG),
                new MemberProfile(Gender.MALE, 생년월일, 전화번호));
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

    private RoadmapCategory 로드맵_카테고리를_저장한다(final String 카테고리_이름) {
        final RoadmapCategory 로드맵_카테고리 = new RoadmapCategory(카테고리_이름);
        return roadmapCategoryRepository.save(로드맵_카테고리);
    }

    private Long 제목별로_로드맵을_생성한다(final String 로그인_토큰_정보, final RoadmapCategory 로드맵_카테고리, final String 로드맵_제목) {
        final RoadmapSaveRequest 로드맵_저장_요청 = new RoadmapSaveRequest(
                로드맵_카테고리.getId(), 로드맵_제목, "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30, List.of(
                new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용"),
                new RoadmapNodeSaveRequest("로드맵 2주차", "로드맵 2주차 내용")),
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

    private RoadmapResponse 로드맵을_조회한다(final Long 로드맵_아이디) {
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

    private GoalRoomResponse 예상하는_골룸_응답을_생성한다() {
        final List<GoalRoomNodeResponse> goalRoomNodeResponses = List.of(
                new GoalRoomNodeResponse("로드맵 1주차", 오늘, 십일_후, 10),
                new GoalRoomNodeResponse("로드맵 2주차", 이십일_후, 삼십일_후, 10));
        return new GoalRoomResponse("골룸", 1, 10, goalRoomNodeResponses, 31);
    }

    private GoalRoomCertifiedResponse 로그인후_예상하는_골룸_응답을_생성한다() {
        final List<GoalRoomNodeResponse> goalRoomNodeResponses = List.of(
                new GoalRoomNodeResponse("로드맵 1주차", 오늘, 십일_후, 10),
                new GoalRoomNodeResponse("로드맵 2주차", 이십일_후, 삼십일_후, 10));
        return new GoalRoomCertifiedResponse("골룸", 1, 10, goalRoomNodeResponses, 31, true);
    }

    private ExtractableResponse<Response> 회원가입_요청(final MemberJoinRequest 회원가입_요청값) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(회원가입_요청값)
                .post(API_PREFIX + "/members/join")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 로그인_요청(final LoginRequest 로그인_요청값) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(로그인_요청값)
                .post(API_PREFIX + "/auth/login")
                .then().log().all()
                .extract();
    }

    private String access_token을_받는다(final ExtractableResponse<Response> 로그인_응답) throws JsonProcessingException {
        final AuthenticationResponse 토큰_응답값 = jsonToClass(로그인_응답.body().asString(), new TypeReference<>() {
        });
        return 토큰_응답값.accessToken();
    }

    private Long 골룸_생성(final GoalRoomCreateRequest 골룸_생성_요청, final String 액세스_토큰) {
        final String 생성된_골룸_아이디 = given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(골룸_생성_요청)
                .header(new Header(HttpHeaders.AUTHORIZATION, 액세스_토큰))
                .post(API_PREFIX + "/goal-rooms")
                .then()
                .log().all()
                .extract()
                .response()
                .getHeader(LOCATION)
                .replace("/api/goal-rooms/", "");
        return Long.valueOf(생성된_골룸_아이디);
    }

    private Long 골룸_투두리스트_추가(final String 액세스_토큰, final Long 골룸_아이디, final GoalRoomTodoRequest 골룸_추가_요청) {
        final String 응답_로케이션_헤더 = API_PREFIX + "/goal-rooms/" + 골룸_아이디 + "/todos/";
        final String 생성된_투두_아이디 = given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(골룸_추가_요청)
                .header(new Header(HttpHeaders.AUTHORIZATION, 액세스_토큰))
                .post(API_PREFIX + "/goal-rooms/{goalRoomId}/todos", 골룸_아이디)
                .then()
                .log().all()
                .extract()
                .response()
                .getHeader(LOCATION)
                .replace(응답_로케이션_헤더, "");
        return Long.valueOf(생성된_투두_아이디);
    }

    private GoalRoomToDoCheckResponse 골룸_투두리스트를_체크한다(final String 로그인_토큰_정보, final Long 골룸_아이디, final Long 투두_아이디) {
        return given()
                .header(AUTHORIZATION, 로그인_토큰_정보)
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(API_PREFIX + "/goal-rooms/{goalRoomId}/todos/{todoId}", 골룸_아이디, 투두_아이디)
                .then()
                .log().all()
                .extract()
                .as(new TypeRef<>() {
                });
    }

    // TODO 골룸 단일 조회 명세 수정 후 아이디 값도 함께 반환되도록 수정할 예정
    private List<GoalRoomTodoResponse> 예상하는_골룸_투두리스트_응답을_생성한다() {
        return List.of(
                new GoalRoomTodoResponse(null, "투두 1", 오늘, 십일_후, new GoalRoomToDoCheckResponse(true)),
                new GoalRoomTodoResponse(null, "투두 2", 십일_후, 이십일_후, new GoalRoomToDoCheckResponse(false)));
    }

    private ExtractableResponse<Response> 로드맵_생성_요청(final RoadmapSaveRequest 로드맵_생성_요청값, final String accessToken) {
        return given().log().all()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(로드맵_생성_요청값).log().all()
                .post(API_PREFIX + "/roadmaps")
                .then().log().all()
                .extract();
    }

    private void 테스트용으로_생성된_파일을_제거한다(final String filePath) {
        final File file = new File(filePath);

        if (!file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("Invalid file path: " + filePath);
        }

        if (!file.delete()) {
            throw new RuntimeException("Failed to delete the file: " + filePath);
        }
    }
}
