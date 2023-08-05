package co.kirikiri.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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
import co.kirikiri.persistence.goalroom.GoalRoomMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomPendingMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.persistence.roadmap.RoadmapCategoryRepository;
import co.kirikiri.persistence.roadmap.RoadmapRepository;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

class GoalRoomServiceIntegrationTest extends IntegrationTest {

    private static final LocalDate TODAY = LocalDate.now();

    private final GoalRoomCreateService goalRoomService;
    private final GoalRoomRepository goalRoomRepository;
    private final GoalRoomPendingMemberRepository goalRoomPendingMemberRepository;
    private final GoalRoomMemberRepository goalRoomMemberRepository;
    private final MemberRepository memberRepository;
    private final RoadmapRepository roadmapRepository;
    private final RoadmapCategoryRepository roadmapCategoryRepository;

    public GoalRoomServiceIntegrationTest(final GoalRoomCreateService goalRoomService,
                                          final GoalRoomRepository goalRoomRepository,
                                          final GoalRoomPendingMemberRepository goalRoomPendingMemberRepository,
                                          final GoalRoomMemberRepository goalRoomMemberRepository,
                                          final MemberRepository memberRepository,
                                          final RoadmapRepository roadmapRepository,
                                          final RoadmapCategoryRepository roadmapCategoryRepository) {
        this.goalRoomService = goalRoomService;
        this.goalRoomRepository = goalRoomRepository;
        this.goalRoomPendingMemberRepository = goalRoomPendingMemberRepository;
        this.goalRoomMemberRepository = goalRoomMemberRepository;
        this.memberRepository = memberRepository;
        this.roadmapRepository = roadmapRepository;
        this.roadmapCategoryRepository = roadmapCategoryRepository;
    }

    @Test
    void 골룸이_시작되면_골룸_대기_사용자에서_골룸_사용자로_이동하고_대기_사용자에서는_제거된다() {
        // given
        final Member 크리에이터 = 사용자를_생성한다("creator1", "password1!", "creator", "010-1111-1000");
        final RoadmapCategory 로드맵_카테고리 = 로드맵_카테고리를_저장한다("여행");
        final Roadmap 로드맵 = 로드맵을_생성한다(크리에이터, 로드맵_카테고리);

        final RoadmapContents roadmapContents = 로드맵.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom1 = 골룸을_생성한다(크리에이터, targetRoadmapContent, TODAY);
        final GoalRoom goalRoom2 = 골룸을_생성한다(크리에이터, targetRoadmapContent, TODAY.plusDays(10));

        final Member follower1 = 사용자를_생성한다("identifier1", "password2!", "name1", "010-1111-1111");
        final Member follower2 = 사용자를_생성한다("identifier2", "password3!", "name2", "010-1111-1112");
        final Member follower3 = 사용자를_생성한다("identifier3", "password4!", "name3", "010-1111-1113");

        골룸_대기자를_생성한다(goalRoom1, follower1, GoalRoomRole.LEADER);
        골룸_대기자를_생성한다(goalRoom1, follower2, GoalRoomRole.FOLLOWER);
        골룸_대기자를_생성한다(goalRoom2, follower3, GoalRoomRole.FOLLOWER);

        goalRoom1.join(follower1);
        goalRoom1.join(follower2);
        goalRoom2.join(follower3);

        // when
        goalRoomService.startGoalRooms();

        // then
        assertAll(
                () -> assertThat(goalRoomPendingMemberRepository.findAllByGoalRoom(goalRoom1)).hasSize(0),
                () -> assertThat(goalRoomMemberRepository.findAllByGoalRoom(goalRoom1)).hasSize(3),
                () -> assertThat(goalRoomPendingMemberRepository.findAllByGoalRoom(goalRoom2)).hasSize(2),
                () -> assertThat(goalRoomMemberRepository.findAllByGoalRoom(goalRoom2)).hasSize(0)
        );
    }

    @Test
    void 골룸의_시작날짜가_오늘보다_이후이면_아무일도_일어나지_않는다() {
        // given
        final Member 크리에이터 = 사용자를_생성한다("creator1", "password1!", "creator", "010-1111-1000");
        final RoadmapCategory 로드맵_카테고리 = 로드맵_카테고리를_저장한다("여행");
        final Roadmap 로드맵 = 로드맵을_생성한다(크리에이터, 로드맵_카테고리);

        final RoadmapContents roadmapContents = 로드맵.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom1 = 골룸을_생성한다(크리에이터, targetRoadmapContent, TODAY.plusDays(1));
        final GoalRoom goalRoom2 = 골룸을_생성한다(크리에이터, targetRoadmapContent, TODAY.plusDays(10));

        final Member follower1 = 사용자를_생성한다("identifier1", "password2!", "name1", "010-1111-1111");
        final Member follower2 = 사용자를_생성한다("identifier2", "password3!", "name2", "010-1111-1112");
        final Member follower3 = 사용자를_생성한다("identifier3", "password4!", "name3", "010-1111-1113");

        골룸_대기자를_생성한다(goalRoom1, follower1, GoalRoomRole.LEADER);
        골룸_대기자를_생성한다(goalRoom1, follower2, GoalRoomRole.FOLLOWER);
        골룸_대기자를_생성한다(goalRoom2, follower3, GoalRoomRole.FOLLOWER);

        goalRoom1.join(follower1);
        goalRoom1.join(follower2);
        goalRoom2.join(follower3);

        // when
        goalRoomService.startGoalRooms();

        // then
        assertAll(
                () -> assertThat(goalRoomPendingMemberRepository.findAllByGoalRoom(goalRoom1)).hasSize(3),
                () -> assertThat(goalRoomMemberRepository.findAllByGoalRoom(goalRoom1)).hasSize(0),
                () -> assertThat(goalRoomPendingMemberRepository.findAllByGoalRoom(goalRoom2)).hasSize(2),
                () -> assertThat(goalRoomMemberRepository.findAllByGoalRoom(goalRoom2)).hasSize(0)
        );
    }

    private Member 사용자를_생성한다(final String 아이디, final String 비밀번호, final String 닉네임,
                             final String 전화번호) {
        final MemberProfile 사용자_프로필 = new MemberProfile(Gender.MALE, LocalDate.of(1990, 1, 1), 전화번호);
        final Member 사용자 = new Member(new Identifier(아이디),
                new EncryptedPassword(new Password(비밀번호)), new Nickname(닉네임), 사용자_프로필);
        return memberRepository.save(사용자);
    }

    private RoadmapCategory 로드맵_카테고리를_저장한다(final String 카테고리_이름) {
        final RoadmapCategory 로드맵_카테고리 = new RoadmapCategory(카테고리_이름);
        return roadmapCategoryRepository.save(로드맵_카테고리);
    }

    private Roadmap 로드맵을_생성한다(final Member 크리에이터, final RoadmapCategory 카테고리) {
        final List<RoadmapNode> 로드맵_노드들 = 로드맵_노드들을_생성한다();
        final RoadmapContent 로드맵_본문 = 로드맵_본문을_생성한다(로드맵_노드들);
        final Roadmap 로드맵 = new Roadmap("로드맵 제목", "로드맵 소개글", 10, RoadmapDifficulty.NORMAL, 크리에이터, 카테고리);
        로드맵.addContent(로드맵_본문);
        return roadmapRepository.save(로드맵);
    }

    private List<RoadmapNode> 로드맵_노드들을_생성한다() {
        final RoadmapNode 노드1 = new RoadmapNode("로드맵 1주차", "로드맵 1주차 내용");
        final RoadmapNode 노드2 = new RoadmapNode("로드맵 2주차", "로드맵 2주차 내용");
        return List.of(노드1, 노드2);
    }

    private RoadmapContent 로드맵_본문을_생성한다(final List<RoadmapNode> 로드맵_노드들) {
        final RoadmapContent 로드맵_본문 = new RoadmapContent("로드맵 본문");
        로드맵_본문.addNodes(new RoadmapNodes(로드맵_노드들));
        return 로드맵_본문;
    }

    private GoalRoom 골룸을_생성한다(final Member member, final RoadmapContent 로드맵_본문, final LocalDate 골룸_시작날짜) {
        final GoalRoom 골룸 = new GoalRoom(new GoalRoomName("골룸"), new LimitedMemberCount(10), 로드맵_본문, member);
        final List<RoadmapNode> 로드맵_노드들 = 로드맵_본문.getNodes().getValues();

        final RoadmapNode 첫번째_노드 = 로드맵_노드들.get(0);
        final GoalRoomRoadmapNode 첫번째_골룸_노드 = new GoalRoomRoadmapNode(
                new Period(골룸_시작날짜, 골룸_시작날짜.plusDays(10)), 10, 첫번째_노드);

        final RoadmapNode 두번째_노드 = 로드맵_노드들.get(1);
        final GoalRoomRoadmapNode 두번째_골룸_노드 = new GoalRoomRoadmapNode(
                new Period(골룸_시작날짜.plusDays(11), 골룸_시작날짜.plusDays(20)), 2, 두번째_노드);

        final GoalRoomRoadmapNodes 골룸_노드들 = new GoalRoomRoadmapNodes(
                List.of(첫번째_골룸_노드, 두번째_골룸_노드));
        골룸.addAllGoalRoomRoadmapNodes(골룸_노드들);

        return goalRoomRepository.save(골룸);
    }

    private GoalRoomPendingMember 골룸_대기자를_생성한다(final GoalRoom 골룸, final Member 골룸_참여자,
                                               final GoalRoomRole 골룸_역할) {
        final GoalRoomPendingMember 골룸_대기_사용자 = new GoalRoomPendingMember(골룸_역할,
                LocalDateTime.of(2023, 7, 19, 12, 0, 0), 골룸, 골룸_참여자);
        return goalRoomPendingMemberRepository.save(골룸_대기_사용자);
    }
}
