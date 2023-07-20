package co.kirikiri.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomMember;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNodes;
import co.kirikiri.domain.goalroom.GoalRoomRole;
import co.kirikiri.domain.goalroom.GoalRoomStatus;
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
import co.kirikiri.domain.roadmap.RoadmapDifficulty;
import co.kirikiri.domain.roadmap.RoadmapNode;
import co.kirikiri.domain.roadmap.RoadmapNodes;
import co.kirikiri.integration.helper.IntegrationTest;
import co.kirikiri.persistence.goalroom.GoalRoomMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.persistence.roadmap.RoadmapCategoryRepository;
import co.kirikiri.persistence.roadmap.RoadmapRepository;
import co.kirikiri.service.dto.ErrorResponse;
import co.kirikiri.service.dto.auth.request.LoginRequest;
import co.kirikiri.service.dto.auth.response.AuthenticationResponse;
import co.kirikiri.service.dto.member.GenderType;
import co.kirikiri.service.dto.member.request.MemberJoinRequest;
import co.kirikiri.service.dto.roadmap.RoadmapContentResponse;
import co.kirikiri.service.dto.roadmap.RoadmapDifficultyType;
import co.kirikiri.service.dto.roadmap.RoadmapNodeResponse;
import co.kirikiri.service.dto.roadmap.RoadmapNodeSaveRequest;
import co.kirikiri.service.dto.roadmap.RoadmapResponse;
import co.kirikiri.service.dto.roadmap.RoadmapReviewSaveRequest;
import co.kirikiri.service.dto.roadmap.RoadmapSaveRequest;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class RoadmapIntegrationTest extends IntegrationTest {

    private static final String PASSWORD = "password1!";

    private final RoadmapRepository roadmapRepository;
    private final GoalRoomRepository goalRoomRepository;
    private final GoalRoomMemberRepository goalRoomMemberRepository;
    private final RoadmapCategoryRepository roadmapCategoryRepository;

    public RoadmapIntegrationTest(final RoadmapRepository roadmapRepository,
                                  final GoalRoomRepository goalRoomRepository,
                                  final GoalRoomMemberRepository goalRoomMemberRepository,
                                  final RoadmapCategoryRepository roadmapCategoryRepository) {
        this.roadmapRepository = roadmapRepository;
        this.goalRoomRepository = goalRoomRepository;
        this.goalRoomMemberRepository = goalRoomMemberRepository;
        this.roadmapCategoryRepository = roadmapCategoryRepository;
    }

    @Test
    void 로드맵_리뷰를_생성한다() {
        // given
        final Member 크리에이터 = 사용자를_생성한다("크리에이터", "creator");
        final Member 리더 = 사용자를_생성한다("리더", "leader");
        final Member 팔로워 = 사용자를_생성한다("팔로워", "follower");

        final String 크리에이터_토큰_정보 = 로그인(크리에이터.getIdentifier().getValue());
        final String 팔로워_토큰_정보 = 로그인(팔로워.getIdentifier().getValue());

        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");
        final Long 로드맵_아이디 = 제목별로_로드맵을_생성한다(크리에이터_토큰_정보, 여행_카테고리, "첫 번째 로드맵");

        final RoadmapResponse 로드맵_응답 = 로드맵을_조회한다(로드맵_아이디);
        final Roadmap 저장된_로드맵 = 로드맵_응답으로부터_로드맵_본문을_생성한다(크리에이터, 여행_카테고리, 로드맵_응답);
        final List<RoadmapContent> 로드맵_본문_리스트 = 저장된_로드맵.getContents().getValues();

        // TODO 임의로 완료된 골룸을 생성한다 (골룸 완료 API 추가 시 변경)
        final GoalRoom 골룸 = 골룸을_생성한다(로드맵_본문_리스트, GoalRoomStatus.COMPLETED);
        골룸에_대한_참여자_리스트를_생성한다(리더, 팔로워, 골룸);

        final RoadmapReviewSaveRequest 로드맵_리뷰_생성_요청 = new RoadmapReviewSaveRequest("리뷰 내용", 5.0);

        // when
        final ExtractableResponse<Response> 리뷰_생성_요청_결과 = 리뷰를_생성한다(팔로워_토큰_정보, 저장된_로드맵.getId(), 로드맵_리뷰_생성_요청);

        // then
        assertThat(리뷰_생성_요청_결과.statusCode())
                .isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    void 로드맵_리뷰_생성시_내용이_공백이면_예외가_발생한다() {
        // given
        final Member 팔로워 = 사용자를_생성한다("팔로워", "follower");
        final String 팔로워_토큰_정보 = 로그인(팔로워.getIdentifier().getValue());
        final RoadmapReviewSaveRequest 로드맵_리뷰_생성_요청 = new RoadmapReviewSaveRequest(" ", 5.0);

        // when
        final ExtractableResponse<Response> 리뷰_생성_요청_결과 = 리뷰를_생성한다(팔로워_토큰_정보, 1L, 로드맵_리뷰_생성_요청);

        // then
        final List<ErrorResponse> 예외_응답 = 리뷰_생성_요청_결과.as(new TypeRef<>() {
        });
        assertThat(리뷰_생성_요청_결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(예외_응답.get(0).message()).isEqualTo("리뷰를 입력해 주세요.");
    }

    @Test
    void 로드맵_리뷰_생성시_별점이_잘못된_값이면_예외가_발생한다() {
        // given
        final Member 크리에이터 = 사용자를_생성한다("크리에이터", "creator");
        final Member 리더 = 사용자를_생성한다("리더", "leader");
        final Member 팔로워 = 사용자를_생성한다("팔로워", "follower");

        final String 크리에이터_토큰_정보 = 로그인(크리에이터.getIdentifier().getValue());
        final String 팔로워_토큰_정보 = 로그인(팔로워.getIdentifier().getValue());

        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");
        final Long 로드맵_아이디 = 제목별로_로드맵을_생성한다(크리에이터_토큰_정보, 여행_카테고리, "첫 번째 로드맵");

        final RoadmapResponse 로드맵_응답 = 로드맵을_조회한다(로드맵_아이디);
        final Roadmap 저장된_로드맵 = 로드맵_응답으로부터_로드맵_본문을_생성한다(크리에이터, 여행_카테고리, 로드맵_응답);
        final List<RoadmapContent> 로드맵_본문_리스트 = 저장된_로드맵.getContents().getValues();

        final GoalRoom 골룸 = 골룸을_생성한다(로드맵_본문_리스트, GoalRoomStatus.COMPLETED);
        골룸에_대한_참여자_리스트를_생성한다(리더, 팔로워, 골룸);
        final RoadmapReviewSaveRequest 로드맵_리뷰_생성_요청 = new RoadmapReviewSaveRequest("리뷰 내용", 2.4);

        // when
        final ExtractableResponse<Response> 리뷰_생성_요청_결과 = 리뷰를_생성한다(팔로워_토큰_정보, 저장된_로드맵.getId(), 로드맵_리뷰_생성_요청);

        // then
        final ErrorResponse 예외_응답 = 리뷰_생성_요청_결과.as(ErrorResponse.class);
        assertThat(리뷰_생성_요청_결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(예외_응답.message()).isEqualTo("별점은 0부터 5까지 0.5 단위로 설정할 수 있습니다.");
    }

    @Test
    void 로드맵_리뷰_생성시_내용이_1000자가_넘으면_예외가_발생한다() {
        // given
        final Member 크리에이터 = 사용자를_생성한다("크리에이터", "creator");
        final Member 리더 = 사용자를_생성한다("리더", "leader");
        final Member 팔로워 = 사용자를_생성한다("팔로워", "follower");

        final String 크리에이터_토큰_정보 = 로그인(크리에이터.getIdentifier().getValue());
        final String 팔로워_토큰_정보 = 로그인(팔로워.getIdentifier().getValue());

        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");
        final Long 로드맵_아이디 = 제목별로_로드맵을_생성한다(크리에이터_토큰_정보, 여행_카테고리, "첫 번째 로드맵");

        final RoadmapResponse 로드맵_응답 = 로드맵을_조회한다(로드맵_아이디);
        final Roadmap 저장된_로드맵 = 로드맵_응답으로부터_로드맵_본문을_생성한다(크리에이터, 여행_카테고리, 로드맵_응답);
        final List<RoadmapContent> 로드맵_본문_리스트 = 저장된_로드맵.getContents().getValues();
        final GoalRoom 골룸 = 골룸을_생성한다(로드맵_본문_리스트, GoalRoomStatus.COMPLETED);
        골룸에_대한_참여자_리스트를_생성한다(리더, 팔로워, 골룸);

        final String 엄청_긴_리뷰_내용 = "a".repeat(1001);
        final RoadmapReviewSaveRequest 로드맵_리뷰_생성_요청 = new RoadmapReviewSaveRequest(엄청_긴_리뷰_내용, 5.0);

        // when
        final ExtractableResponse<Response> 리뷰_생성_요청_결과 = 리뷰를_생성한다(팔로워_토큰_정보, 저장된_로드맵.getId(), 로드맵_리뷰_생성_요청);

        // then
        final ErrorResponse 예외_응답 = 리뷰_생성_요청_결과.as(ErrorResponse.class);
        assertThat(리뷰_생성_요청_결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(예외_응답.message()).isEqualTo("리뷰는 최대 1000글자까지 입력할 수 있습니다.");
    }

    @Test
    void 로드맵_리뷰_생성시_존재하지_않은_로드맵이면_예외가_발생한다() {
        // given
        final Member 팔로워 = 사용자를_생성한다("팔로워", "follower");
        final String 팔로워_토큰_정보 = 로그인(팔로워.getIdentifier().getValue());
        final RoadmapReviewSaveRequest 로드맵_리뷰_생성_요청 = new RoadmapReviewSaveRequest("리뷰 내용", 5.0);

        // when
        final ExtractableResponse<Response> 리뷰_생성_요청_결과 = 리뷰를_생성한다(팔로워_토큰_정보, 1L, 로드맵_리뷰_생성_요청);

        // then
        final ErrorResponse 예외_응답 = 리뷰_생성_요청_결과.as(ErrorResponse.class);
        assertThat(리뷰_생성_요청_결과.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(예외_응답.message()).isEqualTo("존재하지 않는 로드맵입니다. roadmapId = 1");
    }

    @Test
    void 로드맵_리뷰_생성시_로드맵에_참여한_사용자가_아니면_예외가_발생한다() {
        // given
        final Member 크리에이터 = 사용자를_생성한다("크리에이터", "creator");
        final Member 리더 = 사용자를_생성한다("리더", "leader");
        final Member 팔로워 = 사용자를_생성한다("팔로워", "follower");
        final Member 팔로워2 = 사용자를_생성한다("팔로워2", "follower2");

        final String 크리에이터_토큰_정보 = 로그인(크리에이터.getIdentifier().getValue());
        final String 팔로워2_토큰_정보 = 로그인(팔로워2.getIdentifier().getValue());

        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");
        final Long 로드맵_아이디 = 제목별로_로드맵을_생성한다(크리에이터_토큰_정보, 여행_카테고리, "첫 번째 로드맵");

        final RoadmapResponse 로드맵_응답 = 로드맵을_조회한다(로드맵_아이디);
        final Roadmap 저장된_로드맵 = 로드맵_응답으로부터_로드맵_본문을_생성한다(크리에이터, 여행_카테고리, 로드맵_응답);
        final List<RoadmapContent> 로드맵_본문_리스트 = 저장된_로드맵.getContents().getValues();
        final GoalRoom 골룸 = 골룸을_생성한다(로드맵_본문_리스트, GoalRoomStatus.COMPLETED);
        골룸에_대한_참여자_리스트를_생성한다(리더, 팔로워, 골룸);

        final RoadmapReviewSaveRequest 로드맵_리뷰_생성_요청 = new RoadmapReviewSaveRequest("리뷰 내용", 5.0);

        // when
        final ExtractableResponse<Response> 리뷰_생성_요청_결과 = 리뷰를_생성한다(팔로워2_토큰_정보, 저장된_로드맵.getId(), 로드맵_리뷰_생성_요청);

        // then
        final ErrorResponse 예외_응답 = 리뷰_생성_요청_결과.as(ErrorResponse.class);
        assertThat(리뷰_생성_요청_결과.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(예외_응답.message()).isEqualTo("로드맵에 참여한 사용자가 아닙니다. roadmapId = " + 저장된_로드맵.getId() +
                " memberIdentifier = follower2");
    }

    @Test
    void 로드맵_리뷰_생성시_로드맵_생성자가_리뷰를_달려고_하면_예외가_발생한다() {
        // given
        final Member 크리에이터 = 사용자를_생성한다("크리에이터", "creator");
        final Member 팔로워 = 사용자를_생성한다("팔로워", "follower");

        final String 크리에이터_토큰_정보 = 로그인(크리에이터.getIdentifier().getValue());

        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");
        final Long 로드맵_아이디 = 제목별로_로드맵을_생성한다(크리에이터_토큰_정보, 여행_카테고리, "첫 번째 로드맵");

        final RoadmapResponse 로드맵_응답 = 로드맵을_조회한다(로드맵_아이디);
        final Roadmap 저장된_로드맵 = 로드맵_응답으로부터_로드맵_본문을_생성한다(크리에이터, 여행_카테고리, 로드맵_응답);
        final List<RoadmapContent> 로드맵_본문_리스트 = 저장된_로드맵.getContents().getValues();
        final GoalRoom 골룸 = 골룸을_생성한다(로드맵_본문_리스트, GoalRoomStatus.COMPLETED);
        골룸에_대한_참여자_리스트를_생성한다(크리에이터, 팔로워, 골룸);

        final RoadmapReviewSaveRequest 로드맵_리뷰_생성_요청 = new RoadmapReviewSaveRequest("리뷰 내용", 5.0);

        // when
        final ExtractableResponse<Response> 리뷰_생성_요청_결과 = 리뷰를_생성한다(크리에이터_토큰_정보, 저장된_로드맵.getId(), 로드맵_리뷰_생성_요청);

        // then
        final ErrorResponse 예외_응답 = 리뷰_생성_요청_결과.as(ErrorResponse.class);
        assertThat(리뷰_생성_요청_결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(예외_응답.message()).isEqualTo("로드맵 생성자는 리뷰를 달 수 없습니다. roadmapId = " + 저장된_로드맵.getId() +
                " memberId = " + 크리에이터.getId());
    }

    @Test
    void 로드맵_리뷰_생성시_이미_리뷰를_단적이_있으면_예외가_발생한다() {
        // given
        final Member 크리에이터 = 사용자를_생성한다("크리에이터", "creator");
        final Member 리더 = 사용자를_생성한다("리더", "leader");
        final Member 팔로워 = 사용자를_생성한다("팔로워", "follower");

        final String 크리에이터_토큰_정보 = 로그인(크리에이터.getIdentifier().getValue());
        final String 팔로워_토큰_정보 = 로그인(팔로워.getIdentifier().getValue());

        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");
        final Long 로드맵_아이디 = 제목별로_로드맵을_생성한다(크리에이터_토큰_정보, 여행_카테고리, "첫 번째 로드맵");

        final RoadmapResponse 로드맵_응답 = 로드맵을_조회한다(로드맵_아이디);
        final Roadmap 저장된_로드맵 = 로드맵_응답으로부터_로드맵_본문을_생성한다(크리에이터, 여행_카테고리, 로드맵_응답);
        final List<RoadmapContent> 로드맵_본문_리스트 = 저장된_로드맵.getContents().getValues();
        final GoalRoom 골룸 = 골룸을_생성한다(로드맵_본문_리스트, GoalRoomStatus.COMPLETED);
        골룸에_대한_참여자_리스트를_생성한다(리더, 팔로워, 골룸);

        final RoadmapReviewSaveRequest 로드맵_리뷰_생성_요청 = new RoadmapReviewSaveRequest("리뷰 내용", 5.0);

        // when
        리뷰를_생성한다(팔로워_토큰_정보, 저장된_로드맵.getId(), 로드맵_리뷰_생성_요청);
        final ExtractableResponse<Response> 두번째_리뷰_생성_요청결과 = 리뷰를_생성한다(팔로워_토큰_정보, 저장된_로드맵.getId(), 로드맵_리뷰_생성_요청);

        // then
        final ErrorResponse 예외_응답 = 두번째_리뷰_생성_요청결과.as(ErrorResponse.class);
        assertThat(두번째_리뷰_생성_요청결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(예외_응답.message()).isEqualTo("이미 작성한 리뷰가 존재합니다. roadmapId = " + 저장된_로드맵.getId() +
                " memberId = " + 팔로워.getId());
    }

    private Member 사용자를_생성한다(final String 닉네임, final String 아이디) {
        final String 전화번호 = "010-1234-5678";
        final LocalDate 생년월일 = LocalDate.of(2023, Month.JULY, 12);
        final MemberJoinRequest 회원가입_요청 = new MemberJoinRequest(아이디, PASSWORD, 닉네임, 전화번호, GenderType.MALE, 생년월일);

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

        return new Member(Long.valueOf(저장된_크리에이터_아이디), new Identifier(아이디),
                new EncryptedPassword(new Password(PASSWORD)),
                new MemberProfile(Gender.MALE, 생년월일, new Nickname(닉네임), 전화번호));
    }

    private String 로그인(final String 아이디) {
        final LoginRequest 로그인_요청 = new LoginRequest(아이디, PASSWORD);

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

    private Roadmap 로드맵_응답으로부터_로드맵_본문을_생성한다(final Member 크리에이터, final RoadmapCategory 카테고리,
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
        return roadmapRepository.save(로드맵);
    }

    private GoalRoom 골룸을_생성한다(final List<RoadmapContent> 로드맵_본문_리스트, final GoalRoomStatus status) {
        final RoadmapContent 로드맵_본문 = 로드맵_본문_리스트.get(0);
        final GoalRoom 골룸 = new GoalRoom("골룸", 10, 5, status, 로드맵_본문);
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
        return goalRoomRepository.save(골룸);
    }

    private void 골룸에_대한_참여자_리스트를_생성한다(final Member 리더, final Member 팔로워, final GoalRoom 골룸) {
        final GoalRoomMember 골룸_멤버_리더 = new GoalRoomMember(GoalRoomRole.LEADER,
                LocalDateTime.of(2023, 7, 1, 12, 0), 골룸, 리더);
        final GoalRoomMember 골룸_멤버_팔로워 = new GoalRoomMember(GoalRoomRole.FOLLOWER,
                LocalDateTime.of(2023, 7, 5, 18, 0), 골룸, 팔로워);
        goalRoomMemberRepository.saveAll(List.of(골룸_멤버_리더, 골룸_멤버_팔로워));
    }

    private ExtractableResponse<Response> 리뷰를_생성한다(final String 팔로워_토큰_정보, final Long 로드맵_아이디,
                                                   final RoadmapReviewSaveRequest 로드맵_리뷰_생성_요청) {
        final ExtractableResponse<Response> 리뷰_생성_요청_결과 = given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .header(AUTHORIZATION, 팔로워_토큰_정보)
                .body(로드맵_리뷰_생성_요청)
                .post("/api/roadmaps/reviews/" + 로드맵_아이디)
                .then()
                .log().all()
                .extract();
        return 리뷰_생성_요청_결과;
    }
}
