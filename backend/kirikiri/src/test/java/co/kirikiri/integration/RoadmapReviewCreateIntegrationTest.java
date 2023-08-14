package co.kirikiri.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomMember;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNodes;
import co.kirikiri.domain.goalroom.GoalRoomRole;
import co.kirikiri.domain.goalroom.vo.GoalRoomName;
import co.kirikiri.domain.goalroom.vo.LimitedMemberCount;
import co.kirikiri.domain.goalroom.vo.Period;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.domain.roadmap.RoadmapNode;
import co.kirikiri.integration.helper.TestTransactionService;
import co.kirikiri.persistence.goalroom.GoalRoomMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.persistence.roadmap.RoadmapCategoryRepository;
import co.kirikiri.service.GoalRoomCreateService;
import co.kirikiri.service.dto.ErrorResponse;
import co.kirikiri.service.dto.auth.request.LoginRequest;
import co.kirikiri.service.dto.member.request.GenderType;
import co.kirikiri.service.dto.member.request.MemberJoinRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapReviewSaveRequest;
import co.kirikiri.service.dto.roadmap.response.RoadmapResponse;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class RoadmapReviewCreateIntegrationTest extends GoalRoomReadIntegrationTest {

    protected final MemberRepository memberRepository;
    protected final TestTransactionService testTransactionService;

    public RoadmapReviewCreateIntegrationTest(final RoadmapCategoryRepository roadmapCategoryRepository,
                                              final GoalRoomRepository goalRoomRepository,
                                              final GoalRoomMemberRepository goalRoomMemberRepository,
                                              final GoalRoomCreateService goalRoomCreateService,
                                              final MemberRepository memberRepository,
                                              final TestTransactionService testTransactionService) {
        super(roadmapCategoryRepository, goalRoomRepository, goalRoomMemberRepository, goalRoomCreateService);
        this.memberRepository = memberRepository;
        this.testTransactionService = testTransactionService;
    }

    @Override
    @BeforeEach
    void init() {
        super.init();
    }

    @Test
    void 로드맵_리뷰를_생성한다() throws IOException {
        // given
        final Member 리더 = memberRepository.findById(기본_회원_아이디).get();

        final Long 로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);

        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(로드맵_아이디);
        final RoadmapContent 로드맵_컨텐츠 = testTransactionService.findRoadmapById(로드맵_응답.content().id());

        final MemberJoinRequest 팔로워_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follower", "010-1234-1234", GenderType.FEMALE, LocalDate.of(1999, 9, 9));
        final LoginRequest 팔로워_로그인_요청 = new LoginRequest(팔로워_회원_가입_요청.identifier(), 팔로워_회원_가입_요청.password());
        final Long 팔로워_아이디 = 회원가입(팔로워_회원_가입_요청);
        final String 팔로워_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(팔로워_로그인_요청).accessToken());

        final Member 팔로워 = memberRepository.findById(팔로워_아이디).get();

        // TODO 임의로 완료된 골룸을 생성한다 (골룸 완료 API 추가 시 변경)
        final GoalRoom 골룸 = 완료한_골룸을_생성한다(로드맵_컨텐츠, 리더);
        골룸에_대한_참여자_리스트를_생성한다(리더, 골룸, 팔로워);

        final RoadmapReviewSaveRequest 로드맵_리뷰_생성_요청 = new RoadmapReviewSaveRequest("리뷰 내용", 5.0);

        // when
        final ExtractableResponse<Response> 리뷰_생성_요청_결과 = 리뷰를_생성한다(팔로워_액세스_토큰, 로드맵_아이디, 로드맵_리뷰_생성_요청);

        // then
        assertThat(리뷰_생성_요청_결과.statusCode())
                .isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    void 로드맵_리뷰_생성시_별점이_null이면_예외가_발생한다() throws IOException {
        // given
        final Member 리더 = memberRepository.findById(기본_회원_아이디).get();

        final Long 로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);

        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(로드맵_아이디);
        final RoadmapContent 로드맵_컨텐츠 = testTransactionService.findRoadmapById(로드맵_응답.content().id());

        final MemberJoinRequest 팔로워_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follower", "010-1234-1234", GenderType.FEMALE, LocalDate.of(1999, 9, 9));
        final LoginRequest 팔로워_로그인_요청 = new LoginRequest(팔로워_회원_가입_요청.identifier(), 팔로워_회원_가입_요청.password());
        final Long 팔로워_아이디 = 회원가입(팔로워_회원_가입_요청);
        final String 팔로워_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(팔로워_로그인_요청).accessToken());

        final Member 팔로워 = memberRepository.findById(팔로워_아이디).get();

        // TODO 임의로 완료된 골룸을 생성한다 (골룸 완료 API 추가 시 변경)
        final GoalRoom 골룸 = 완료한_골룸을_생성한다(로드맵_컨텐츠, 리더);
        골룸에_대한_참여자_리스트를_생성한다(리더, 골룸, 팔로워);

        final RoadmapReviewSaveRequest 로드맵_리뷰_생성_요청 = new RoadmapReviewSaveRequest(" ", null);

        // when
        final ExtractableResponse<Response> 리뷰_생성_요청_결과 = 리뷰를_생성한다(팔로워_액세스_토큰, 1L, 로드맵_리뷰_생성_요청);

        // then
        final List<ErrorResponse> 예외_응답 = 리뷰_생성_요청_결과.as(new TypeRef<>() {
        });
        assertThat(리뷰_생성_요청_결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(예외_응답.get(0).message()).isEqualTo("별점을 입력해 주세요.");
    }

    @Test
    void 로드맵_리뷰_생성시_별점이_잘못된_값이면_예외가_발생한다() throws IOException {
        // given
        final Member 리더 = memberRepository.findById(기본_회원_아이디).get();

        final Long 로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);

        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(로드맵_아이디);
        final RoadmapContent 로드맵_컨텐츠 = testTransactionService.findRoadmapById(로드맵_응답.content().id());

        final MemberJoinRequest 팔로워_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follower", "010-1234-1234", GenderType.FEMALE, LocalDate.of(1999, 9, 9));
        final LoginRequest 팔로워_로그인_요청 = new LoginRequest(팔로워_회원_가입_요청.identifier(), 팔로워_회원_가입_요청.password());
        final Long 팔로워_아이디 = 회원가입(팔로워_회원_가입_요청);
        final String 팔로워_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(팔로워_로그인_요청).accessToken());

        final Member 팔로워 = memberRepository.findById(팔로워_아이디).get();

        // TODO 임의로 완료된 골룸을 생성한다 (골룸 완료 API 추가 시 변경)
        final GoalRoom 골룸 = 완료한_골룸을_생성한다(로드맵_컨텐츠, 리더);
        골룸에_대한_참여자_리스트를_생성한다(리더, 골룸, 팔로워);

        final RoadmapReviewSaveRequest 로드맵_리뷰_생성_요청 = new RoadmapReviewSaveRequest("리뷰 내용", 2.4);

        // when
        final ExtractableResponse<Response> 리뷰_생성_요청_결과 = 리뷰를_생성한다(팔로워_액세스_토큰, 로드맵_아이디, 로드맵_리뷰_생성_요청);

        // then
        final ErrorResponse 예외_응답 = 리뷰_생성_요청_결과.as(ErrorResponse.class);
        assertThat(리뷰_생성_요청_결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(예외_응답.message()).isEqualTo("별점은 0부터 5까지 0.5 단위로 설정할 수 있습니다.");
    }

    @Test
    void 로드맵_리뷰_생성시_내용이_1000자가_넘으면_예외가_발생한다() throws IOException {
        // given
        final Member 리더 = memberRepository.findById(기본_회원_아이디).get();

        final Long 로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);

        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(로드맵_아이디);
        final RoadmapContent 로드맵_컨텐츠 = testTransactionService.findRoadmapById(로드맵_응답.content().id());

        final MemberJoinRequest 팔로워_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follower", "010-1234-1234", GenderType.FEMALE, LocalDate.of(1999, 9, 9));
        final LoginRequest 팔로워_로그인_요청 = new LoginRequest(팔로워_회원_가입_요청.identifier(), 팔로워_회원_가입_요청.password());
        final Long 팔로워_아이디 = 회원가입(팔로워_회원_가입_요청);
        final String 팔로워_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(팔로워_로그인_요청).accessToken());

        final Member 팔로워 = memberRepository.findById(팔로워_아이디).get();

        // TODO 임의로 완료된 골룸을 생성한다 (골룸 완료 API 추가 시 변경)
        final GoalRoom 골룸 = 완료한_골룸을_생성한다(로드맵_컨텐츠, 리더);
        골룸에_대한_참여자_리스트를_생성한다(리더, 골룸, 팔로워);

        final String 엄청_긴_리뷰_내용 = "a".repeat(1001);
        final RoadmapReviewSaveRequest 로드맵_리뷰_생성_요청 = new RoadmapReviewSaveRequest(엄청_긴_리뷰_내용, 5.0);

        // when
        final ExtractableResponse<Response> 리뷰_생성_요청_결과 = 리뷰를_생성한다(팔로워_액세스_토큰, 로드맵_아이디, 로드맵_리뷰_생성_요청);

        // then
        final ErrorResponse 예외_응답 = 리뷰_생성_요청_결과.as(ErrorResponse.class);
        assertThat(리뷰_생성_요청_결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(예외_응답.message()).isEqualTo("리뷰는 최대 1000글자까지 입력할 수 있습니다.");
    }

    @Test
    void 로드맵_리뷰_생성시_존재하지_않은_로드맵이면_예외가_발생한다() {
        // given
        final MemberJoinRequest 팔로워_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follower", "010-1234-1234", GenderType.FEMALE, LocalDate.of(1999, 9, 9));
        final LoginRequest 팔로워_로그인_요청 = new LoginRequest(팔로워_회원_가입_요청.identifier(), 팔로워_회원_가입_요청.password());
        회원가입(팔로워_회원_가입_요청);
        final String 팔로워_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(팔로워_로그인_요청).accessToken());

        final RoadmapReviewSaveRequest 로드맵_리뷰_생성_요청 = new RoadmapReviewSaveRequest("리뷰 내용", 5.0);

        // when
        final ExtractableResponse<Response> 리뷰_생성_요청_결과 = 리뷰를_생성한다(팔로워_액세스_토큰, 1L, 로드맵_리뷰_생성_요청);

        // then
        final ErrorResponse 예외_응답 = 리뷰_생성_요청_결과.as(ErrorResponse.class);
        assertThat(리뷰_생성_요청_결과.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(예외_응답.message()).isEqualTo("존재하지 않는 로드맵입니다. roadmapId = 1");
    }

    @Test
    void 로드맵_리뷰_생성시_완료한_골룸이_없다면_예외가_발생한다() throws IOException {
        // given
        final Member 리더 = memberRepository.findById(기본_회원_아이디).get();

        final Long 로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);

        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(로드맵_아이디);
        final RoadmapContent 로드맵_컨텐츠 = testTransactionService.findRoadmapById(로드맵_응답.content().id());

        final MemberJoinRequest 팔로워_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follower", "010-1234-1234", GenderType.FEMALE, LocalDate.of(1999, 9, 9));
        final LoginRequest 팔로워_로그인_요청 = new LoginRequest(팔로워_회원_가입_요청.identifier(), 팔로워_회원_가입_요청.password());
        final Long 팔로워_아이디 = 회원가입(팔로워_회원_가입_요청);
        final String 팔로워_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(팔로워_로그인_요청).accessToken());

        final Member 팔로워 = memberRepository.findById(팔로워_아이디).get();

        final GoalRoom 골룸 = 진행중인_골룸을_생성한다(로드맵_컨텐츠, 리더);

        골룸에_대한_참여자_리스트를_생성한다(리더, 골룸, 팔로워);

        final RoadmapReviewSaveRequest 로드맵_리뷰_생성_요청 = new RoadmapReviewSaveRequest("리뷰 내용", 5.0);

        // when
        final ExtractableResponse<Response> 리뷰_생성_요청_결과 = 리뷰를_생성한다(팔로워_액세스_토큰, 로드맵_아이디, 로드맵_리뷰_생성_요청);

        // then
        final ErrorResponse 예외_응답 = 리뷰_생성_요청_결과.as(ErrorResponse.class);
        assertThat(리뷰_생성_요청_결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(예외_응답.message()).isEqualTo("로드맵에 대해서 완료된 골룸이 존재하지 않습니다. roadmapId = " + 로드맵_아이디 +
                " memberIdentifier = identifier2");
    }

    @Test
    void 로드맵_리뷰_생성시_로드맵_생성자가_리뷰를_달려고_하면_예외가_발생한다() throws IOException {
        // given
        final Member 리더 = memberRepository.findById(기본_회원_아이디).get();

        final Long 로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);

        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(로드맵_아이디);
        final RoadmapContent 로드맵_컨텐츠 = testTransactionService.findRoadmapById(로드맵_응답.content().id());

        final MemberJoinRequest 팔로워_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follower", "010-1234-1234", GenderType.FEMALE, LocalDate.of(1999, 9, 9));
        final LoginRequest 팔로워_로그인_요청 = new LoginRequest(팔로워_회원_가입_요청.identifier(), 팔로워_회원_가입_요청.password());
        final Long 팔로워_아이디 = 회원가입(팔로워_회원_가입_요청);
        final String 팔로워_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(팔로워_로그인_요청).accessToken());

        final Member 팔로워 = memberRepository.findById(팔로워_아이디).get();

        final GoalRoom 골룸 = 완료한_골룸을_생성한다(로드맵_컨텐츠, 리더);
        골룸에_대한_참여자_리스트를_생성한다(리더, 골룸, 팔로워);

        final RoadmapReviewSaveRequest 로드맵_리뷰_생성_요청 = new RoadmapReviewSaveRequest("리뷰 내용", 5.0);

        // when
        final ExtractableResponse<Response> 리뷰_생성_요청_결과 = 리뷰를_생성한다(기본_로그인_토큰, 로드맵_아이디, 로드맵_리뷰_생성_요청);

        // then
        final ErrorResponse 예외_응답 = 리뷰_생성_요청_결과.as(ErrorResponse.class);
        assertThat(리뷰_생성_요청_결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(예외_응답.message()).isEqualTo("로드맵 생성자는 리뷰를 달 수 없습니다. roadmapId = " + 로드맵_아이디 +
                " memberId = " + 기본_회원_아이디);
    }

    @Test
    void 로드맵_리뷰_생성시_이미_리뷰를_단적이_있으면_예외가_발생한다() throws IOException {
        // given
        final Member 리더 = memberRepository.findById(기본_회원_아이디).get();

        final Long 로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);

        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(로드맵_아이디);
        final RoadmapContent 로드맵_컨텐츠 = testTransactionService.findRoadmapById(로드맵_응답.content().id());

        final MemberJoinRequest 팔로워_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follower", "010-1234-1234", GenderType.FEMALE, LocalDate.of(1999, 9, 9));
        final LoginRequest 팔로워_로그인_요청 = new LoginRequest(팔로워_회원_가입_요청.identifier(), 팔로워_회원_가입_요청.password());
        final Long 팔로워_아이디 = 회원가입(팔로워_회원_가입_요청);
        final String 팔로워_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(팔로워_로그인_요청).accessToken());

        final Member 팔로워 = memberRepository.findById(팔로워_아이디).get();

        final GoalRoom 골룸 = 완료한_골룸을_생성한다(로드맵_컨텐츠, 리더);
        골룸에_대한_참여자_리스트를_생성한다(리더, 골룸, 팔로워);

        final RoadmapReviewSaveRequest 로드맵_리뷰_생성_요청 = new RoadmapReviewSaveRequest("리뷰 내용", 5.0);

        // when
        리뷰를_생성한다(팔로워_액세스_토큰, 로드맵_아이디, 로드맵_리뷰_생성_요청);

        // when
        final ExtractableResponse<Response> 두번째_리뷰_생성_요청결과 = 리뷰를_생성한다(팔로워_액세스_토큰, 로드맵_아이디, 로드맵_리뷰_생성_요청);

        // then
        final ErrorResponse 예외_응답 = 두번째_리뷰_생성_요청결과.as(ErrorResponse.class);
        assertThat(두번째_리뷰_생성_요청결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(예외_응답.message()).isEqualTo("이미 작성한 리뷰가 존재합니다. roadmapId = " + 로드맵_아이디 +
                " memberId = " + 팔로워.getId());
    }

    protected GoalRoom 완료한_골룸을_생성한다(final RoadmapContent 로드맵_본문, final Member 리더) {
        final GoalRoom 골룸 = new GoalRoom(new GoalRoomName("골룸"), new LimitedMemberCount(10), 로드맵_본문, 리더);

        final List<RoadmapNode> 로드맵_노드_리스트 = 로드맵_본문.getNodes().getValues();

        final RoadmapNode 첫번째_로드맵_노드 = 로드맵_노드_리스트.get(0);
        final GoalRoomRoadmapNode 첫번째_골룸_노드 = new GoalRoomRoadmapNode(
                new Period(LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(5)), 3, 첫번째_로드맵_노드);

        final GoalRoomRoadmapNodes 골룸_노드들 = new GoalRoomRoadmapNodes(List.of(첫번째_골룸_노드));
        골룸.addAllGoalRoomRoadmapNodes(골룸_노드들);
        골룸.complete();
        return goalRoomRepository.save(골룸);
    }

    private GoalRoom 진행중인_골룸을_생성한다(final RoadmapContent 로드맵_본문, final Member 리더) {
        final GoalRoom 골룸 = new GoalRoom(new GoalRoomName("골룸"), new LimitedMemberCount(10), 로드맵_본문, 리더);
        final List<RoadmapNode> 로드맵_노드_리스트 = 로드맵_본문.getNodes().getValues();

        final RoadmapNode 첫번째_로드맵_노드 = 로드맵_노드_리스트.get(0);
        final GoalRoomRoadmapNode 첫번째_골룸_노드 = new GoalRoomRoadmapNode(
                new Period(LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(5)), 3, 첫번째_로드맵_노드);

        final GoalRoomRoadmapNodes 골룸_노드들 = new GoalRoomRoadmapNodes(List.of(첫번째_골룸_노드));
        골룸.addAllGoalRoomRoadmapNodes(골룸_노드들);
        return goalRoomRepository.save(골룸);
    }

    protected void 골룸에_대한_참여자_리스트를_생성한다(final Member 리더, final GoalRoom 골룸, final Member... 팔로워들) {
        final GoalRoomMember 골룸_멤버_리더 = new GoalRoomMember(GoalRoomRole.LEADER,
                LocalDateTime.of(2023, 7, 1, 12, 0), 골룸, 리더);
        final List<GoalRoomMember> 골룸_멤버_팔로워_리스트 = new ArrayList<>();
        for (final Member 팔로워 : 팔로워들) {
            final GoalRoomMember 골룸_멤버_팔로워 = new GoalRoomMember(GoalRoomRole.FOLLOWER,
                    LocalDateTime.of(2023, 7, 5, 18, 0), 골룸, 팔로워);
            골룸_멤버_팔로워_리스트.add(골룸_멤버_팔로워);
        }
        goalRoomMemberRepository.save(골룸_멤버_리더);
        goalRoomMemberRepository.saveAll(골룸_멤버_팔로워_리스트);
    }

    protected ExtractableResponse<Response> 리뷰를_생성한다(final String 팔로워_토큰_정보, final Long 로드맵_아이디,
                                                     final RoadmapReviewSaveRequest 로드맵_리뷰_생성_요청) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .header(AUTHORIZATION, 팔로워_토큰_정보)
                .body(로드맵_리뷰_생성_요청)
                .post("/api/roadmaps/" + 로드맵_아이디 + "/reviews")
                .then()
                .log().all()
                .extract();
    }
}
