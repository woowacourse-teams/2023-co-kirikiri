package co.kirikiri.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomPendingMember;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNodes;
import co.kirikiri.domain.goalroom.GoalRoomRole;
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
import co.kirikiri.persistence.goalroom.GoalRoomPendingMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.persistence.roadmap.RoadmapCategoryRepository;
import co.kirikiri.persistence.roadmap.RoadmapRepository;
import co.kirikiri.service.dto.auth.request.LoginRequest;
import co.kirikiri.service.dto.auth.response.AuthenticationResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomCertifiedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomNodeResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomResponse;
import co.kirikiri.service.dto.member.request.GenderType;
import co.kirikiri.service.dto.member.request.MemberJoinRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapDifficultyType;
import co.kirikiri.service.dto.roadmap.request.RoadmapNodeSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapSaveRequest;
import co.kirikiri.service.dto.roadmap.response.RoadmapContentResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapNodeResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapResponse;
import io.restassured.common.mapper.TypeRef;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

class GoalRoomReadIntegrationTest extends IntegrationTest {

    private static final String IDENTIFIER = "identifier1";
    private static final String PASSWORD = "password1!";
    private static final LocalDate TODAY = LocalDate.now();
    private static final LocalDate TEN_DAY_LATER = TODAY.plusDays(10);
    private static final LocalDate TWENTY_DAY_LAYER = TODAY.plusDays(20);
    private static final LocalDate THIRTY_DAY_LATER = TODAY.plusDays(30);

    private final RoadmapRepository roadmapRepository;
    private final GoalRoomRepository goalRoomRepository;
    private final RoadmapCategoryRepository roadmapCategoryRepository;
    private final GoalRoomPendingMemberRepository goalRoomPendingMemberRepository;

    public GoalRoomReadIntegrationTest(final RoadmapRepository roadmapRepository,
                                       final GoalRoomRepository goalRoomRepository,
                                       final RoadmapCategoryRepository roadmapCategoryRepository,
                                       final GoalRoomPendingMemberRepository goalRoomPendingMemberRepository) {
        this.roadmapRepository = roadmapRepository;
        this.goalRoomRepository = goalRoomRepository;
        this.roadmapCategoryRepository = roadmapCategoryRepository;
        this.goalRoomPendingMemberRepository = goalRoomPendingMemberRepository;
    }

    @Test
    void 골룸_아이디로_골룸_정보를_조회한다() {
        // given
        final Member 크리에이터 = 크리에이터를_저장한다();
        final String 로그인_토큰_정보 = 로그인();
        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");
        final Long 로드맵_아이디 = 제목별로_로드맵을_생성한다(로그인_토큰_정보, 여행_카테고리, "첫 번째 로드맵");
        final RoadmapResponse 로드맵_응답 = 로드맵을_조회한다(로드맵_아이디);
        final List<RoadmapContent> 로드맵_본문_리스트 = 로드맵_응답으로부터_로드맵_본문을_생성한다(크리에이터, 여행_카테고리, 로드맵_응답).getValues();
        final GoalRoom 골룸 = 골룸을_저장한다(로드맵_본문_리스트);
        골룸_대기_사용자를_저장한다(크리에이터, 골룸);

        // when
        final GoalRoomResponse 골룸_응답값 = given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(API_PREFIX + "/goal-rooms/{goalRoomId}", 골룸.getId())
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
        final Member 크리에이터 = 크리에이터를_저장한다();
        final String 로그인_토큰_정보 = 로그인();
        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");
        final Long 로드맵_아이디 = 제목별로_로드맵을_생성한다(로그인_토큰_정보, 여행_카테고리, "첫 번째 로드맵");
        final RoadmapResponse 로드맵_응답 = 로드맵을_조회한다(로드맵_아이디);
        final List<RoadmapContent> 로드맵_본문_리스트 = 로드맵_응답으로부터_로드맵_본문을_생성한다(크리에이터, 여행_카테고리, 로드맵_응답).getValues();
        final GoalRoom 골룸 = 골룸을_저장한다(로드맵_본문_리스트);
        골룸_대기_사용자를_저장한다(크리에이터, 골룸);

        // when
        final GoalRoomCertifiedResponse 골룸_응답값 = given()
                .header(AUTHORIZATION, 로그인_토큰_정보)
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(API_PREFIX + "/goal-rooms/{goalRoomId}", 골룸.getId())
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
                new EncryptedPassword(new Password(PASSWORD)),
                new MemberProfile(Gender.MALE, 생년월일, new Nickname(닉네임), 전화번호));
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
        final RoadmapSaveRequest 로드맵_저장_요청 = new RoadmapSaveRequest(로드맵_카테고리.getId(), 로드맵_제목, "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30, List.of(
                new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용"),
                new RoadmapNodeSaveRequest("로드맵 2주차", "로드맵 2주차 내용")
        ));

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

    private RoadmapContents 로드맵_응답으로부터_로드맵_본문을_생성한다(final Member 크리에이터, final RoadmapCategory 카테고리,
                                                    final RoadmapResponse 로드맵_응답) {
        final Roadmap 로드맵 = new Roadmap(로드맵_응답.roadmapTitle(), 로드맵_응답.introduction(),
                로드맵_응답.recommendedRoadmapPeriod(), RoadmapDifficulty.valueOf(로드맵_응답.difficulty()), 크리에이터, 카테고리);
        final RoadmapContentResponse 로드맵_본문_응답 = 로드맵_응답.content();
        final RoadmapContent 로드맵_본문 = new RoadmapContent(로드맵_본문_응답.content());
        final List<RoadmapNodeResponse> 로드맵_본문_노드_응답 = 로드맵_본문_응답.nodes();
        final List<RoadmapNode> 로드맵_노드_리스트 = 로드맵_본문_노드_응답.stream()
                .map(response -> new RoadmapNode(response.title(), response.description()))
                .toList();

        로드맵_본문.addNodes(new RoadmapNodes(로드맵_노드_리스트));
        로드맵.addContent(로드맵_본문);

        // TODO 추후 골룸 생성 API가 들어오면 제거될 로직
        final Roadmap 저장된_로드맵 = roadmapRepository.save(로드맵);
        return 저장된_로드맵.getContents();
    }

    private GoalRoom 골룸을_저장한다(final List<RoadmapContent> 로드맵_본문_리스트) {
        final RoadmapContent 로드맵_본문 = 로드맵_본문_리스트.get(0);
        final GoalRoom 골룸 = new GoalRoom(new GoalRoomName("골룸"), new LimitedMemberCount(10), 로드맵_본문);
        final List<RoadmapNode> 로드맵_노드_리스트 = 로드맵_본문.getNodes().getValues();

        final RoadmapNode 첫번째_로드맵_노드 = 로드맵_노드_리스트.get(0);
        final GoalRoomRoadmapNode 첫번째_골룸_노드 = new GoalRoomRoadmapNode(
                new Period(TODAY, TEN_DAY_LATER),
                10, 첫번째_로드맵_노드);

        final RoadmapNode 두번째_로드맵_노드 = 로드맵_노드_리스트.get(1);
        final GoalRoomRoadmapNode 두번째_골룸_노드 = new GoalRoomRoadmapNode(
                new Period(TWENTY_DAY_LAYER, THIRTY_DAY_LATER),
                2, 두번째_로드맵_노드);

        final GoalRoomRoadmapNodes 골룸_노드들 = new GoalRoomRoadmapNodes(List.of(첫번째_골룸_노드, 두번째_골룸_노드));
        골룸.addAllGoalRoomRoadmapNodes(골룸_노드들);
        return goalRoomRepository.save(골룸);
    }

    private GoalRoomResponse 예상하는_골룸_응답을_생성한다() {
        final List<GoalRoomNodeResponse> goalRoomNodeResponses = List.of(
                new GoalRoomNodeResponse("로드맵 1주차", TODAY, TEN_DAY_LATER, 10),
                new GoalRoomNodeResponse("로드맵 2주차", TWENTY_DAY_LAYER, THIRTY_DAY_LATER, 2));
        return new GoalRoomResponse("골룸", 1, 10, goalRoomNodeResponses, 31);
    }

    private GoalRoomCertifiedResponse 로그인후_예상하는_골룸_응답을_생성한다() {
        final List<GoalRoomNodeResponse> goalRoomNodeResponses = List.of(
                new GoalRoomNodeResponse("로드맵 1주차", TODAY, TEN_DAY_LATER, 10),
                new GoalRoomNodeResponse("로드맵 2주차", TWENTY_DAY_LAYER, THIRTY_DAY_LATER, 2));
        return new GoalRoomCertifiedResponse("골룸", 1, 10, goalRoomNodeResponses, 31, true);
    }

    private void 골룸_대기_사용자를_저장한다(final Member 크리에이터, final GoalRoom 골룸) {
        final GoalRoomPendingMember 골룸_대기_사용자 = new GoalRoomPendingMember(GoalRoomRole.LEADER,
                LocalDateTime.of(2023, 7, 19, 12, 0, 0), 골룸, 크리에이터);
        goalRoomPendingMemberRepository.save(골룸_대기_사용자);
    }
}
