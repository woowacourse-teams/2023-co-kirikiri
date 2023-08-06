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
import co.kirikiri.domain.roadmap.RoadmapNode;
import co.kirikiri.integration.helper.IntegrationTest;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.persistence.roadmap.RoadmapCategoryRepository;
import co.kirikiri.persistence.roadmap.RoadmapNodeRepository;
import co.kirikiri.persistence.roadmap.RoadmapRepository;
import co.kirikiri.service.GoalRoomCreateService;
import co.kirikiri.service.dto.auth.request.LoginRequest;
import co.kirikiri.service.dto.auth.response.AuthenticationResponse;
import co.kirikiri.service.dto.goalroom.request.CheckFeedRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomCreateRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomRoadmapNodeRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomTodoRequest;
import co.kirikiri.service.dto.goalroom.response.CheckFeedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomCertifiedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomRoadmapNodeResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomRoadmapNodesResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomToDoCheckResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomTodoResponse;
import co.kirikiri.service.dto.member.request.GenderType;
import co.kirikiri.service.dto.member.request.MemberJoinRequest;
import co.kirikiri.service.dto.member.response.MemberGoalRoomForListResponse;
import co.kirikiri.service.dto.member.response.MemberGoalRoomResponse;
import co.kirikiri.service.dto.member.response.MemberResponse;
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
    private static final int 정상적인_골룸_노드_인증_횟수 = (int) ChronoUnit.DAYS.between(오늘, 십일_후);

    private final String storageLocation;
    private final RoadmapRepository roadmapRepository;
    private final GoalRoomRepository goalRoomRepository;
    private final RoadmapCategoryRepository roadmapCategoryRepository;
    private final RoadmapNodeRepository roadmapNodeRepository;
    private final GoalRoomCreateService goalRoomCreateService;

    public GoalRoomReadIntegrationTest(@Value("${file.upload-dir}") final String storageLocation,
                                       final RoadmapRepository roadmapRepository,
                                       final GoalRoomRepository goalRoomRepository,
                                       final RoadmapNodeRepository roadmapNodeRepository,
                                       final RoadmapCategoryRepository roadmapCategoryRepository,
                                       final GoalRoomCreateService goalRoomCreateService) {
        this.storageLocation = storageLocation;
        this.roadmapRepository = roadmapRepository;
        this.goalRoomRepository = goalRoomRepository;
        this.roadmapNodeRepository = roadmapNodeRepository;
        this.roadmapCategoryRepository = roadmapCategoryRepository;
        this.goalRoomCreateService = goalRoomCreateService;
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
        final Long 투두_아이디 = 골룸_투두리스트_추가(로그인_토큰_정보, 골룸_아이디, 골룸_두번째_투두_요청);

        // TODO 아이디 값에 대한 변환 필요
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
        final List<GoalRoomTodoResponse> 예상하는_골룸_투두리스트_응답값 = 예상하는_골룸_투두리스트_응답을_생성한다();
        assertThat(골룸_투두리스트_응답값)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(예상하는_골룸_투두리스트_응답값);
    }

    @Test
    void 사용자_단일_골룸을_조회한다() throws IOException {
        // given
        회원가입을_한다("identifier", "password1!", "코끼리",
                "010-1111-2222", GenderType.MALE, LocalDate.of(1999, 9, 9));
        final String 액세스_토큰 = 로그인을_한다("identifier", "password1!");

        final RoadmapCategory 카테고리 = 로드맵_카테고리를_저장한다("여가");
        final Long 로드맵_아이디 = 로드맵을_생성한다(액세스_토큰, 카테고리.getId(), "로드맵 제목", "로드맵 소개글",
                "로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용"),
                        new RoadmapNodeSaveRequest("로드맵 2주차", "로드맵 2주차 내용")));
        final RoadmapNode 첫번째_로드맵_노드 = roadmapNodeRepository.findAll().get(0);
        final RoadmapNode 두번째_로드맵_노드 = roadmapNodeRepository.findAll().get(1);

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(첫번째_로드맵_노드.getId(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후),
                new GoalRoomRoadmapNodeRequest(두번째_로드맵_노드.getId(), 정상적인_골룸_노드_인증_횟수, 십일_후.plusDays(1), 이십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_아이디, 정상적인_골룸_이름,
                정상적인_골룸_제한_인원, 골룸_투두_요청, 골룸_노드_별_기간_요청);
        final Long 골룸_아이디 = 골룸_생성(골룸_생성_요청, 액세스_토큰);

        회원가입을_한다("identifier2", "password2@", "팔로워", "010-1234-5555", GenderType.FEMALE, LocalDate.of(2000, 1, 1));
        final String 팔로워_액세스_토큰 = 로그인을_한다("identifier2", "password2@");
        골룸_참가_요청(골룸_아이디, 팔로워_액세스_토큰);
        goalRoomCreateService.startGoalRooms();

        final MockMultipartFile 가짜_이미지_객체 = new MockMultipartFile("image", "originalFileName.jpeg",
                "image/webp", "tempImage".getBytes());
        final CheckFeedRequest 인증_피드_등록_요청 = new CheckFeedRequest(가짜_이미지_객체, "image description");
        인증_피드_등록을_요청한다(인증_피드_등록_요청, 액세스_토큰, 골룸_아이디);
        인증_피드_등록을_요청한다(인증_피드_등록_요청, 팔로워_액세스_토큰, 골룸_아이디);

        // when
        final ExtractableResponse<Response> 사용자_단일_골룸_조회_응답 = given().log().all()
                .header(AUTHORIZATION, 액세스_토큰)
                .when()
                .get(API_PREFIX + "/goal-rooms/{goalRoomId}/me", 골룸_아이디)
                .then()
                .log().all()
                .extract();

        // then
        final MemberGoalRoomResponse 예상되는_응답 = new MemberGoalRoomResponse(정상적인_골룸_이름, "RUNNING", 1L,
                2, 정상적인_골룸_제한_인원, 오늘, 이십일_후, 1L,
                new GoalRoomRoadmapNodesResponse(false, false,
                        List.of(
                                new GoalRoomRoadmapNodeResponse(1L, "로드맵 1주차", 오늘, 십일_후, 정상적인_골룸_노드_인증_횟수),
                                new GoalRoomRoadmapNodeResponse(2L, "로드맵 2주차", 십일_후.plusDays(1), 이십일_후,
                                        정상적인_골룸_노드_인증_횟수))),
                List.of(new GoalRoomTodoResponse(1L, 정상적인_골룸_투두_컨텐츠, 오늘, 십일_후,
                        new GoalRoomToDoCheckResponse(false))),
                List.of(
                        new CheckFeedResponse(1L, "filePath1", "image description"),
                        new CheckFeedResponse(2L, "filePath1", "image description")
                ));
        final MemberGoalRoomResponse 요청_응답값 = objectMapper.readValue(사용자_단일_골룸_조회_응답.asString(), new TypeReference<>() {
        });

        assertThat(요청_응답값)
                .usingRecursiveComparison()
                .ignoringFields("checkFeeds.imageUrl")
                .isEqualTo(예상되는_응답);
    }

    @Test
    void 골룸_시작_전에_사용자_단일_골룸_조회_시_인증_피드가_빈_응답을_반환한다() throws JsonProcessingException {
        // given
        회원가입을_한다("identifier", "password1!", "코끼리", "010-1111-2222", GenderType.MALE, LocalDate.of(1999, 9, 9));
        final String 액세스_토큰 = 로그인을_한다("identifier", "password1!");
        final RoadmapCategory 카테고리 = 로드맵_카테고리를_저장한다("여가");
        final Long 로드맵_아이디 = 로드맵을_생성한다(액세스_토큰, 카테고리.getId(), "로드맵 제목", "로드맵 소개글",
                "로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용")));
        final RoadmapNode 로드맵_노드 = roadmapNodeRepository.findAll().get(0);

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 십일_후, 이십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_노드.getId(), 정상적인_골룸_노드_인증_횟수, 십일_후, 이십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_아이디, 정상적인_골룸_이름,
                정상적인_골룸_제한_인원, 골룸_투두_요청, 골룸_노드_별_기간_요청);
        final Long 골룸_아이디 = 골룸_생성(골룸_생성_요청, 액세스_토큰);

        //when
        final ExtractableResponse<Response> 사용자_단일_골룸_조회_응답 = given().log().all()
                .header(AUTHORIZATION, 액세스_토큰)
                .when()
                .get(API_PREFIX + "/goal-rooms/{goalRoomId}/me", 골룸_아이디)
                .then()
                .log().all()
                .extract();

        //then
        final MemberGoalRoomResponse 예상되는_응답 = new MemberGoalRoomResponse(정상적인_골룸_이름, "RECRUITING", 1L,
                1, 정상적인_골룸_제한_인원, 십일_후, 이십일_후, 1L,
                new GoalRoomRoadmapNodesResponse(false, false,
                        List.of(new GoalRoomRoadmapNodeResponse(1L, "로드맵 1주차", 십일_후, 이십일_후, 정상적인_골룸_노드_인증_횟수))),
                List.of(new GoalRoomTodoResponse(1L, 정상적인_골룸_투두_컨텐츠, 십일_후, 이십일_후,
                        new GoalRoomToDoCheckResponse(false))),
                Collections.emptyList());
        final MemberGoalRoomResponse 요청_응답값 = objectMapper.readValue(사용자_단일_골룸_조회_응답.asString(), new TypeReference<>() {
        });

        assertThat(요청_응답값)
                .usingRecursiveComparison()
                .ignoringFields("checkFeeds.imageUrl")
                .isEqualTo(예상되는_응답);
    }

    @Test
    void 사용자의_모든_골룸_목록을_조회한다() throws JsonProcessingException {
        // given
        회원가입을_한다("identifier", "password1!", "코끼리",
                "010-1111-2222", GenderType.MALE, LocalDate.of(1999, 9, 9));
        final String 액세스_토큰 = 로그인을_한다("identifier", "password1!");
        final RoadmapCategory 카테고리 = 로드맵_카테고리를_저장한다("여가");
        final Long 첫번째_로드맵_아이디 = 로드맵을_생성한다(액세스_토큰, 카테고리.getId(), "첫번째_로드맵 제목",
                "첫번째_로드맵 소개글", "첫번째_로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("첫번째_로드맵 1주차", "첫번째_로드맵 1주차 내용")));
        final RoadmapNode 첫번째_로드맵_노드 = roadmapNodeRepository.findAll().get(0);
        final GoalRoomTodoRequest 첫번째_골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 첫번째_골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(첫번째_로드맵_노드.getId(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 첫번째_골룸_생성_요청 = new GoalRoomCreateRequest(첫번째_로드맵_아이디, 정상적인_골룸_이름,
                정상적인_골룸_제한_인원, 첫번째_골룸_투두_요청, 첫번째_골룸_노드_별_기간_요청);
        final Long 첫번째_골룸_아이디 = 골룸_생성(첫번째_골룸_생성_요청, 액세스_토큰);

        final Long 두번째_로드맵_아이디 = 로드맵을_생성한다(액세스_토큰, 카테고리.getId(), "두번째_로드맵 제목",
                "두번째_로드맵 소개글", "두번째_로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("두번째_로드맵 1주차", "두번째_로드맵 1주차 내용")));
        final RoadmapNode 두번째_로드맵_노드 = roadmapNodeRepository.findAll().get(1);
        final GoalRoomTodoRequest 두번째_골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 십일_후, 이십일_후);
        final List<GoalRoomRoadmapNodeRequest> 두번째_골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(두번째_로드맵_노드.getId(), 정상적인_골룸_노드_인증_횟수, 십일_후, 이십일_후));
        final GoalRoomCreateRequest 두번째_골룸_생성_요청 = new GoalRoomCreateRequest(두번째_로드맵_아이디, 정상적인_골룸_이름,
                정상적인_골룸_제한_인원, 두번째_골룸_투두_요청, 두번째_골룸_노드_별_기간_요청);
        final Long 두번째_골룸_아이디 = 골룸_생성(두번째_골룸_생성_요청, 액세스_토큰);
        goalRoomCreateService.startGoalRooms();
        // when
        final ExtractableResponse<Response> 사용자_단일_골룸_조회_응답 = given().log().all()
                .header(AUTHORIZATION, 액세스_토큰)
                .when()
                .get(API_PREFIX + "/goal-rooms/me")
                .then()
                .log().all()
                .extract();
        // then
        final List<MemberGoalRoomForListResponse> 예상되는_응답 = List.of(
                new MemberGoalRoomForListResponse(첫번째_골룸_아이디, 정상적인_골룸_이름, "RUNNING",
                        1, 정상적인_골룸_제한_인원, LocalDateTime.now(), 오늘, 십일_후,
                        new MemberResponse(1L, "코끼리")),
                new MemberGoalRoomForListResponse(두번째_골룸_아이디, 정상적인_골룸_이름, "RECRUITING",
                        1, 정상적인_골룸_제한_인원, LocalDateTime.now(), 십일_후, 이십일_후,
                        new MemberResponse(1L, "코끼리")));

        final List<MemberGoalRoomForListResponse> 요청_응답값 = objectMapper.readValue(사용자_단일_골룸_조회_응답.asString(),
                new TypeReference<>() {
                });

        assertThat(요청_응답값)
                .usingRecursiveComparison()
                .ignoringFields("createdAt")
                .isEqualTo(예상되는_응답);
    }

    @Test
    void 사용자가_참여한_골룸_중_모집_중인_골룸_목록을_조회한다() throws JsonProcessingException {
        // given
        회원가입을_한다("identifier", "password1!", "코끼리", "010-1111-2222", GenderType.MALE, LocalDate.of(1999, 9, 9));
        final String 액세스_토큰 = 로그인을_한다("identifier", "password1!");
        final RoadmapCategory 카테고리 = 로드맵_카테고리를_저장한다("여가");
        final Long 첫번째_로드맵_아이디 = 로드맵을_생성한다(액세스_토큰, 카테고리.getId(), "첫번째_로드맵 제목",
                "첫번째_로드맵 소개글", "첫번째_로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("첫번째_로드맵 1주차", "첫번째_로드맵 1주차 내용")));
        final RoadmapNode 첫번째_로드맵_노드 = roadmapNodeRepository.findAll().get(0);
        final GoalRoomTodoRequest 첫번째_골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 첫번째_골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(첫번째_로드맵_노드.getId(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 첫번째_골룸_생성_요청 = new GoalRoomCreateRequest(첫번째_로드맵_아이디, 정상적인_골룸_이름,
                정상적인_골룸_제한_인원, 첫번째_골룸_투두_요청, 첫번째_골룸_노드_별_기간_요청);
        final Long 첫번째_골룸_아이디 = 골룸_생성(첫번째_골룸_생성_요청, 액세스_토큰);
        final Long 두번째_로드맵_아이디 = 로드맵을_생성한다(액세스_토큰, 카테고리.getId(), "두번째_로드맵 제목",
                "두번째_로드맵 소개글", "두번째_로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("두번째_로드맵 1주차", "두번째_로드맵 1주차 내용")));
        final RoadmapNode 두번째_로드맵_노드 = roadmapNodeRepository.findAll().get(1);
        final GoalRoomTodoRequest 두번째_골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 십일_후, 이십일_후);
        final List<GoalRoomRoadmapNodeRequest> 두번째_골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(두번째_로드맵_노드.getId(), 정상적인_골룸_노드_인증_횟수, 십일_후, 이십일_후));
        final GoalRoomCreateRequest 두번째_골룸_생성_요청 = new GoalRoomCreateRequest(두번째_로드맵_아이디, 정상적인_골룸_이름,
                정상적인_골룸_제한_인원, 두번째_골룸_투두_요청, 두번째_골룸_노드_별_기간_요청);
        final Long 두번째_골룸_아이디 = 골룸_생성(두번째_골룸_생성_요청, 액세스_토큰);
        goalRoomCreateService.startGoalRooms();
        // when
        final ExtractableResponse<Response> 사용자_단일_골룸_조회_응답 = given().log().all()
                .header(AUTHORIZATION, 액세스_토큰)
                .queryParam("statusCond", "RECRUITING")
                .when()
                .get(API_PREFIX + "/goal-rooms/me")
                .then()
                .log().all()
                .extract();
        // then
        final List<MemberGoalRoomForListResponse> 예상되는_응답 = List.of(
                new MemberGoalRoomForListResponse(두번째_골룸_아이디, 정상적인_골룸_이름, "RECRUITING",
                        1, 정상적인_골룸_제한_인원, LocalDateTime.now(), 십일_후, 이십일_후,
                        new MemberResponse(1L, "코끼리")));
        final List<MemberGoalRoomForListResponse> 요청_응답값 = objectMapper.readValue(사용자_단일_골룸_조회_응답.asString(),
                new TypeReference<>() {
                });
        assertThat(요청_응답값)
                .usingRecursiveComparison()
                .ignoringFields("createdAt")
                .isEqualTo(예상되는_응답);
    }

    @Test
    void 사용자가_참여한_골룸_중_진행_중인_골룸_목록을_조회한다() throws JsonProcessingException {
        // given
        회원가입을_한다("identifier", "password1!", "코끼리", "010-1111-2222", GenderType.MALE, LocalDate.of(1999, 9, 9));
        final String 액세스_토큰 = 로그인을_한다("identifier", "password1!");
        final RoadmapCategory 카테고리 = 로드맵_카테고리를_저장한다("여가");
        final Long 첫번째_로드맵_아이디 = 로드맵을_생성한다(액세스_토큰, 카테고리.getId(), "첫번째_로드맵 제목",
                "첫번째_로드맵 소개글", "첫번째_로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("첫번째_로드맵 1주차", "첫번째_로드맵 1주차 내용")));
        final RoadmapNode 첫번째_로드맵_노드 = roadmapNodeRepository.findAll().get(0);
        final GoalRoomTodoRequest 첫번째_골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 첫번째_골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(첫번째_로드맵_노드.getId(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 첫번째_골룸_생성_요청 = new GoalRoomCreateRequest(첫번째_로드맵_아이디, 정상적인_골룸_이름,
                정상적인_골룸_제한_인원, 첫번째_골룸_투두_요청, 첫번째_골룸_노드_별_기간_요청);
        final Long 첫번째_골룸_아이디 = 골룸_생성(첫번째_골룸_생성_요청, 액세스_토큰);
        final Long 두번째_로드맵_아이디 = 로드맵을_생성한다(액세스_토큰, 카테고리.getId(), "두번째_로드맵 제목",
                "두번째_로드맵 소개글", "두번째_로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("두번째_로드맵 1주차", "두번째_로드맵 1주차 내용")));
        final RoadmapNode 두번째_로드맵_노드 = roadmapNodeRepository.findAll().get(1);
        final GoalRoomTodoRequest 두번째_골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 십일_후, 이십일_후);
        final List<GoalRoomRoadmapNodeRequest> 두번째_골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(두번째_로드맵_노드.getId(), 정상적인_골룸_노드_인증_횟수, 십일_후, 이십일_후));
        final GoalRoomCreateRequest 두번째_골룸_생성_요청 = new GoalRoomCreateRequest(두번째_로드맵_아이디, 정상적인_골룸_이름,
                정상적인_골룸_제한_인원, 두번째_골룸_투두_요청, 두번째_골룸_노드_별_기간_요청);
        final Long 두번째_골룸_아이디 = 골룸_생성(두번째_골룸_생성_요청, 액세스_토큰);
        goalRoomCreateService.startGoalRooms();
        // when
        final ExtractableResponse<Response> 사용자_단일_골룸_조회_응답 = given().log().all()
                .header(AUTHORIZATION, 액세스_토큰)
                .queryParam("statusCond", "RUNNING")
                .when()
                .get(API_PREFIX + "/goal-rooms/me")
                .then()
                .log().all()
                .extract();
        // then
        final List<MemberGoalRoomForListResponse> 예상되는_응답 = List.of(
                new MemberGoalRoomForListResponse(첫번째_골룸_아이디, 정상적인_골룸_이름, "RUNNING",
                        1, 정상적인_골룸_제한_인원, LocalDateTime.now(), 오늘, 십일_후,
                        new MemberResponse(1L, "코끼리")));
        final List<MemberGoalRoomForListResponse> 요청_응답값 = objectMapper.readValue(사용자_단일_골룸_조회_응답.asString(),
                new TypeReference<>() {
                });
        assertThat(요청_응답값)
                .usingRecursiveComparison()
                .ignoringFields("createdAt")
                .isEqualTo(예상되는_응답);
    }

    @Test
    void 골룸_노드를_조회한다() {
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
                new GoalRoomRoadmapNodeRequest(로드맵_첫번째_노드_아이디, 10, 오늘, 십일_후),
                new GoalRoomRoadmapNodeRequest(로드맵_두번째_노드_아이디, 5, 이십일_후, 삼십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_아이디, "골룸", 10, 골룸_첫번째_투두_요청,
                골룸_노드_별_기간_요청);
        final Long 골룸_아이디 = 골룸_생성(골룸_생성_요청, 로그인_토큰_정보);

        goalRoomCreateService.startGoalRooms();

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
        final List<GoalRoomRoadmapNodeResponse> 예상하는_골룸_노드_응답값 = 예상하는_골룸_노드_응답을_생성한다();
        assertThat(골룸_노드_응답값)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(예상하는_골룸_노드_응답값);
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

    private String 로그인을_한다(final String 아이디, final String 비밀번호) throws JsonProcessingException {
        final LoginRequest 로그인_요청값 = new LoginRequest(아이디, 비밀번호);
        final ExtractableResponse<Response> 로그인_응답값 = 로그인_요청(로그인_요청값);
        return access_token을_받는다(로그인_응답값);
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
        return String.format(BEARER_TOKEN_FORMAT, 토큰_응답값.accessToken());
    }

    private Long 로드맵을_생성한다(final String 토큰, final Long 카테고리_아이디, final String 로드맵_제목, final String 로드맵_소개글,
                           final String 로드맵_본문,
                           final RoadmapDifficultyType 난이도, final int 추천_소요_기간,
                           final List<RoadmapNodeSaveRequest> 로드맵_노드들) {
        final RoadmapSaveRequest 로드맵_생성_요청값 = new RoadmapSaveRequest(카테고리_아이디, 로드맵_제목, 로드맵_소개글, 로드맵_본문,
                난이도, 추천_소요_기간, 로드맵_노드들, List.of(new RoadmapTagSaveRequest("태그")));
        final ExtractableResponse<Response> 로드맵_생성_응답값 = 로드맵_생성_요청(로드맵_생성_요청값, 토큰);
        return 로드맵_아이디를_반환한다(로드맵_생성_응답값);
    }

    private Long 로드맵_아이디를_반환한다(final ExtractableResponse<Response> 응답) {
        return Long.parseLong(응답.header(HttpHeaders.LOCATION).split("/")[3]);
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

    private ExtractableResponse<Response> 로드맵_생성_요청(final RoadmapSaveRequest 로드맵_생성_요청값, final String accessToken) {
        return given().log().all()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(로드맵_생성_요청값).log().all()
                .post(API_PREFIX + "/roadmaps")
                .then().log().all()
                .extract();
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

    private List<GoalRoomTodoResponse> 예상하는_골룸_투두리스트_응답을_생성한다() {
        return List.of(
                new GoalRoomTodoResponse(1L, "투두 1", 오늘, 십일_후, new GoalRoomToDoCheckResponse(false)),
                new GoalRoomTodoResponse(2L, "투두 2", 십일_후, 이십일_후, new GoalRoomToDoCheckResponse(true)));
    }

    private List<GoalRoomRoadmapNodeResponse> 예상하는_골룸_노드_응답을_생성한다() {
        return List.of(
                new GoalRoomRoadmapNodeResponse(1L, "로드맵 1주차", 오늘, 십일_후, 10),
                new GoalRoomRoadmapNodeResponse(2L, "로드맵 2주차", 이십일_후, 삼십일_후, 5));
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

    private ExtractableResponse<Response> 인증_피드_등록을_요청한다(final CheckFeedRequest 인증_피드_등록_요청,
                                                         final String 액세스_토큰, final Long 골룸_id) throws IOException {
        final MultipartFile 가짜_이미지_객체 = 인증_피드_등록_요청.image();

        final ExtractableResponse<Response> 인증_피드_등록_응답 = given().log().all()
                .multiPart(가짜_이미지_객체.getName(), 가짜_이미지_객체.getOriginalFilename(),
                        가짜_이미지_객체.getBytes(), 가짜_이미지_객체.getContentType())
                .formParam("description", 인증_피드_등록_요청.description())
                .header(AUTHORIZATION, 액세스_토큰)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .when()
                .post(API_PREFIX + "/goal-rooms/{goalRoomId}/checkFeeds", 골룸_id)
                .then()
                .log().all()
                .extract();

        인증_피드_등록_응답.response().header("Location");
        테스트용으로_생성된_파일을_제거한다();

        return 인증_피드_등록_응답;
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
