package co.kirikiri.integration;

import static co.kirikiri.integration.fixture.AuthenticationAPIFixture.로그인;
import static co.kirikiri.integration.fixture.CommonFixture.BEARER_TOKEN_FORMAT;
import static co.kirikiri.integration.fixture.CommonFixture.LOCATION;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.골룸_생성;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.골룸_참가_요청;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.골룸을_시작한다;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.기본_골룸_생성;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.사용자의_특정_골룸_정보를_조회한다;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.십일_후;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.오늘;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.이십일_후;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.정상적인_골룸_노드_인증_횟수;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.정상적인_골룸_이름;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.정상적인_골룸_제한_인원;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.정상적인_골룸_투두_컨텐츠;
import static co.kirikiri.integration.fixture.MemberAPIFixture.회원가입;
import static co.kirikiri.integration.fixture.RoadmapAPIFixture.로드맵_생성;
import static co.kirikiri.integration.fixture.RoadmapAPIFixture.로드맵을_아이디로_조회하고_응답객체를_반환한다;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomStatus;
import co.kirikiri.integration.helper.InitIntegrationTest;
import co.kirikiri.persistence.goalroom.GoalRoomMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomPendingMemberRepository;
import co.kirikiri.service.GoalRoomScheduler;
import co.kirikiri.service.dto.auth.request.LoginRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomCreateRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomRoadmapNodeRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomTodoRequest;
import co.kirikiri.service.dto.member.request.GenderType;
import co.kirikiri.service.dto.member.request.MemberJoinRequest;
import co.kirikiri.service.dto.member.response.MemberGoalRoomResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
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
        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 어드민_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final Long 기본_골룸_아이디 = 기본_골룸_생성(기본_로그인_토큰, 로드맵_응답);
        final GoalRoom 골룸 = new GoalRoom(기본_골룸_아이디, null, null, null, null);

        final MemberJoinRequest 팔로워_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follower", "010-1234-1234", GenderType.FEMALE, LocalDate.of(1999, 9, 9));
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
    void 골룸의_시작날짜가_오늘보다_이후이면_아무일도_일어나지_않는다() throws IOException {
        // given
        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 어드민_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final Long 기본_골룸_아이디 = 기본_골룸_생성(기본_로그인_토큰, 로드맵_응답);
        final GoalRoom 골룸 = new GoalRoom(기본_골룸_아이디, null, null, null, null);

        final MemberJoinRequest 팔로워_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follower", "010-1234-1234", GenderType.FEMALE, LocalDate.of(1999, 9, 9));
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
        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 어드민_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final Long 기본_골룸_아이디 = 기본_골룸_생성(기본_로그인_토큰, 로드맵_응답);

        final MemberJoinRequest 팔로워_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follower", "010-1234-1234", GenderType.FEMALE, LocalDate.of(1999, 9, 9));
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
        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 어드민_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final Long 기본_골룸_아이디 = 기본_골룸_생성(기본_로그인_토큰, 로드맵_응답);

        final MemberJoinRequest 팔로워_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follower", "010-1234-1234", GenderType.FEMALE, LocalDate.of(1999, 9, 9));
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

    @Test
    void 진행_중인_사용자_단일_골룸을_조회할_때_진행_중인_노드_기간이_아니면_빈_인증피드를_반환한다() throws IOException {
        // given
        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 어드민_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final Long 기본_골룸_아이디 = 십일_후에_시작하는_골룸_생성(기본_로그인_토큰, 로드맵_응답);

        final MemberJoinRequest 팔로워_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follower", "010-1234-1234", GenderType.FEMALE, LocalDate.of(1999, 9, 9));
        final LoginRequest 팔로워_로그인_요청 = new LoginRequest(팔로워_회원_가입_요청.identifier(), 팔로워_회원_가입_요청.password());
        회원가입(팔로워_회원_가입_요청);
        final String 팔로워_액세스_토큰 = 로그인(팔로워_로그인_요청).accessToken();

        골룸_참가_요청(기본_골룸_아이디, 팔로워_액세스_토큰);

        testTransactionService.골룸의_시작날짜를_변경한다(기본_골룸_아이디, 오늘);
        골룸을_시작한다(기본_로그인_토큰, 기본_골룸_아이디);

        // when
        final MemberGoalRoomResponse 요청_응답값 = 사용자의_특정_골룸_정보를_조회한다(기본_로그인_토큰, 기본_골룸_아이디);

        // then
        assertThat(요청_응답값.checkFeeds()).isEmpty();
    }

    @Test
    void 모집_중인_사용자_단일_골룸_조회_시_인증_피드가_빈_응답을_반환한다() throws IOException {
        // given
        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 어드민_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final Long 기본_골룸_아이디 = 기본_골룸_생성(기본_로그인_토큰, 로드맵_응답);

        final MemberJoinRequest 팔로워_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follower", "010-1234-1234", GenderType.FEMALE, LocalDate.of(1999, 9, 9));
        final LoginRequest 팔로워_로그인_요청 = new LoginRequest(팔로워_회원_가입_요청.identifier(), 팔로워_회원_가입_요청.password());
        회원가입(팔로워_회원_가입_요청);
        final String 팔로워_액세스_토큰 = 로그인(팔로워_로그인_요청).accessToken();

        골룸_참가_요청(기본_골룸_아이디, 팔로워_액세스_토큰);

        //when
        final MemberGoalRoomResponse 요청_응답값 = 사용자의_특정_골룸_정보를_조회한다(기본_로그인_토큰, 기본_골룸_아이디);

        //then
        assertThat(요청_응답값.checkFeeds()).isEmpty();
    }

    private Long 십일_후에_시작하는_골룸_생성(final String 액세스_토큰, final RoadmapResponse 로드맵_응답) {
        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 십일_후, 이십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_응답.content().nodes().get(0).id(), 정상적인_골룸_노드_인증_횟수, 십일_후, 이십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_응답.content().id(), 정상적인_골룸_이름,
                정상적인_골룸_제한_인원, 골룸_투두_요청,
                골룸_노드_별_기간_요청);
        final String Location_헤더 = 골룸_생성(골룸_생성_요청, 액세스_토큰).response().header(LOCATION);
        return Long.parseLong(Location_헤더.substring(16));
    }
}
