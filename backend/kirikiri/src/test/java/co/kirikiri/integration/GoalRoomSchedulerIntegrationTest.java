package co.kirikiri.integration;

import static co.kirikiri.integration.fixture.AuthenticationAPIFixture.로그인;
import static co.kirikiri.integration.fixture.CommonFixture.BEARER_TOKEN_FORMAT;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.골룸_참가_요청;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.골룸을_시작한다;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.기본_골룸_생성;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.사용자의_특정_골룸_정보를_조회한다;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.오늘;
import static co.kirikiri.integration.fixture.MemberAPIFixture.DEFAULT_EMAIL;
import static co.kirikiri.integration.fixture.MemberAPIFixture.회원가입;
import static co.kirikiri.integration.fixture.RoadmapAPIFixture.로드맵_생성;
import static co.kirikiri.integration.fixture.RoadmapAPIFixture.로드맵을_아이디로_조회하고_응답객체를_반환한다;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import co.kirikiri.goalroom.domain.GoalRoom;
import co.kirikiri.goalroom.domain.GoalRoomStatus;
import co.kirikiri.goalroom.persistence.GoalRoomMemberRepository;
import co.kirikiri.goalroom.persistence.GoalRoomPendingMemberRepository;
import co.kirikiri.goalroom.service.dto.response.MemberGoalRoomResponse;
import co.kirikiri.goalroom.service.scheduler.GoalRoomScheduler;
import co.kirikiri.integration.helper.InitIntegrationTest;
import co.kirikiri.service.dto.auth.request.LoginRequest;
import co.kirikiri.service.dto.member.request.GenderType;
import co.kirikiri.service.dto.member.request.MemberJoinRequest;
import co.kirikiri.service.dto.roadmap.response.RoadmapResponse;
import java.io.IOException;
import org.junit.jupiter.api.Test;

class GoalRoomSchedulerIntegrationTest extends InitIntegrationTest {

    private final GoalRoomScheduler goalRoomScheduler;
    private final GoalRoomPendingMemberRepository goalRoomPendingMemberRepository;
    private final GoalRoomMemberRepository goalRoomMemberRepository;

    public GoalRoomSchedulerIntegrationTest(final GoalRoomScheduler goalRoomScheduler,
                                            final GoalRoomPendingMemberRepository goalRoomPendingMemberRepository,
                                            final GoalRoomMemberRepository goalRoomMemberRepository) {
        this.goalRoomScheduler = goalRoomScheduler;
        this.goalRoomPendingMemberRepository = goalRoomPendingMemberRepository;
        this.goalRoomMemberRepository = goalRoomMemberRepository;
    }

    @Test
    void 골룸이_시작되면_골룸_대기_사용자에서_골룸_사용자로_이동하고_대기_사용자에서는_제거된다() throws IOException {
        // given
        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final Long 기본_골룸_아이디 = 기본_골룸_생성(기본_로그인_토큰, 로드맵_응답);
        final GoalRoom 골룸 = new GoalRoom(기본_골룸_아이디, null, null, null, null);

        final MemberJoinRequest 팔로워_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follower", GenderType.FEMALE, DEFAULT_EMAIL);
        final LoginRequest 팔로워_로그인_요청 = new LoginRequest(팔로워_회원_가입_요청.identifier(), 팔로워_회원_가입_요청.password());
        회원가입(팔로워_회원_가입_요청);
        final String 팔로워_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(팔로워_로그인_요청).accessToken());

        골룸_참가_요청(기본_골룸_아이디, 팔로워_액세스_토큰);

        // when
        goalRoomScheduler.startGoalRooms();

        // then
        assertAll(
                () -> assertThat(goalRoomPendingMemberRepository.findAllByGoalRoom(골룸)).isEmpty(),
                () -> assertThat(goalRoomMemberRepository.findAllByGoalRoom(골룸)).hasSize(2)
        );
    }

    @Test
    void 자정에_시작_날짜가_오늘_이전이면서_모집_중인_골룸들도_시작된다() throws IOException {
        // given
        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final Long 기본_골룸_아이디 = 기본_골룸_생성(기본_로그인_토큰, 로드맵_응답);
        final GoalRoom 골룸 = new GoalRoom(기본_골룸_아이디, null, null, null, null);

        final MemberJoinRequest 팔로워_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follower", GenderType.FEMALE, DEFAULT_EMAIL);
        final LoginRequest 팔로워_로그인_요청 = new LoginRequest(팔로워_회원_가입_요청.identifier(), 팔로워_회원_가입_요청.password());
        회원가입(팔로워_회원_가입_요청);
        final String 팔로워_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(팔로워_로그인_요청).accessToken());

        골룸_참가_요청(기본_골룸_아이디, 팔로워_액세스_토큰);
        testTransactionService.골룸의_시작날짜를_변경한다(기본_골룸_아이디, 오늘.minusDays(10));

        // when
        goalRoomScheduler.startGoalRooms();

        // then
        assertAll(
                () -> assertThat(goalRoomPendingMemberRepository.findAllByGoalRoom(골룸)).isEmpty(),
                () -> assertThat(goalRoomMemberRepository.findAllByGoalRoom(골룸)).hasSize(2)
        );
    }

    @Test
    void 골룸의_시작날짜가_오늘보다_이후이면_아무일도_일어나지_않는다() throws IOException {
        // given
        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final Long 기본_골룸_아이디 = 기본_골룸_생성(기본_로그인_토큰, 로드맵_응답);
        final GoalRoom 골룸 = new GoalRoom(기본_골룸_아이디, null, null, null, null);

        final MemberJoinRequest 팔로워_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follower", GenderType.FEMALE, DEFAULT_EMAIL);
        final LoginRequest 팔로워_로그인_요청 = new LoginRequest(팔로워_회원_가입_요청.identifier(), 팔로워_회원_가입_요청.password());
        회원가입(팔로워_회원_가입_요청);
        final String 팔로워_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(팔로워_로그인_요청).accessToken());

        골룸_참가_요청(기본_골룸_아이디, 팔로워_액세스_토큰);
        testTransactionService.골룸의_시작날짜를_변경한다(기본_골룸_아이디, 오늘.plusDays(1));

        // when
        goalRoomScheduler.startGoalRooms();

        // then
        assertAll(
                () -> assertThat(goalRoomPendingMemberRepository.findAllByGoalRoom(골룸)).hasSize(2),
                () -> assertThat(goalRoomMemberRepository.findAllByGoalRoom(골룸)).isEmpty()
        );
    }

    @Test
    void 골룸_종료시_종료_날짜가_어제인_골룸의_상태가_COMPLETED로_변경된다() throws IOException {
        // given
        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final Long 기본_골룸_아이디 = 기본_골룸_생성(기본_로그인_토큰, 로드맵_응답);

        final MemberJoinRequest 팔로워_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follower", GenderType.FEMALE, DEFAULT_EMAIL);
        final LoginRequest 팔로워_로그인_요청 = new LoginRequest(팔로워_회원_가입_요청.identifier(), 팔로워_회원_가입_요청.password());
        회원가입(팔로워_회원_가입_요청);
        final String 팔로워_액세스_토큰 = 로그인(팔로워_로그인_요청).accessToken();

        골룸_참가_요청(기본_골룸_아이디, 팔로워_액세스_토큰);
        골룸을_시작한다(기본_로그인_토큰, 기본_골룸_아이디);

        // when
        testTransactionService.골룸의_종료날짜를_변경한다(기본_골룸_아이디, 오늘.minusDays(1));
        goalRoomScheduler.endGoalRooms();
        final MemberGoalRoomResponse 요청_응답값 = 사용자의_특정_골룸_정보를_조회한다(기본_로그인_토큰, 기본_골룸_아이디);

        // then
        assertThat(요청_응답값.status()).isEqualTo(GoalRoomStatus.COMPLETED.name());
    }

    @Test
    void 골룸_종료시_종료_날짜가_어제가_아니면_아무_일도_일어나지_않는다() throws IOException {
        // given
        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final Long 기본_골룸_아이디 = 기본_골룸_생성(기본_로그인_토큰, 로드맵_응답);

        final MemberJoinRequest 팔로워_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follower", GenderType.FEMALE, DEFAULT_EMAIL);
        final LoginRequest 팔로워_로그인_요청 = new LoginRequest(팔로워_회원_가입_요청.identifier(), 팔로워_회원_가입_요청.password());
        회원가입(팔로워_회원_가입_요청);
        final String 팔로워_액세스_토큰 = 로그인(팔로워_로그인_요청).accessToken();

        골룸_참가_요청(기본_골룸_아이디, 팔로워_액세스_토큰);
        골룸을_시작한다(기본_로그인_토큰, 기본_골룸_아이디);

        // when
        goalRoomScheduler.endGoalRooms();
        final MemberGoalRoomResponse 요청_응답값 = 사용자의_특정_골룸_정보를_조회한다(기본_로그인_토큰, 기본_골룸_아이디);

        // then
        assertThat(요청_응답값.status()).isEqualTo(GoalRoomStatus.RUNNING.name());
    }
}
