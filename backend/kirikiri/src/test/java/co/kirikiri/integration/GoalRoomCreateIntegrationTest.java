package co.kirikiri.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomPendingMember;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNodes;
import co.kirikiri.domain.goalroom.GoalRoomRole;
import co.kirikiri.domain.goalroom.GoalRoomStatus;
import co.kirikiri.domain.goalroom.LimitedMemberCount;
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
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.persistence.roadmap.RoadmapCategoryRepository;
import co.kirikiri.persistence.roadmap.RoadmapRepository;
import co.kirikiri.service.dto.auth.request.LoginRequest;
import co.kirikiri.service.dto.auth.response.AuthenticationResponse;
import co.kirikiri.service.dto.member.request.GenderType;
import co.kirikiri.service.dto.member.request.MemberJoinRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapDifficultyType;
import co.kirikiri.service.dto.roadmap.request.RoadmapNodeSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapSaveRequest;
import co.kirikiri.service.dto.roadmap.response.RoadmapContentResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapNodeResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapResponse;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class GoalRoomCreateIntegrationTest extends IntegrationTest {

    private static final String IDENTIFIER = "identifier1";
    private static final String PASSWORD = "password1!";

    private final RoadmapRepository roadmapRepository;
    private final GoalRoomRepository goalRoomRepository;
    private final RoadmapCategoryRepository roadmapCategoryRepository;

    public GoalRoomCreateIntegrationTest(final RoadmapRepository roadmapRepository,
                                         final GoalRoomRepository goalRoomRepository,
                                         final RoadmapCategoryRepository roadmapCategoryRepository) {
        this.roadmapRepository = roadmapRepository;
        this.goalRoomRepository = goalRoomRepository;
        this.roadmapCategoryRepository = roadmapCategoryRepository;
    }

    @Test
    void 골룸에_참가_요청을_보낸다() {
        //given
        final Member 크리에이터 = 크리에이터를_저장한다();
        final String 크리에이터_로그인_토큰_정보 = 로그인(IDENTIFIER);
        final RoadmapCategory 놀라운_카테고리 = 로드맵_카테고리를_저장한다("놀라운 카테고리");
        final Long 로드맵_아이디 = 제목별로_로드맵을_생성한다(크리에이터_로그인_토큰_정보, 놀라운_카테고리, "획기적인 로드맵");
        final RoadmapResponse 로드맵_응답 = 로드맵을_조회한다(로드맵_아이디);
        final List<RoadmapContent> 로드맵_본문_리스트 = 로드맵_응답으로부터_로드맵_본문을_생성한다(크리에이터, 놀라운_카테고리, 로드맵_응답).getValues();
        final GoalRoom 골룸 = 골룸을_저장한다(로드맵_본문_리스트, 크리에이터, 5, GoalRoomStatus.RECRUITING);

        final String 팔로워_아이디 = "identifier2";
        팔로워를_저장한다(팔로워_아이디, "끼리코");
        final String 팔로워_로그인_토큰_정보 = 로그인(팔로워_아이디);

        //when
        final ExtractableResponse<Response> 참가_요청에_대한_응답 = given()
                .log().all()
                .header(AUTHORIZATION, 팔로워_로그인_토큰_정보)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .post(API_PREFIX + "/goal-rooms/{goalRoomId}/join", 골룸.getId())
                .then()
                .log().all()
                .extract();

        //then
        assertThat(참가_요청에_대한_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void 존재하지_않는_골룸_아이디로_참가_요청을_보내면_예외가_발생한다() {
        //given
        final Long 존재하지_않는_골룸_아이디 = 1L;
        final String 팔로워_아이디 = "identifier2";
        팔로워를_저장한다(팔로워_아이디, "끼리코");
        final String 팔로워_로그인_토큰_정보 = 로그인(팔로워_아이디);

        //when
        final ExtractableResponse<Response> 참가_요청에_대한_응답 = given()
                .log().all()
                .header(AUTHORIZATION, 팔로워_로그인_토큰_정보)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .post(API_PREFIX + "/goal-rooms/{goalRoomId}/join", 존재하지_않는_골룸_아이디)
                .then()
                .log().all()
                .extract();

        //then
        final String 예외_메시지 = 참가_요청에_대한_응답.asString();

        assertAll(
                () -> assertThat(참가_요청에_대한_응답.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value()),
                () -> assertThat(예외_메시지)
        );
    }

    @Test
    void 인원이_가득_찬_골룸에_참가_요청을_보내면_예외가_발생한다() {
        //given
        final Member 크리에이터 = 크리에이터를_저장한다();
        final String 크리에이터_로그인_토큰_정보 = 로그인(IDENTIFIER);
        final RoadmapCategory 놀라운_카테고리 = 로드맵_카테고리를_저장한다("놀라운 카테고리");
        final Long 로드맵_아이디 = 제목별로_로드맵을_생성한다(크리에이터_로그인_토큰_정보, 놀라운_카테고리, "획기적인 로드맵");
        final RoadmapResponse 로드맵_응답 = 로드맵을_조회한다(로드맵_아이디);
        final List<RoadmapContent> 로드맵_본문_리스트 = 로드맵_응답으로부터_로드맵_본문을_생성한다(크리에이터, 놀라운_카테고리, 로드맵_응답).getValues();
        final GoalRoom 골룸 = 골룸을_저장한다(로드맵_본문_리스트, 크리에이터, 1, GoalRoomStatus.RECRUITING);

        final String 팔로워_아이디 = "identifier2";
        팔로워를_저장한다(팔로워_아이디, "끼리코");
        final String 팔로워_로그인_토큰_정보 = 로그인(팔로워_아이디);

        //when
        final ExtractableResponse<Response> 참가_요청에_대한_응답 = given()
                .log().all()
                .header(AUTHORIZATION, 팔로워_로그인_토큰_정보)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .post(API_PREFIX + "/goal-rooms/{goalRoomId}/join", 골룸.getId())
                .then()
                .log().all()
                .extract();

        //then
        final String 예외_메시지 = 참가_요청에_대한_응답.asString();

        assertAll(
                () -> assertThat(참가_요청에_대한_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(예외_메시지).contains("제한 인원이 꽉 찬 골룸에는 참여할 수 없습니다.")
        );
    }

    @Test
    void 모집_중이지_않은_골룸에_참가_요청을_보내면_예외가_발생한다() {
        //given
        final Member 크리에이터 = 크리에이터를_저장한다();
        final String 크리에이터_로그인_토큰_정보 = 로그인(IDENTIFIER);
        final RoadmapCategory 놀라운_카테고리 = 로드맵_카테고리를_저장한다("놀라운 카테고리");
        final Long 로드맵_아이디 = 제목별로_로드맵을_생성한다(크리에이터_로그인_토큰_정보, 놀라운_카테고리, "획기적인 로드맵");
        final RoadmapResponse 로드맵_응답 = 로드맵을_조회한다(로드맵_아이디);
        final List<RoadmapContent> 로드맵_본문_리스트 = 로드맵_응답으로부터_로드맵_본문을_생성한다(크리에이터, 놀라운_카테고리, 로드맵_응답).getValues();
        final GoalRoom 골룸 = 골룸을_저장한다(로드맵_본문_리스트, 크리에이터, 5, GoalRoomStatus.RUNNING);

        final String 팔로워_아이디 = "identifier2";
        팔로워를_저장한다(팔로워_아이디, "끼리코");
        final String 팔로워_로그인_토큰_정보 = 로그인(팔로워_아이디);

        //when
        final ExtractableResponse<Response> 참가_요청에_대한_응답 = given()
                .log().all()
                .header(AUTHORIZATION, 팔로워_로그인_토큰_정보)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .post(API_PREFIX + "/goal-rooms/{goalRoomId}/join", 골룸.getId())
                .then()
                .log().all()
                .extract();

        //then
        final String 예외_메시지 = 참가_요청에_대한_응답.asString();

        assertAll(
                () -> assertThat(참가_요청에_대한_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(예외_메시지).contains("모집 중이지 않은 골룸에는 참여할 수 없습니다.")
        );
    }

    @Test
    void 이미_참여한_골룸에_참가_요청을_보내면_예외가_발생한다() {
        //given
        final Member 크리에이터 = 크리에이터를_저장한다();
        final String 크리에이터_로그인_토큰_정보 = 로그인(IDENTIFIER);
        final RoadmapCategory 놀라운_카테고리 = 로드맵_카테고리를_저장한다("놀라운 카테고리");
        final Long 로드맵_아이디 = 제목별로_로드맵을_생성한다(크리에이터_로그인_토큰_정보, 놀라운_카테고리, "획기적인 로드맵");
        final RoadmapResponse 로드맵_응답 = 로드맵을_조회한다(로드맵_아이디);
        final List<RoadmapContent> 로드맵_본문_리스트 = 로드맵_응답으로부터_로드맵_본문을_생성한다(크리에이터, 놀라운_카테고리, 로드맵_응답).getValues();
        final GoalRoom 골룸 = 골룸을_저장한다(로드맵_본문_리스트, 크리에이터, 5, GoalRoomStatus.RECRUITING);

        final String 팔로워_아이디 = "identifier2";
        팔로워를_저장한다(팔로워_아이디, "끼리코");
        final String 팔로워_로그인_토큰_정보 = 로그인(팔로워_아이디);

        given().log().all()
                .header(AUTHORIZATION, 팔로워_로그인_토큰_정보)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .post(API_PREFIX + "/goal-rooms/{goalRoomId}/join", 골룸.getId())
                .then()
                .log().all()
                .extract();

        //when
        final ExtractableResponse<Response> 참가_요청에_대한_응답 = given()
                .log().all()
                .header(AUTHORIZATION, 팔로워_로그인_토큰_정보)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .post(API_PREFIX + "/goal-rooms/{goalRoomId}/join", 골룸.getId())
                .then()
                .log().all()
                .extract();

        //then
        final String 예외_메시지 = 참가_요청에_대한_응답.asString();

        assertAll(
                () -> assertThat(참가_요청에_대한_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(예외_메시지).contains("이미 참여한 골룸에는 참여할 수 없습니다.")
        );
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

    private Member 팔로워를_저장한다(final String identifier, final String nickname) {
        final String 닉네임 = nickname;
        final String 전화번호 = "010-9876-5432";
        final LocalDate 생년월일 = LocalDate.of(2023, Month.JULY, 12);
        final MemberJoinRequest 회원가입_요청 = new MemberJoinRequest(identifier, PASSWORD, 닉네임, 전화번호, GenderType.MALE, 생년월일);

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

        return new Member(Long.valueOf(저장된_크리에이터_아이디), new Identifier(identifier),
                new EncryptedPassword(new Password(PASSWORD)),
                new MemberProfile(Gender.MALE, 생년월일, new Nickname(닉네임), 전화번호));
    }

    private String 로그인(final String identifier) {
        final LoginRequest 로그인_요청 = new LoginRequest(identifier, PASSWORD);

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

    private GoalRoom 골룸을_저장한다(final List<RoadmapContent> 로드맵_본문_리스트, final Member 크리에이터, final Integer 제한_인원,
                              final GoalRoomStatus status) {
        final RoadmapContent 로드맵_본문 = 로드맵_본문_리스트.get(0);
        final GoalRoom 골룸 = new GoalRoom("골룸", new LimitedMemberCount(제한_인원), 로드맵_본문,
                new GoalRoomPendingMember(크리에이터, GoalRoomRole.LEADER));
        final List<RoadmapNode> 로드맵_노드_리스트 = 로드맵_본문.getNodes().getValues();

        final RoadmapNode 첫번째_로드맵_노드 = 로드맵_노드_리스트.get(0);
        final GoalRoomRoadmapNode 첫번째_골룸_노드 = new GoalRoomRoadmapNode(
                LocalDate.of(2023, 7, 19),
                LocalDate.of(2023, 7, 30), 10, 첫번째_로드맵_노드);

        final RoadmapNode 두번째_로드맵_노드 = 로드맵_노드_리스트.get(1);
        final GoalRoomRoadmapNode 두번째_골룸_노드 = new GoalRoomRoadmapNode(
                LocalDate.of(2023, 8, 1),
                LocalDate.of(2023, 8, 5), 2, 두번째_로드맵_노드);

        final GoalRoomRoadmapNodes 골룸_노드들 = new GoalRoomRoadmapNodes(List.of(첫번째_골룸_노드, 두번째_골룸_노드));
        골룸.addGoalRoomRoadmapNodes(골룸_노드들);
        골룸.updateStatus(status);
        return goalRoomRepository.save(골룸);
    }
}
