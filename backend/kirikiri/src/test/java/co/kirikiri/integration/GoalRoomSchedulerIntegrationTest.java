package co.kirikiri.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import co.kirikiri.domain.ImageContentType;
import co.kirikiri.domain.goalroom.CheckFeed;
import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomMember;
import co.kirikiri.domain.goalroom.GoalRoomPendingMember;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNodes;
import co.kirikiri.domain.goalroom.GoalRoomRole;
import co.kirikiri.domain.goalroom.GoalRoomStatus;
import co.kirikiri.domain.goalroom.vo.GoalRoomName;
import co.kirikiri.domain.goalroom.vo.LimitedMemberCount;
import co.kirikiri.domain.goalroom.vo.Period;
import co.kirikiri.domain.member.EncryptedPassword;
import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.domain.roadmap.RoadmapContents;
import co.kirikiri.domain.roadmap.RoadmapDifficulty;
import co.kirikiri.domain.roadmap.RoadmapNode;
import co.kirikiri.domain.roadmap.RoadmapNodes;
import co.kirikiri.integration.helper.IntegrationTest;
import co.kirikiri.integration.helper.IntegrationTestHelper;
import co.kirikiri.persistence.goalroom.CheckFeedRepository;
import co.kirikiri.persistence.goalroom.GoalRoomMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomPendingMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.persistence.roadmap.RoadmapCategoryRepository;
import co.kirikiri.persistence.roadmap.RoadmapRepository;
import co.kirikiri.service.GoalRoomScheduler;
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
import co.kirikiri.service.dto.member.response.MemberGoalRoomResponse;
import co.kirikiri.service.dto.roadmap.request.RoadmapDifficultyType;
import co.kirikiri.service.dto.roadmap.request.RoadmapNodeSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapTagSaveRequest;
import co.kirikiri.service.dto.roadmap.response.RoadmapResponse;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.Header;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collections;
import java.util.List;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class GoalRoomSchedulerIntegrationTest extends IntegrationTest {

    private static final String IDENTIFIER = "identifier1";
    private static final String PASSWORD = "password1!";
    private static final LocalDate 오늘 = LocalDate.now();
    private static final LocalDate 십일_후 = 오늘.plusDays(10);
    private static final LocalDate 이십일_후 = 오늘.plusDays(20);
    private static final LocalDate 삼십일_후 = 오늘.plusDays(30);

    private final String storageLocation;
    private final IntegrationTestHelper testHelper;
    private final GoalRoomScheduler goalRoomScheduler;
    private final GoalRoomRepository goalRoomRepository;
    private final GoalRoomPendingMemberRepository goalRoomPendingMemberRepository;
    private final GoalRoomMemberRepository goalRoomMemberRepository;
    private final MemberRepository memberRepository;
    private final RoadmapRepository roadmapRepository;
    private final RoadmapCategoryRepository roadmapCategoryRepository;
    private final CheckFeedRepository checkFeedRepository;

    public GoalRoomSchedulerIntegrationTest(@Value("${file.upload-dir}") final String storageLocation,
                                            final IntegrationTestHelper testHelper,
                                            final GoalRoomScheduler goalRoomScheduler,
                                            final GoalRoomRepository goalRoomRepository,
                                            final GoalRoomPendingMemberRepository goalRoomPendingMemberRepository,
                                            final GoalRoomMemberRepository goalRoomMemberRepository,
                                            final MemberRepository memberRepository,
                                            final RoadmapRepository roadmapRepository,
                                            final RoadmapCategoryRepository roadmapCategoryRepository,
                                            final CheckFeedRepository checkFeedRepository) {
        this.storageLocation = storageLocation;
        this.testHelper = testHelper;
        this.goalRoomScheduler = goalRoomScheduler;
        this.goalRoomRepository = goalRoomRepository;
        this.goalRoomPendingMemberRepository = goalRoomPendingMemberRepository;
        this.goalRoomMemberRepository = goalRoomMemberRepository;
        this.memberRepository = memberRepository;
        this.roadmapRepository = roadmapRepository;
        this.roadmapCategoryRepository = roadmapCategoryRepository;
        this.checkFeedRepository = checkFeedRepository;
    }

    @Test
    void 골룸이_시작되면_골룸_대기_사용자에서_골룸_사용자로_이동하고_대기_사용자에서는_제거된다() {
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

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest("투두 콘텐츠", 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_첫번째_노드_아이디, 3, 오늘, 십일_후),
                new GoalRoomRoadmapNodeRequest(로드맵_두번째_노드_아이디, 3, 십일_후.plusDays(1), 이십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_아이디, "골룸 이름",
                10, 골룸_투두_요청, 골룸_노드_별_기간_요청);
        final Long 골룸_아이디 = 골룸을_생성하고_아이디를_알아낸다(골룸_생성_요청, 로그인_토큰_정보);
        final GoalRoom 골룸 = new GoalRoom(골룸_아이디, null, null, null, null);

        회원가입을_한다("identifier2", "password2@", "팔로워1", "010-1234-5555",
                GenderType.FEMALE, LocalDate.of(2000, 1, 1));
        회원가입을_한다("identifier3", "password3#", "팔로워2", "010-1234-5555",
                GenderType.FEMALE, LocalDate.of(2000, 1, 1));
        final String 팔로워1_액세스_토큰 = 로그인을_한다("identifier2", "password2@");
        final String 팔로워2_액세스_토큰 = 로그인을_한다("identifier3", "password3#");

        골룸_참가_요청(골룸_아이디, 팔로워1_액세스_토큰);
        골룸_참가_요청(골룸_아이디, 팔로워2_액세스_토큰);

        // when
        goalRoomScheduler.startGoalRooms();

        // then
        assertAll(
                () -> assertThat(goalRoomPendingMemberRepository.findAllByGoalRoom(골룸)).isEmpty(),
                () -> assertThat(goalRoomMemberRepository.findAllByGoalRoom(골룸)).hasSize(3)
        );
    }

    @Test
    void 골룸의_시작날짜가_오늘보다_이후이면_아무일도_일어나지_않는다() {
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

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest("투두 콘텐츠", 오늘.plusDays(1), 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_첫번째_노드_아이디, 3, 오늘.plusDays(1), 십일_후),
                new GoalRoomRoadmapNodeRequest(로드맵_두번째_노드_아이디, 3, 십일_후.plusDays(1), 이십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_아이디, "골룸 이름",
                10, 골룸_투두_요청, 골룸_노드_별_기간_요청);
        final Long 골룸_아이디 = 골룸을_생성하고_아이디를_알아낸다(골룸_생성_요청, 로그인_토큰_정보);
        final GoalRoom 골룸 = new GoalRoom(골룸_아이디, null, null, null, null);

        회원가입을_한다("identifier2", "password2@", "팔로워1", "010-1234-5555",
                GenderType.FEMALE, LocalDate.of(2000, 1, 1));
        회원가입을_한다("identifier3", "password3#", "팔로워2", "010-1234-5555",
                GenderType.FEMALE, LocalDate.of(2000, 1, 1));
        final String 팔로워1_액세스_토큰 = 로그인을_한다("identifier2", "password2@");
        final String 팔로워2_액세스_토큰 = 로그인을_한다("identifier3", "password3#");

        골룸_참가_요청(골룸_아이디, 팔로워1_액세스_토큰);
        골룸_참가_요청(골룸_아이디, 팔로워2_액세스_토큰);

        // when
        goalRoomScheduler.startGoalRooms();

        // then
        assertAll(
                () -> assertThat(goalRoomPendingMemberRepository.findAllByGoalRoom(골룸)).hasSize(3),
                () -> assertThat(goalRoomMemberRepository.findAllByGoalRoom(골룸)).isEmpty()
        );
    }

    @Test
    void 골룸_종료시_종료_날짜가_어제인_골룸의_상태가_COMPLETED로_변경된다() {
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

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest("투두 콘텐츠", 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_첫번째_노드_아이디, 3, 오늘, 십일_후),
                new GoalRoomRoadmapNodeRequest(로드맵_두번째_노드_아이디, 3, 십일_후.plusDays(1), 이십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_아이디, "골룸 이름",
                10, 골룸_투두_요청, 골룸_노드_별_기간_요청);
        final Long 골룸_아이디 = 골룸을_생성하고_아이디를_알아낸다(골룸_생성_요청, 로그인_토큰_정보);

        회원가입을_한다("identifier2", "password2@", "팔로워", "010-1234-5555", GenderType.FEMALE, LocalDate.of(2000, 1, 1));
        final String 팔로워_액세스_토큰 = 로그인을_한다("identifier2", "password2@");

        골룸_참가_요청(골룸_아이디, 팔로워_액세스_토큰);
        goalRoomScheduler.startGoalRooms();

        // when
        testHelper.골룸의_종료날짜를_변경한다(골룸_아이디, 오늘.minusDays(1));
        goalRoomScheduler.endGoalRooms();
        final MemberGoalRoomResponse 요청_응답값 = 사용자의_특정_골룸_정보를_조회한다(로그인_토큰_정보, 골룸_아이디);

        // then
        final MemberGoalRoomResponse 예상되는_응답 = new MemberGoalRoomResponse("골룸 이름", "COMPLETED", 1L,
                2, 10, 오늘, 오늘.minusDays(1), 1L,
                new GoalRoomRoadmapNodesResponse(false, false,
                        List.of(
                                new GoalRoomRoadmapNodeResponse(1L, "로드맵 1주차", 오늘, 십일_후, 3),
                                new GoalRoomRoadmapNodeResponse(2L, "로드맵 2주차", 십일_후.plusDays(1), 이십일_후, 3))),
                List.of(new GoalRoomTodoResponse(1L, "투두 콘텐츠", 오늘, 십일_후,
                        new GoalRoomToDoCheckResponse(false))),
                Collections.emptyList()
                );

        assertThat(요청_응답값).isEqualTo(예상되는_응답);
    }

    @Test
    void 골룸_종료시_종료_날짜가_어제가_아니면_아무_일도_일어나지_않는다() {
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

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest("투두 콘텐츠", 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_첫번째_노드_아이디, 3, 오늘, 십일_후),
                new GoalRoomRoadmapNodeRequest(로드맵_두번째_노드_아이디, 3, 십일_후.plusDays(1), 이십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_아이디, "골룸 이름",
                10, 골룸_투두_요청, 골룸_노드_별_기간_요청);
        final Long 골룸_아이디 = 골룸을_생성하고_아이디를_알아낸다(골룸_생성_요청, 로그인_토큰_정보);

        회원가입을_한다("identifier2", "password2@", "팔로워", "010-1234-5555", GenderType.FEMALE, LocalDate.of(2000, 1, 1));
        final String 팔로워_액세스_토큰 = 로그인을_한다("identifier2", "password2@");

        골룸_참가_요청(골룸_아이디, 팔로워_액세스_토큰);
        goalRoomScheduler.startGoalRooms();

        // when
        goalRoomScheduler.endGoalRooms();
        final MemberGoalRoomResponse 요청_응답값 = 사용자의_특정_골룸_정보를_조회한다(로그인_토큰_정보, 골룸_아이디);

        // then
        final MemberGoalRoomResponse 예상되는_응답 = new MemberGoalRoomResponse("골룸 이름", "RUNNING", 1L,
                2, 10, 오늘, 이십일_후, 1L,
                new GoalRoomRoadmapNodesResponse(false, false,
                        List.of(
                                new GoalRoomRoadmapNodeResponse(1L, "로드맵 1주차", 오늘, 십일_후, 3),
                                new GoalRoomRoadmapNodeResponse(2L, "로드맵 2주차", 십일_후.plusDays(1), 이십일_후, 3))),
                List.of(new GoalRoomTodoResponse(1L, "투두 콘텐츠", 오늘, 십일_후,
                        new GoalRoomToDoCheckResponse(false))),
                Collections.emptyList()
        );

        assertThat(요청_응답값).isEqualTo(예상되는_응답);
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
}
