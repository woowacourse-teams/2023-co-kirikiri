package co.kirikiri.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import co.kirikiri.domain.ImageContentType;
import co.kirikiri.domain.ImageDirType;
import co.kirikiri.domain.goalroom.CheckFeed;
import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNodes;
import co.kirikiri.domain.goalroom.vo.GoalRoomName;
import co.kirikiri.domain.goalroom.vo.LimitedMemberCount;
import co.kirikiri.domain.goalroom.vo.Period;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.domain.roadmap.RoadmapNode;
import co.kirikiri.integration.helper.IntegrationTest;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.persistence.roadmap.RoadmapCategoryRepository;
import co.kirikiri.service.FilePathGenerator;
import co.kirikiri.service.GoalRoomCreateService;
import co.kirikiri.service.GoalRoomScheduler;
import co.kirikiri.service.dto.ErrorResponse;
import co.kirikiri.service.dto.auth.request.LoginRequest;
import co.kirikiri.service.dto.auth.response.AuthenticationResponse;
import co.kirikiri.service.dto.goalroom.request.CheckFeedRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomCreateRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomRoadmapNodeRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomTodoRequest;
import co.kirikiri.service.dto.goalroom.response.CheckFeedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomCertifiedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomCheckFeedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomRoadmapNodeResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomRoadmapNodesResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomToDoCheckResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomTodoResponse;
import co.kirikiri.service.dto.member.request.GenderType;
import co.kirikiri.service.dto.member.request.MemberJoinRequest;
import co.kirikiri.service.dto.member.response.MemberGoalRoomForListResponse;
import co.kirikiri.service.dto.member.response.MemberGoalRoomResponse;
import co.kirikiri.service.dto.member.response.MemberNameAndImageResponse;
import co.kirikiri.service.dto.member.response.MemberResponse;
import co.kirikiri.service.dto.roadmap.request.RoadmapDifficultyType;
import co.kirikiri.service.dto.roadmap.request.RoadmapNodeSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapTagSaveRequest;
import co.kirikiri.service.dto.roadmap.response.RoadmapResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.Header;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class GoalRoomReadIntegrationTest extends IntegrationTest {

    private static final int 정상적인_골룸_제한_인원 = 20;
    private static final String 정상적인_골룸_이름 = "GOAL_ROOM_NAME";
    private static final String 정상적인_골룸_투두_컨텐츠 = "GOAL_ROOM_TO_DO_CONTENT";
    private static final String IDENTIFIER = "identifier1";
    private static final String PASSWORD = "password1!";
    private static final LocalDate 오늘 = LocalDate.now();
    private static final LocalDate 십일_후 = 오늘.plusDays(10L);
    private static final LocalDate 이십일_후 = 오늘.plusDays(20);
    private static final LocalDate 삼십일_후 = 오늘.plusDays(30);
    private static final int 오늘부터_십일까지_인증_횟수 = (int) ChronoUnit.DAYS.between(오늘, 십일_후);
    private static final int 이십일부터_삼십일까지_인증_횟수 = (int) ChronoUnit.DAYS.between(이십일_후, 삼십일_후);

    private final String storageLocation;
    private final String serverPathPrefix;
    private final FilePathGenerator filePathGenerator;
    private final RoadmapCategoryRepository roadmapCategoryRepository;
    private final GoalRoomRepository goalRoomRepository;
    private final GoalRoomCreateService goalRoomCreateService;
    private final GoalRoomScheduler goalRoomScheduler;

    public GoalRoomReadIntegrationTest(@Value("${file.upload-dir}") final String storageLocation,
                                       @Value("${file.server-path}") final String serverPathPrefix,
                                       final FilePathGenerator filePathGenerator,
                                       final RoadmapCategoryRepository roadmapCategoryRepository,
                                       final GoalRoomRepository goalRoomRepository,
                                       final GoalRoomCreateService goalRoomCreateService,
                                       final GoalRoomScheduler goalRoomScheduler) {
        this.storageLocation = storageLocation;
        this.serverPathPrefix = serverPathPrefix;
        this.filePathGenerator = filePathGenerator;
        this.roadmapCategoryRepository = roadmapCategoryRepository;
        this.goalRoomRepository = goalRoomRepository;
        this.goalRoomCreateService = goalRoomCreateService;
        this.goalRoomScheduler = goalRoomScheduler;
    }

    @Test
    void 골룸_아이디로_골룸_정보를_조회한다() {
        // given
        회원가입을_한다(IDENTIFIER, PASSWORD, "코끼리", "010-1234-5678", GenderType.MALE, LocalDate.of(2023, Month.JULY, 12));
        final String 로그인_토큰_정보 = 로그인을_한다(IDENTIFIER, PASSWORD);

        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");
        final Long 로드맵_아이디 = 제목별로_로드맵을_생성한다(로그인_토큰_정보, 여행_카테고리, "첫 번째 로드맵");
        final RoadmapResponse 로드맵_응답 = 로드맵을_조회한다(로드맵_아이디);
        final Long 로드맵_첫번째_노드_아이디 = 로드맵_응답.content().nodes().get(0).id();
        final Long 로드맵_두번째_노드_아이디 = 로드맵_응답.content().nodes().get(1).id();

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest("투두 1", 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_첫번째_노드_아이디, 오늘부터_십일까지_인증_횟수, 오늘, 십일_후),
                new GoalRoomRoadmapNodeRequest(로드맵_두번째_노드_아이디, 이십일부터_삼십일까지_인증_횟수, 이십일_후, 삼십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_아이디, "골룸", 10, 골룸_투두_요청, 골룸_노드_별_기간_요청);
        final Long 골룸_아이디 = 골룸을_생성하고_아이디를_알아낸다(골룸_생성_요청, 로그인_토큰_정보);

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
        회원가입을_한다(IDENTIFIER, PASSWORD, "코끼리", "010-1234-5678", GenderType.MALE, LocalDate.of(2023, Month.JULY, 12));
        final String 로그인_토큰_정보 = 로그인을_한다(IDENTIFIER, PASSWORD);

        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");
        final Long 로드맵_아이디 = 제목별로_로드맵을_생성한다(로그인_토큰_정보, 여행_카테고리, "첫 번째 로드맵");
        final RoadmapResponse 로드맵_응답 = 로드맵을_조회한다(로드맵_아이디);
        final Long 로드맵_첫번째_노드_아이디 = 로드맵_응답.content().nodes().get(0).id();
        final Long 로드맵_두번째_노드_아이디 = 로드맵_응답.content().nodes().get(1).id();

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest("투두 1", 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_첫번째_노드_아이디, 오늘부터_십일까지_인증_횟수, 오늘, 십일_후),
                new GoalRoomRoadmapNodeRequest(로드맵_두번째_노드_아이디, 이십일부터_삼십일까지_인증_횟수, 이십일_후, 삼십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_아이디, "골룸", 10, 골룸_투두_요청, 골룸_노드_별_기간_요청);
        final Long 골룸_아이디 = 골룸을_생성하고_아이디를_알아낸다(골룸_생성_요청, 로그인_토큰_정보);

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
        회원가입을_한다(IDENTIFIER, PASSWORD, "코끼리", "010-1234-5678", GenderType.MALE, LocalDate.of(2023, Month.JULY, 12));
        final String 로그인_토큰_정보 = 로그인을_한다(IDENTIFIER, PASSWORD);

        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");
        final Long 로드맵_아이디 = 제목별로_로드맵을_생성한다(로그인_토큰_정보, 여행_카테고리, "첫 번째 로드맵");
        final RoadmapResponse 로드맵_응답 = 로드맵을_조회한다(로드맵_아이디);
        final Long 로드맵_첫번째_노드_아이디 = 로드맵_응답.content().nodes().get(0).id();
        final Long 로드맵_두번째_노드_아이디 = 로드맵_응답.content().nodes().get(1).id();

        final GoalRoomTodoRequest 골룸_첫번째_투두_요청 = new GoalRoomTodoRequest("투두 1", 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_첫번째_노드_아이디, 오늘부터_십일까지_인증_횟수, 오늘, 십일_후),
                new GoalRoomRoadmapNodeRequest(로드맵_두번째_노드_아이디, 이십일부터_삼십일까지_인증_횟수, 이십일_후, 삼십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_아이디, "골룸", 10, 골룸_첫번째_투두_요청,
                골룸_노드_별_기간_요청);
        final Long 골룸_아이디 = 골룸을_생성하고_아이디를_알아낸다(골룸_생성_요청, 로그인_토큰_정보);

        final GoalRoomTodoRequest 골룸_두번째_투두_요청 = new GoalRoomTodoRequest("투두 2", 십일_후, 이십일_후);
        final Long 투두_아이디 = 골룸_투두리스트_추가(로그인_토큰_정보, 골룸_아이디, 골룸_두번째_투두_요청);

        골룸_참가_요청(골룸_아이디, 로그인_토큰_정보);
        골룸을_시작한다();
        final MemberGoalRoomResponse 사용자_단일_골룸_조회_응답 = 사용자의_특정_골룸_정보를_조회한다(로그인_토큰_정보, 골룸_아이디);

        골룸_투두리스트를_체크한다(로그인_토큰_정보, 골룸_아이디, 투두_아이디);

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
        final List<GoalRoomTodoResponse> 예상하는_골룸_투두리스트_응답값 = 예상하는_골룸_투두리스트_응답을_생성한다(사용자_단일_골룸_조회_응답.goalRoomTodos());
        assertThat(골룸_투두리스트_응답값)
                .isEqualTo(예상하는_골룸_투두리스트_응답값);
    }

    @Test
    void 골룸_투두리스트_조회시_존재하지_않은_골룸일_경우() {
        // given
        회원가입을_한다(IDENTIFIER, PASSWORD, "코끼리", "010-1234-5678", GenderType.MALE, LocalDate.of(2023, Month.JULY, 12));
        final String 로그인_토큰_정보 = 로그인을_한다(IDENTIFIER, PASSWORD);

        // when
        final ErrorResponse 예외_응답 = given()
                .header(AUTHORIZATION, 로그인_토큰_정보)
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(API_PREFIX + "/goal-rooms/{goalRoomId}/todos", 1L)
                .then()
                .log().all()
                .extract()
                .as(new TypeRef<>() {
                });

        // then
        assertThat(예외_응답)
                .isEqualTo(new ErrorResponse("존재하지 않는 골룸입니다. goalRoomId = 1"));
    }

    @Test
    void 골룸_투두리스트_조회시_참여하지_않은_사용자일_경우() {
        // given
        회원가입을_한다(IDENTIFIER, PASSWORD, "코끼리", "010-1234-5678", GenderType.MALE, LocalDate.of(2023, Month.JULY, 12));
        final String 로그인_토큰_정보 = 로그인을_한다(IDENTIFIER, PASSWORD);

        회원가입을_한다("identifier2", "password2!", "끼리코",
                "010-1111-2222", GenderType.MALE, LocalDate.of(1999, 9, 9));
        final String 다른_사용자_토큰_정보 = 로그인을_한다("identifier2", "password2!");

        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");
        final Long 로드맵_아이디 = 제목별로_로드맵을_생성한다(로그인_토큰_정보, 여행_카테고리, "첫 번째 로드맵");
        final RoadmapResponse 로드맵_응답 = 로드맵을_조회한다(로드맵_아이디);
        final Long 로드맵_첫번째_노드_아이디 = 로드맵_응답.content().nodes().get(0).id();
        final Long 로드맵_두번째_노드_아이디 = 로드맵_응답.content().nodes().get(1).id();

        final GoalRoomTodoRequest 골룸_첫번째_투두_요청 = new GoalRoomTodoRequest("투두 1", 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_첫번째_노드_아이디, 오늘부터_십일까지_인증_횟수, 오늘, 십일_후),
                new GoalRoomRoadmapNodeRequest(로드맵_두번째_노드_아이디, 이십일부터_삼십일까지_인증_횟수, 이십일_후, 삼십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_아이디, "골룸", 10, 골룸_첫번째_투두_요청,
                골룸_노드_별_기간_요청);
        final Long 골룸_아이디 = 골룸을_생성하고_아이디를_알아낸다(골룸_생성_요청, 로그인_토큰_정보);

        골룸을_시작한다();

        final GoalRoomTodoRequest 골룸_두번째_투두_요청 = new GoalRoomTodoRequest("투두 2", 십일_후, 이십일_후);
        final Long 투두_아이디 = 골룸_투두리스트_추가(로그인_토큰_정보, 골룸_아이디, 골룸_두번째_투두_요청);

        골룸_투두리스트를_체크한다(로그인_토큰_정보, 골룸_아이디, 투두_아이디);

        // when
        final ErrorResponse 예외_응답 = given()
                .header(AUTHORIZATION, 다른_사용자_토큰_정보)
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
        assertThat(예외_응답)
                .isEqualTo(new ErrorResponse("골룸에 참여하지 않은 사용자입니다. goalRoomId = " + 골룸_아이디 +
                        " memberIdentifier = identifier2"));
    }

    @Test
    void 진행_중인_사용자_단일_골룸을_조회한다() throws IOException {
        // given
        회원가입을_한다(IDENTIFIER, PASSWORD, "코끼리", "010-1234-5678", GenderType.MALE, LocalDate.of(2023, Month.JULY, 12));
        final String 로그인_토큰_정보 = 로그인을_한다(IDENTIFIER, PASSWORD);

        final RoadmapCategory 카테고리 = 로드맵_카테고리를_저장한다("여가");
        final Long 로드맵_아이디 = 로드맵을_생성한다(로그인_토큰_정보, 카테고리.getId(), "로드맵 제목", "로드맵 소개글",
                "로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용"),
                        new RoadmapNodeSaveRequest("로드맵 2주차", "로드맵 2주차 내용")));

        final RoadmapResponse 로드맵_응답 = 로드맵을_조회한다(로드맵_아이디);
        final Long 로드맵_첫번째_노드_아이디 = 로드맵_응답.content().nodes().get(0).id();
        final Long 로드맵_두번째_노드_아이디 = 로드맵_응답.content().nodes().get(1).id();

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_첫번째_노드_아이디, 오늘부터_십일까지_인증_횟수, 오늘, 십일_후),
                new GoalRoomRoadmapNodeRequest(로드맵_두번째_노드_아이디, 오늘부터_십일까지_인증_횟수, 십일_후.plusDays(1), 이십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_아이디, 정상적인_골룸_이름,
                정상적인_골룸_제한_인원, 골룸_투두_요청, 골룸_노드_별_기간_요청);
        final Long 골룸_아이디 = 골룸을_생성하고_아이디를_알아낸다(골룸_생성_요청, 로그인_토큰_정보);

        회원가입을_한다("identifier2", "password2@", "팔로워", "010-1234-5555", GenderType.FEMALE, LocalDate.of(2000, 1, 1));
        final String 팔로워_액세스_토큰 = 로그인을_한다("identifier2", "password2@");

        골룸_참가_요청(골룸_아이디, 팔로워_액세스_토큰);
        골룸을_시작한다();

        final MockMultipartFile 가짜_이미지_객체 = new MockMultipartFile("image", "originalFileName.jpeg",
                "image/webp", "tempImage".getBytes());
        final CheckFeedRequest 인증_피드_등록_요청 = new CheckFeedRequest(가짜_이미지_객체, "image description");
        인증_피드_등록_요청(로그인_토큰_정보, 골룸_아이디, 인증_피드_등록_요청);
        인증_피드_등록_요청(팔로워_액세스_토큰, 골룸_아이디, 인증_피드_등록_요청);

        // when
        final MemberGoalRoomResponse 요청_응답값 = 사용자의_특정_골룸_정보를_조회한다(로그인_토큰_정보, 골룸_아이디);

        // then
        final MemberGoalRoomResponse 예상되는_응답 = new MemberGoalRoomResponse(정상적인_골룸_이름, "RUNNING", 1L,
                2, 정상적인_골룸_제한_인원, 오늘, 이십일_후, 1L,
                new GoalRoomRoadmapNodesResponse(false, false,
                        List.of(
                                new GoalRoomRoadmapNodeResponse(1L, "로드맵 1주차", 오늘, 십일_후, 오늘부터_십일까지_인증_횟수),
                                new GoalRoomRoadmapNodeResponse(2L, "로드맵 2주차", 십일_후.plusDays(1), 이십일_후,
                                        오늘부터_십일까지_인증_횟수))),
                List.of(new GoalRoomTodoResponse(1L, 정상적인_골룸_투두_컨텐츠, 오늘, 십일_후,
                        new GoalRoomToDoCheckResponse(false))),
                List.of(
                        new CheckFeedResponse(1L, "filePath1", "image description", LocalDateTime.now()),
                        new CheckFeedResponse(2L, "filePath1", "image description", LocalDateTime.now())
                ));

        assertThat(요청_응답값)
                .usingRecursiveComparison()
                .ignoringFields("checkFeeds.imageUrl", "checkFeeds.createdAt")
                .isEqualTo(예상되는_응답);
    }

    @Test
    void 모집_중인_사용자_단일_골룸_조회_시_인증_피드가_빈_응답을_반환한다() {
        // given
        회원가입을_한다(IDENTIFIER, PASSWORD, "코끼리", "010-1234-5678", GenderType.MALE, LocalDate.of(2023, Month.JULY, 12));
        final String 로그인_토큰_정보 = 로그인을_한다(IDENTIFIER, PASSWORD);

        final RoadmapCategory 카테고리 = 로드맵_카테고리를_저장한다("여가");
        final Long 로드맵_아이디 = 로드맵을_생성한다(로그인_토큰_정보, 카테고리.getId(), "로드맵 제목", "로드맵 소개글",
                "로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용")));

        final RoadmapResponse 로드맵_응답 = 로드맵을_조회한다(로드맵_아이디);
        final Long 로드맵_첫번째_노드_아이디 = 로드맵_응답.content().nodes().get(0).id();

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 십일_후, 이십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_첫번째_노드_아이디, 오늘부터_십일까지_인증_횟수, 십일_후, 이십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_아이디, 정상적인_골룸_이름,
                정상적인_골룸_제한_인원, 골룸_투두_요청, 골룸_노드_별_기간_요청);
        final Long 골룸_아이디 = 골룸을_생성하고_아이디를_알아낸다(골룸_생성_요청, 로그인_토큰_정보);

        //when
        final MemberGoalRoomResponse 요청_응답값 = 사용자의_특정_골룸_정보를_조회한다(로그인_토큰_정보, 골룸_아이디);

        //then
        final MemberGoalRoomResponse 예상되는_응답 = new MemberGoalRoomResponse(정상적인_골룸_이름, "RECRUITING", 1L,
                1, 정상적인_골룸_제한_인원, 십일_후, 이십일_후, 1L,
                new GoalRoomRoadmapNodesResponse(false, false,
                        List.of(new GoalRoomRoadmapNodeResponse(1L, "로드맵 1주차", 십일_후, 이십일_후, 오늘부터_십일까지_인증_횟수))),
                List.of(new GoalRoomTodoResponse(1L, 정상적인_골룸_투두_컨텐츠, 십일_후, 이십일_후,
                        new GoalRoomToDoCheckResponse(false))),
                Collections.emptyList());

        assertThat(요청_응답값)
                .usingRecursiveComparison()
                .ignoringFields("checkFeeds.imageUrl")
                .isEqualTo(예상되는_응답);
    }

    @Test
    void 사용자의_모든_골룸_목록을_조회한다() {
        // given
        회원가입을_한다(IDENTIFIER, PASSWORD, "코끼리", "010-1234-5678", GenderType.MALE, LocalDate.of(2023, Month.JULY, 12));
        final String 로그인_토큰_정보 = 로그인을_한다(IDENTIFIER, PASSWORD);

        final RoadmapCategory 카테고리 = 로드맵_카테고리를_저장한다("여가");
        final Long 첫번째_로드맵_아이디 = 로드맵을_생성한다(로그인_토큰_정보, 카테고리.getId(), "첫번째_로드맵 제목",
                "첫번째_로드맵 소개글", "첫번째_로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("첫번째_로드맵 1주차", "첫번째_로드맵 1주차 내용")));

        final RoadmapResponse 첫번째_로드맵_응답 = 로드맵을_조회한다(첫번째_로드맵_아이디);
        final Long 로드맵_첫번째_노드_아이디 = 첫번째_로드맵_응답.content().nodes().get(0).id();

        final GoalRoomTodoRequest 첫번째_골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 첫번째_골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_첫번째_노드_아이디, 오늘부터_십일까지_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 첫번째_골룸_생성_요청 = new GoalRoomCreateRequest(첫번째_로드맵_아이디, 정상적인_골룸_이름,
                정상적인_골룸_제한_인원, 첫번째_골룸_투두_요청, 첫번째_골룸_노드_별_기간_요청);
        final Long 첫번째_골룸_아이디 = 골룸을_생성하고_아이디를_알아낸다(첫번째_골룸_생성_요청, 로그인_토큰_정보);

        final Long 두번째_로드맵_아이디 = 로드맵을_생성한다(로그인_토큰_정보, 카테고리.getId(), "두번째_로드맵 제목",
                "두번째_로드맵 소개글", "두번째_로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("두번째_로드맵 1주차", "두번째_로드맵 1주차 내용")));
        final RoadmapResponse 두번째_로드맵_응답 = 로드맵을_조회한다(두번째_로드맵_아이디);
        final Long 두번째_로드맵_노드_아이디 = 두번째_로드맵_응답.content().nodes().get(0).id();

        final GoalRoomTodoRequest 두번째_골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 십일_후, 이십일_후);
        final List<GoalRoomRoadmapNodeRequest> 두번째_골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(두번째_로드맵_노드_아이디, 오늘부터_십일까지_인증_횟수, 십일_후, 이십일_후));
        final GoalRoomCreateRequest 두번째_골룸_생성_요청 = new GoalRoomCreateRequest(두번째_로드맵_아이디, 정상적인_골룸_이름,
                정상적인_골룸_제한_인원, 두번째_골룸_투두_요청, 두번째_골룸_노드_별_기간_요청);
        final Long 두번째_골룸_아이디 = 골룸을_생성하고_아이디를_알아낸다(두번째_골룸_생성_요청, 로그인_토큰_정보);

        골룸을_시작한다();

        // when
        final List<MemberGoalRoomForListResponse> 요청_응답값 = given().log().all()
                .header(AUTHORIZATION, 로그인_토큰_정보)
                .when()
                .get(API_PREFIX + "/goal-rooms/me")
                .then()
                .log().all()
                .extract()
                .as(new TypeRef<>() {
                });

        // then
        final List<MemberGoalRoomForListResponse> 예상되는_응답 = List.of(
                new MemberGoalRoomForListResponse(첫번째_골룸_아이디, 정상적인_골룸_이름, "RUNNING",
                        1, 정상적인_골룸_제한_인원, LocalDateTime.now(), 오늘, 십일_후,
                        new MemberResponse(1L, "코끼리", "default-member-image")),
                new MemberGoalRoomForListResponse(두번째_골룸_아이디, 정상적인_골룸_이름, "RECRUITING",
                        1, 정상적인_골룸_제한_인원, LocalDateTime.now(), 십일_후, 이십일_후,
                        new MemberResponse(1L, "코끼리", "default-member-image")));

        assertAll(
                () -> assertThat(요청_응답값)
                        .usingRecursiveComparison()
                        .ignoringFields("createdAt", "goalRoomLeader.imageUrl")
                        .isEqualTo(예상되는_응답),
                () -> assertThat(요청_응답값.get(0).goalRoomLeader().imageUrl())
                        .contains("default-member-image")
        );
    }

    @Test
    void 사용자가_참여한_골룸_중_모집_중인_골룸_목록을_조회한다() {
        // given
        회원가입을_한다("identifier", "password1!", "코끼리", "010-1111-2222", GenderType.MALE, LocalDate.of(1999, 9, 9));
        final String 액세스_토큰 = 로그인을_한다("identifier", "password1!");
        회원가입을_한다("follower1", "password1!", "팔로워", "010-1111-1111", GenderType.FEMALE, LocalDate.of(1111, 11, 11));
        final String 팔로워_액세스_토큰 = 로그인을_한다("follower1", "password1!");

        final RoadmapCategory 카테고리 = 로드맵_카테고리를_저장한다("여가");
        final Long 첫번째_로드맵_아이디 = 로드맵을_생성한다(액세스_토큰, 카테고리.getId(), "첫번째_로드맵 제목",
                "첫번째_로드맵 소개글", "첫번째_로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("첫번째_로드맵 1주차", "첫번째_로드맵 1주차 내용")));
        final RoadmapResponse 첫번째_로드맵_응답 = 로드맵을_조회한다(첫번째_로드맵_아이디);
        final Long 로드맵_첫번째_노드_아이디 = 첫번째_로드맵_응답.content().nodes().get(0).id();

        final GoalRoomTodoRequest 첫번째_골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 첫번째_골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_첫번째_노드_아이디, 오늘부터_십일까지_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 첫번째_골룸_생성_요청 = new GoalRoomCreateRequest(첫번째_로드맵_아이디, 정상적인_골룸_이름,
                정상적인_골룸_제한_인원, 첫번째_골룸_투두_요청, 첫번째_골룸_노드_별_기간_요청);
        final Long 첫번째_골룸_아이디 = 골룸을_생성하고_아이디를_알아낸다(첫번째_골룸_생성_요청, 액세스_토큰);

        final Long 두번째_로드맵_아이디 = 로드맵을_생성한다(액세스_토큰, 카테고리.getId(), "두번째_로드맵 제목",
                "두번째_로드맵 소개글", "두번째_로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("두번째_로드맵 1주차", "두번째_로드맵 1주차 내용")));
        final RoadmapResponse 두번째_로드맵_응답 = 로드맵을_조회한다(두번째_로드맵_아이디);
        final Long 두번째_로드맵_노드_아이디 = 두번째_로드맵_응답.content().nodes().get(0).id();

        final GoalRoomTodoRequest 두번째_골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 십일_후, 이십일_후);
        final List<GoalRoomRoadmapNodeRequest> 두번째_골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(두번째_로드맵_노드_아이디, 오늘부터_십일까지_인증_횟수, 십일_후, 이십일_후));
        final GoalRoomCreateRequest 두번째_골룸_생성_요청 = new GoalRoomCreateRequest(두번째_로드맵_아이디, 정상적인_골룸_이름,
                정상적인_골룸_제한_인원, 두번째_골룸_투두_요청, 두번째_골룸_노드_별_기간_요청);
        final Long 두번째_골룸_아이디 = 골룸을_생성하고_아이디를_알아낸다(두번째_골룸_생성_요청, 액세스_토큰);

        골룸_참가_요청(첫번째_골룸_아이디, 팔로워_액세스_토큰);
        골룸_참가_요청(두번째_골룸_아이디, 팔로워_액세스_토큰);
        골룸을_시작한다();

        // when
        final List<MemberGoalRoomForListResponse> 요청_응답값 = given().log().all()
                .header(AUTHORIZATION, 팔로워_액세스_토큰)
                .queryParam("statusCond", "RECRUITING")
                .when()
                .get(API_PREFIX + "/goal-rooms/me")
                .then()
                .log().all()
                .extract()
                .as(new TypeRef<>() {
                });

        // then
        final List<MemberGoalRoomForListResponse> 예상되는_응답 = List.of(
                new MemberGoalRoomForListResponse(두번째_골룸_아이디, 정상적인_골룸_이름, "RECRUITING",
                        2, 정상적인_골룸_제한_인원, LocalDateTime.now(), 십일_후, 이십일_후,
                        new MemberResponse(1L, "코끼리", "default-member-image")));

        assertAll(
                () -> assertThat(요청_응답값)
                        .usingRecursiveComparison()
                        .ignoringFields("createdAt", "goalRoomLeader.imageUrl")
                        .isEqualTo(예상되는_응답),
                () -> assertThat(요청_응답값.get(0).goalRoomLeader().imageUrl())
                        .contains("default-member-image")
        );
    }

    @Test
    void 사용자가_참여한_골룸_중_진행_중인_골룸_목록을_조회한다() {
        // given
        회원가입을_한다("identifier", "password1!", "코끼리", "010-1111-2222", GenderType.MALE, LocalDate.of(1999, 9, 9));
        final String 액세스_토큰 = 로그인을_한다("identifier", "password1!");
        회원가입을_한다("follower1", "password1!", "팔로워", "010-1111-1111", GenderType.FEMALE, LocalDate.of(1111, 11, 11));
        final String 팔로워_액세스_토큰 = 로그인을_한다("follower1", "password1!");

        final RoadmapCategory 카테고리 = 로드맵_카테고리를_저장한다("여가");
        final Long 첫번째_로드맵_아이디 = 로드맵을_생성한다(액세스_토큰, 카테고리.getId(), "첫번째_로드맵 제목",
                "첫번째_로드맵 소개글", "첫번째_로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("첫번째_로드맵 1주차", "첫번째_로드맵 1주차 내용")));
        final RoadmapResponse 첫번째_로드맵_응답 = 로드맵을_조회한다(첫번째_로드맵_아이디);
        final Long 로드맵_첫번째_노드_아이디 = 첫번째_로드맵_응답.content().nodes().get(0).id();

        final GoalRoomTodoRequest 첫번째_골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 첫번째_골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_첫번째_노드_아이디, 오늘부터_십일까지_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 첫번째_골룸_생성_요청 = new GoalRoomCreateRequest(첫번째_로드맵_아이디, 정상적인_골룸_이름,
                정상적인_골룸_제한_인원, 첫번째_골룸_투두_요청, 첫번째_골룸_노드_별_기간_요청);
        final Long 첫번째_골룸_아이디 = 골룸을_생성하고_아이디를_알아낸다(첫번째_골룸_생성_요청, 액세스_토큰);

        final Long 두번째_로드맵_아이디 = 로드맵을_생성한다(액세스_토큰, 카테고리.getId(), "두번째_로드맵 제목",
                "두번째_로드맵 소개글", "두번째_로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("두번째_로드맵 1주차", "두번째_로드맵 1주차 내용")));
        final RoadmapResponse 두번째_로드맵_응답 = 로드맵을_조회한다(두번째_로드맵_아이디);
        final Long 두번째_로드맵_노드_아이디 = 두번째_로드맵_응답.content().nodes().get(0).id();

        final GoalRoomTodoRequest 두번째_골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 십일_후, 이십일_후);
        final List<GoalRoomRoadmapNodeRequest> 두번째_골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(두번째_로드맵_노드_아이디, 오늘부터_십일까지_인증_횟수, 십일_후, 이십일_후));
        final GoalRoomCreateRequest 두번째_골룸_생성_요청 = new GoalRoomCreateRequest(두번째_로드맵_아이디, 정상적인_골룸_이름,
                정상적인_골룸_제한_인원, 두번째_골룸_투두_요청, 두번째_골룸_노드_별_기간_요청);
        final Long 두번째_골룸_아이디 = 골룸을_생성하고_아이디를_알아낸다(두번째_골룸_생성_요청, 액세스_토큰);

        골룸_참가_요청(첫번째_골룸_아이디, 팔로워_액세스_토큰);
        골룸_참가_요청(두번째_골룸_아이디, 팔로워_액세스_토큰);
        골룸을_시작한다();

        // when
        final List<MemberGoalRoomForListResponse> 요청_응답값 = given().log().all()
                .header(AUTHORIZATION, 팔로워_액세스_토큰)
                .queryParam("statusCond", "RUNNING")
                .when()
                .get(API_PREFIX + "/goal-rooms/me")
                .then()
                .log().all()
                .extract()
                .as(new TypeRef<>() {
                });

        // then
        final List<MemberGoalRoomForListResponse> 예상되는_응답 = List.of(
                new MemberGoalRoomForListResponse(첫번째_골룸_아이디, 정상적인_골룸_이름, "RUNNING",
                        2, 정상적인_골룸_제한_인원, LocalDateTime.now(), 오늘, 십일_후,
                        new MemberResponse(1L, "코끼리", "default-member-image")));

        assertAll(
                () -> assertThat(요청_응답값)
                        .usingRecursiveComparison()
                        .ignoringFields("createdAt", "goalRoomLeader.imageUrl")
                        .isEqualTo(예상되는_응답),
                () -> assertThat(요청_응답값.get(0).goalRoomLeader().imageUrl())
                        .contains("default-member-image")
        );
    }

    @Test
    void 골룸_노드를_조회한다() {
        // given
        회원가입을_한다(IDENTIFIER, PASSWORD, "코끼리", "010-1234-5678", GenderType.MALE, LocalDate.of(2023, Month.JULY, 12));
        final String 로그인_토큰_정보 = 로그인을_한다(IDENTIFIER, PASSWORD);

        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");
        final Long 로드맵_아이디 = 제목별로_로드맵을_생성한다(로그인_토큰_정보, 여행_카테고리, "첫 번째 로드맵");
        final RoadmapResponse 로드맵_응답 = 로드맵을_조회한다(로드맵_아이디);
        final Long 로드맵_첫번째_노드_아이디 = 로드맵_응답.content().nodes().get(0).id();
        final Long 로드맵_두번째_노드_아이디 = 로드맵_응답.content().nodes().get(1).id();

        final GoalRoomTodoRequest 골룸_첫번째_투두_요청 = new GoalRoomTodoRequest("투두 1", 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_첫번째_노드_아이디, 10, 오늘, 십일_후),
                new GoalRoomRoadmapNodeRequest(로드맵_두번째_노드_아이디, 5, 이십일_후, 삼십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_아이디, "골룸", 10, 골룸_첫번째_투두_요청,
                골룸_노드_별_기간_요청);
        final Long 골룸_아이디 = 골룸을_생성하고_아이디를_알아낸다(골룸_생성_요청, 로그인_토큰_정보);

        골룸_참가_요청(골룸_아이디, 로그인_토큰_정보);
        골룸을_시작한다();
        final MemberGoalRoomResponse 사용자_골룸_정보 = 사용자의_특정_골룸_정보를_조회한다(로그인_토큰_정보, 골룸_아이디);

        // when
        final List<GoalRoomRoadmapNodeResponse> 골룸_노드_응답값 = given()
                .header(AUTHORIZATION, 로그인_토큰_정보)
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(API_PREFIX + "/goal-rooms/{goalRoomId}/nodes", 골룸_아이디)
                .then()
                .log().all()
                .extract()
                .as(new TypeRef<>() {
                });

        // then
        final List<GoalRoomRoadmapNodeResponse> 예상하는_골룸_노드_응답값 = 예상하는_골룸_노드_응답을_생성한다(
                사용자_골룸_정보.goalRoomRoadmapNodes().nodes());
        assertThat(골룸_노드_응답값)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(예상하는_골룸_노드_응답값);
    }

    @Test
    void 골룸_노드_조회시_존재하지_않은_골룸일_경우_예외가_발생한다() {
        // given
        회원가입을_한다(IDENTIFIER, PASSWORD, "코끼리", "010-1234-5678", GenderType.MALE, LocalDate.of(2023, Month.JULY, 12));
        final String 로그인_토큰_정보 = 로그인을_한다(IDENTIFIER, PASSWORD);

        // when
        final ErrorResponse 예외_응답값 = given()
                .header(AUTHORIZATION, 로그인_토큰_정보)
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(API_PREFIX + "/goal-rooms/{goalRoomId}/nodes", 1L)
                .then()
                .log().all()
                .extract()
                .as(new TypeRef<>() {
                });

        // then
        assertThat(예외_응답값)
                .isEqualTo(new ErrorResponse("존재하지 않는 골룸입니다. goalRoomId = 1"));
    }

    @Test
    void 골룸_노드_조회시_참여하지_않은_사용자일_경우_예외가_발생한다() {
        // given
        회원가입을_한다(IDENTIFIER, PASSWORD, "코끼리", "010-1234-5678", GenderType.MALE, LocalDate.of(2023, Month.JULY, 12));
        final String 로그인_토큰_정보 = 로그인을_한다(IDENTIFIER, PASSWORD);

        회원가입을_한다("identifier2", "password2!", "끼리코",
                "010-1111-2222", GenderType.MALE, LocalDate.of(1999, 9, 9));
        final String 다른_사용자_토큰_정보 = 로그인을_한다("identifier2", "password2!");

        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");
        final Long 로드맵_아이디 = 제목별로_로드맵을_생성한다(로그인_토큰_정보, 여행_카테고리, "첫 번째 로드맵");
        final RoadmapResponse 로드맵_응답 = 로드맵을_조회한다(로드맵_아이디);
        final Long 로드맵_첫번째_노드_아이디 = 로드맵_응답.content().nodes().get(0).id();
        final Long 로드맵_두번째_노드_아이디 = 로드맵_응답.content().nodes().get(1).id();

        final GoalRoomTodoRequest 골룸_첫번째_투두_요청 = new GoalRoomTodoRequest("투두 1", 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_첫번째_노드_아이디, 10, 오늘, 십일_후),
                new GoalRoomRoadmapNodeRequest(로드맵_두번째_노드_아이디, 5, 이십일_후, 삼십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_아이디, "골룸", 10, 골룸_첫번째_투두_요청,
                골룸_노드_별_기간_요청);
        final Long 골룸_아이디 = 골룸을_생성하고_아이디를_알아낸다(골룸_생성_요청, 로그인_토큰_정보);

        골룸을_시작한다();

        // when
        final ErrorResponse 예외_응답값 = given()
                .header(AUTHORIZATION, 다른_사용자_토큰_정보)
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(API_PREFIX + "/goal-rooms/{goalRoomId}/nodes", 골룸_아이디)
                .then()
                .log().all()
                .extract()
                .as(new TypeRef<>() {
                });

        // then
        assertThat(예외_응답값)
                .isEqualTo(new ErrorResponse("골룸에 참여하지 않은 사용자입니다. goalRoomId = 1 memberIdentifier = identifier2"));
    }

    @Test
    void 골룸의_인증피드를_전체_조회한다() throws IOException {
        // given
        회원가입을_한다("identifier1", "password1!", "name1", "010-1111-2222", GenderType.MALE,
                LocalDate.of(2023, Month.JULY, 12));
        회원가입을_한다("identifier2", "password2!", "name2", "010-1111-3333", GenderType.MALE,
                LocalDate.of(2023, Month.JULY, 12));
        final String 로그인_토큰_정보1 = 로그인을_한다(IDENTIFIER, PASSWORD);
        final String 로그인_토큰_정보2 = 로그인을_한다("identifier2", "password2!");
        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");
        final Long 로드맵_아이디 = 제목별로_로드맵을_생성한다(로그인_토큰_정보1, 여행_카테고리, "첫 번째 로드맵");

        final RoadmapResponse 로드맵_응답 = 로드맵을_조회한다(로드맵_아이디);
        final Long 로드맵_첫번째_노드_아이디 = 로드맵_응답.content().nodes().get(0).id();
        final Long 로드맵_두번째_노드_아이디 = 로드맵_응답.content().nodes().get(1).id();

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_첫번째_노드_아이디, 오늘부터_십일까지_인증_횟수, 오늘, 십일_후),
                new GoalRoomRoadmapNodeRequest(로드맵_두번째_노드_아이디, 오늘부터_십일까지_인증_횟수, 십일_후.plusDays(1),
                        십일_후.plusDays(20)));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_아이디, 정상적인_골룸_이름, 정상적인_골룸_제한_인원, 골룸_투두_요청,
                골룸_노드_별_기간_요청);
        final Long 골룸_id = 골룸을_생성하고_아이디를_알아낸다(골룸_생성_요청, 로그인_토큰_정보1);
        goalRoomCreateService.join("identifier2", 골룸_id);
        goalRoomScheduler.startGoalRooms();

        final MockMultipartFile 가짜_이미지_객체 = new MockMultipartFile("image", "originalFileName.jpeg",
                "image/jpeg", "tempImage".getBytes());
        final CheckFeedRequest 인증_피드_등록_요청1 = new CheckFeedRequest(가짜_이미지_객체, "image description1");
        final CheckFeedRequest 인증_피드_등록_요청2 = new CheckFeedRequest(가짜_이미지_객체, "image description2");

        인증_피드_등록_요청(로그인_토큰_정보1, 골룸_id, 인증_피드_등록_요청1);
        인증_피드_등록_요청(로그인_토큰_정보2, 골룸_id, 인증_피드_등록_요청2);

        //when
        final ExtractableResponse<Response> 인증_피드_전체_조회_요청에_대한_응답 = 인증_피드_전체_조회_요청(로그인_토큰_정보2, 골룸_id);

        // then
        final List<GoalRoomCheckFeedResponse> 인증_피드_전체_조회_응답_바디 = jsonToClass(인증_피드_전체_조회_요청에_대한_응답.asString(),
                new TypeReference<>() {
                });

        final GoalRoomCheckFeedResponse goalRoomCheckFeedResponse1 = new GoalRoomCheckFeedResponse(
                new MemberNameAndImageResponse(1L, "name1", "serverFilePath"),
                new CheckFeedResponse(1L, serverPathPrefix + filePathGenerator.makeFilePath(1L, ImageDirType.CHECK_FEED)
                        + "originalFileName.jpeg", "image description1",
                        LocalDateTime.now()));
        final GoalRoomCheckFeedResponse goalRoomCheckFeedResponse2 = new GoalRoomCheckFeedResponse(
                new MemberNameAndImageResponse(2L, "name2", "serverFilePath"),
                new CheckFeedResponse(2L, serverPathPrefix + filePathGenerator.makeFilePath(1L, ImageDirType.CHECK_FEED)
                        + "originalFileName.jpeg", "image description2",
                        LocalDateTime.now()));

        final List<GoalRoomCheckFeedResponse> 예상하는_응답값 = List.of(goalRoomCheckFeedResponse2,
                goalRoomCheckFeedResponse1);

        assertThat(인증_피드_전체_조회_응답_바디).usingRecursiveComparison()
                .ignoringFields("member.imageUrl", "checkFeed.imageUrl", "checkFeed.createdAt")
                .isEqualTo(예상하는_응답값);
    }

    @Test
    void 골룸의_인증피드를_전체_조회시_존재하지_않는_골룸인_경우_예외가_발생한다() throws IOException {
        // given
        회원가입을_한다("identifier1", "password1!", "name1", "010-1111-2222", GenderType.MALE,
                LocalDate.of(2023, Month.JULY, 12));
        회원가입을_한다("identifier2", "password2!", "name2", "010-1111-3333", GenderType.MALE,
                LocalDate.of(2023, Month.JULY, 12));
        final String 로그인_토큰_정보 = 로그인을_한다("identifier1", "password1!");
        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");
        제목별로_로드맵을_생성한다(로그인_토큰_정보, 여행_카테고리, "첫 번째 로드맵");

        //when
        final Long 존재하지_않는_골룸_아이디 = 1L;
        final ExtractableResponse<Response> 인증_피드_전체_조회_요청에_대한_응답 = 인증_피드_전체_조회_요청(로그인_토큰_정보, 존재하지_않는_골룸_아이디);

        // then
        final ErrorResponse 인증_피드_전체_조회_응답_바디 = jsonToClass(인증_피드_전체_조회_요청에_대한_응답.asString(), new TypeReference<>() {
        });
        assertThat(인증_피드_전체_조회_요청에_대한_응답.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(인증_피드_전체_조회_응답_바디).isEqualTo(new ErrorResponse("존재하지 않는 골룸입니다. goalRoomId = 1"));
    }

    @Test
    void 골룸의_인증피드를_전체_조회시_골룸에_참여하지_않은_사용자면_예외가_발생한다() throws IOException {
        // given
        회원가입을_한다("identifier1", "password1!", "name1", "010-1111-2222", GenderType.MALE,
                LocalDate.of(2023, Month.JULY, 12));
        회원가입을_한다("identifier2", "password2!", "name2", "010-1111-3333", GenderType.MALE,
                LocalDate.of(2023, Month.JULY, 12));
        final String 로그인_토큰_정보1 = 로그인을_한다(IDENTIFIER, PASSWORD);
        final String 로그인_토큰_정보2 = 로그인을_한다("identifier2", "password2!");
        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");
        final Long 로드맵_아이디 = 제목별로_로드맵을_생성한다(로그인_토큰_정보1, 여행_카테고리, "첫 번째 로드맵");

        final RoadmapResponse 로드맵_응답 = 로드맵을_조회한다(로드맵_아이디);
        final Long 로드맵_첫번째_노드_아이디 = 로드맵_응답.content().nodes().get(0).id();
        final Long 로드맵_두번째_노드_아이디 = 로드맵_응답.content().nodes().get(1).id();

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_첫번째_노드_아이디, 오늘부터_십일까지_인증_횟수, 오늘, 십일_후),
                new GoalRoomRoadmapNodeRequest(로드맵_두번째_노드_아이디, 오늘부터_십일까지_인증_횟수, 십일_후.plusDays(1),
                        십일_후.plusDays(20)));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_아이디, 정상적인_골룸_이름, 정상적인_골룸_제한_인원, 골룸_투두_요청,
                골룸_노드_별_기간_요청);
        final Long 골룸_id = 골룸을_생성하고_아이디를_알아낸다(골룸_생성_요청, 로그인_토큰_정보1);
        goalRoomScheduler.startGoalRooms();

        final MockMultipartFile 가짜_이미지_객체 = new MockMultipartFile("image", "originalFileName.jpeg",
                "image/jpeg", "tempImage".getBytes());
        final CheckFeedRequest 인증_피드_등록_요청1 = new CheckFeedRequest(가짜_이미지_객체, "image description1");

        인증_피드_등록_요청(로그인_토큰_정보1, 골룸_id, 인증_피드_등록_요청1);

        //when
        final ExtractableResponse<Response> 인증_피드_전체_조회_요청에_대한_응답 = 인증_피드_전체_조회_요청(로그인_토큰_정보2, 골룸_id);

        // then
        final ErrorResponse 인증_피드_전체_조회_응답_바디 = jsonToClass(인증_피드_전체_조회_요청에_대한_응답.asString(), new TypeReference<>() {
        });
        assertThat(인증_피드_전체_조회_요청에_대한_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(인증_피드_전체_조회_응답_바디).isEqualTo(new ErrorResponse("골룸에 참여하지 않은 회원입니다."));
    }

    private void 회원가입을_한다(final String 아이디, final String 비밀번호, final String 닉네임, final String 전화번호, final GenderType 성별,
                          final LocalDate 생년월일) {
        final MemberJoinRequest 회원가입_요청값 = new MemberJoinRequest(아이디, 비밀번호, 닉네임, 전화번호, 성별, 생년월일);
        회원가입_요청(회원가입_요청값);
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

    private String 로그인을_한다(final String 아이디, final String 비밀번호) {
        final LoginRequest 로그인_요청값 = new LoginRequest(아이디, 비밀번호);
        final ExtractableResponse<Response> 로그인_응답값 = 로그인_요청(로그인_요청값);
        return 액세스_토큰을_받는다(로그인_응답값);
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

    private String 액세스_토큰을_받는다(final ExtractableResponse<Response> 로그인_응답) {
        final AuthenticationResponse 토큰_응답값 = 로그인_응답.as(new TypeRef<>() {
        });
        return String.format(BEARER_TOKEN_FORMAT, 토큰_응답값.accessToken());
    }

    private Long 로드맵을_생성한다(final String 토큰, final Long 카테고리_아이디, final String 로드맵_제목, final String 로드맵_소개글,
                           final String 로드맵_본문, final RoadmapDifficultyType 난이도, final int 추천_소요_기간,
                           final List<RoadmapNodeSaveRequest> 로드맵_노드들) {
        final RoadmapSaveRequest 로드맵_생성_요청값 = new RoadmapSaveRequest(카테고리_아이디, 로드맵_제목, 로드맵_소개글, 로드맵_본문,
                난이도, 추천_소요_기간, 로드맵_노드들, List.of(new RoadmapTagSaveRequest("태그")));
        final ExtractableResponse<Response> 로드맵_생성_응답값 = 로드맵_생성_요청(로드맵_생성_요청값, 토큰);
        return 로드맵_아이디를_반환한다(로드맵_생성_응답값);
    }

    private ExtractableResponse<Response> 로드맵_생성_요청(final RoadmapSaveRequest 로드맵_생성_요청값, final String accessToken) {
        return given().log().all()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(로드맵_생성_요청값).log().all()
                .post(API_PREFIX + "/roadmaps")
                .then().log().all()
                .extract();
    }

    private Long 로드맵_아이디를_반환한다(final ExtractableResponse<Response> 응답) {
        return Long.parseLong(응답.header(HttpHeaders.LOCATION).split("/")[3]);
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

    private RoadmapCategory 로드맵_카테고리를_저장한다(final String 카테고리_이름) {
        final RoadmapCategory 로드맵_카테고리 = new RoadmapCategory(카테고리_이름);
        return roadmapCategoryRepository.save(로드맵_카테고리);
    }

    private GoalRoomResponse 예상하는_골룸_응답을_생성한다() {
        final List<GoalRoomRoadmapNodeResponse> goalRoomNodeResponses = List.of(
                new GoalRoomRoadmapNodeResponse(1L, "로드맵 1주차", 오늘, 십일_후, 10),
                new GoalRoomRoadmapNodeResponse(2L, "로드맵 2주차", 이십일_후, 삼십일_후, 10));
        return new GoalRoomResponse("골룸", 1, 10, goalRoomNodeResponses, 31);
    }

    private GoalRoomCertifiedResponse 로그인후_예상하는_골룸_응답을_생성한다() {
        final List<GoalRoomRoadmapNodeResponse> goalRoomNodeResponses = List.of(
                new GoalRoomRoadmapNodeResponse(1L, "로드맵 1주차", 오늘, 십일_후, 10),
                new GoalRoomRoadmapNodeResponse(2L, "로드맵 2주차", 이십일_후, 삼십일_후, 10));
        return new GoalRoomCertifiedResponse("골룸", 1, 10, goalRoomNodeResponses, 31, true);
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

    private MemberGoalRoomResponse 사용자의_특정_골룸_정보를_조회한다(final String 로그인_토큰_정보, final Long 골룸_아이디) {
        final MemberGoalRoomResponse 사용자_단일_골룸_조회_응답 = given().log().all()
                .header(AUTHORIZATION, 로그인_토큰_정보)
                .when()
                .get(API_PREFIX + "/goal-rooms/{goalRoomId}/me", 골룸_아이디)
                .then()
                .log().all()
                .extract()
                .as(new TypeRef<>() {
                });
        return 사용자_단일_골룸_조회_응답;
    }

    private List<GoalRoomTodoResponse> 예상하는_골룸_투두리스트_응답을_생성한다(final List<GoalRoomTodoResponse> 사용자_골룸_투두_응답) {
        return List.of(
                new GoalRoomTodoResponse(사용자_골룸_투두_응답.get(0).id(), "투두 1", 오늘, 십일_후,
                        new GoalRoomToDoCheckResponse(false)),
                new GoalRoomTodoResponse(사용자_골룸_투두_응답.get(1).id(), "투두 2", 십일_후, 이십일_후,
                        new GoalRoomToDoCheckResponse(true)));
    }

    private List<GoalRoomRoadmapNodeResponse> 예상하는_골룸_노드_응답을_생성한다(
            final List<GoalRoomRoadmapNodeResponse> 사용자_골룸_노드_응답) {
        return List.of(
                new GoalRoomRoadmapNodeResponse(사용자_골룸_노드_응답.get(0).id(), "로드맵 1주차", 오늘, 십일_후, 10),
                new GoalRoomRoadmapNodeResponse(사용자_골룸_노드_응답.get(1).id(), "로드맵 2주차", 이십일_후, 삼십일_후, 5));
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

    private void 골룸을_시작한다() {
        goalRoomScheduler.startGoalRooms();
    }

    private Long 골룸을_생성하고_아이디를_알아낸다(final GoalRoomCreateRequest 골룸_생성_요청, final String 액세스_토큰) {
        final ExtractableResponse<Response> 골룸_응답 = 골룸_생성(골룸_생성_요청, 액세스_토큰);
        final String Location_헤더 = 골룸_응답.response().header("Location");
        final Long 골룸_아이디 = Long.parseLong(Location_헤더.substring(16));
        return 골룸_아이디;
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

    private ExtractableResponse<Response> 인증_피드_등록_요청(final String 액세스_토큰, final Long 골룸_아이디,
                                                      final CheckFeedRequest 인증_피드_등록_요청) throws IOException {
        final MultipartFile 인증피드_가짜_이미지_객체 = 인증_피드_등록_요청.image();

        final ExtractableResponse<Response> 인증_피드_등록_응답 = given().log().all()
                .multiPart(인증피드_가짜_이미지_객체.getName(), 인증피드_가짜_이미지_객체.getOriginalFilename(),
                        인증피드_가짜_이미지_객체.getBytes(), 인증피드_가짜_이미지_객체.getContentType())
                .formParam("description", 인증_피드_등록_요청.description())
                .header(AUTHORIZATION, 액세스_토큰)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .when()
                .post(API_PREFIX + "/goal-rooms/{goalRoomId}/checkFeeds", 골룸_아이디)
                .then()
                .log().all()
                .extract();

        테스트용으로_생성된_파일을_제거한다();
        return 인증_피드_등록_응답;
    }

    private ExtractableResponse<Response> 인증_피드_전체_조회_요청(final String 액세스_토큰, final Long 골룸_아이디) {
        return given().log().all()
                .header(AUTHORIZATION, 액세스_토큰)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(API_PREFIX + "/goal-rooms/{goalRoomId}/checkFeeds", 골룸_아이디)
                .then()
                .log().all()
                .extract();
    }

    private void 테스트용으로_생성된_파일을_제거한다() {
        final String rootPath = storageLocation;

        try {
            final File rootDir = new File(rootPath);
            FileUtils.deleteDirectory(rootDir);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
