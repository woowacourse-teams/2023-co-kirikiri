package co.kirikiri.service;

import static co.kirikiri.domain.goalroom.GoalRoomStatus.RECRUITING;
import static co.kirikiri.domain.goalroom.GoalRoomStatus.RUNNING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.kirikiri.domain.ImageContentType;
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
import co.kirikiri.roadmap.domain.Roadmap;
import co.kirikiri.roadmap.domain.RoadmapCategory;
import co.kirikiri.roadmap.domain.RoadmapContent;
import co.kirikiri.roadmap.domain.RoadmapContents;
import co.kirikiri.roadmap.domain.RoadmapDifficulty;
import co.kirikiri.roadmap.domain.RoadmapNode;
import co.kirikiri.roadmap.domain.RoadmapNodeImage;
import co.kirikiri.roadmap.domain.RoadmapNodeImages;
import co.kirikiri.roadmap.domain.RoadmapNodes;
import co.kirikiri.persistence.goalroom.GoalRoomMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomPendingMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.service.scheduler.GoalRoomScheduler;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GoalRoomSchedulerTest {

    private static final LocalDate TODAY = LocalDate.now();
    private static final LocalDate TEN_DAY_LATER = TODAY.plusDays(10);
    private static final LocalDate TWENTY_DAY_LATER = TODAY.plusDays(20);

    @Mock
    private GoalRoomRepository goalRoomRepository;

    @Mock
    private GoalRoomPendingMemberRepository goalRoomPendingMemberRepository;
    
    @Mock
    private GoalRoomMemberRepository goalRoomMemberRepository;

    @InjectMocks
    private GoalRoomScheduler goalRoomScheduler;

    @Test
    void 골룸의_시작날짜가_되면_골룸의_상태가_진행중으로_변경된다() {
        // given
        final Member creator = 사용자를_생성한다(1L, "cokirikiri", "password1!", "코끼리", "010-1234-5678");
        final Roadmap roadmap = 로드맵을_생성한다(creator);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom1 = 골룸을_생성한다(1L, creator, targetRoadmapContent, 10);
        final GoalRoom goalRoom2 = 골룸을_생성한다(2L, creator, targetRoadmapContent, 10);

        final Member follower1 = 사용자를_생성한다(2L, "identifier1", "password2!", "name1", "kirikiri@email.com");
        final Member follower2 = 사용자를_생성한다(3L, "identifier2", "password3!", "name2", "kirikiri@email.com");
        final Member follower3 = 사용자를_생성한다(4L, "identifier3", "password4!", "name3", "kirikiri@email.com");

        골룸_대기자를_생성한다(goalRoom2, creator, GoalRoomRole.FOLLOWER);
        골룸_대기자를_생성한다(goalRoom1, follower1, GoalRoomRole.FOLLOWER);
        골룸_대기자를_생성한다(goalRoom1, follower2, GoalRoomRole.FOLLOWER);

        goalRoom1.join(follower1);
        goalRoom1.join(follower2);
        goalRoom2.join(follower3);

        when(goalRoomRepository.findAllRecruitingGoalRoomsByStartDateEarlierThan(LocalDate.now()))
                .thenReturn(List.of(goalRoom1));

        // when
        goalRoomScheduler.startGoalRooms();

        // then
        assertAll(
                () -> assertThat(goalRoom1.getStatus()).isEqualTo(RUNNING),
                () -> assertThat(goalRoom2.getStatus()).isEqualTo(RECRUITING)
        );
    }

    @Test
    void 골룸의_시작날짜가_아직_지나지_않았다면_골룸의_상태가_변경되지_않는다() {
        // given
        final Member creator = 사용자를_생성한다(1L, "cokirikiri", "password1!", "코끼리", "kirikiri@email.com");
        final Roadmap roadmap = 로드맵을_생성한다(creator);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom1 = 골룸을_생성한다(1L, creator, targetRoadmapContent, 10);
        final GoalRoom goalRoom2 = 골룸을_생성한다(2L, creator, targetRoadmapContent, 10);

        final Member follower1 = 사용자를_생성한다(2L, "identifier1", "password2!", "name1", "kirikiri@email.com");
        final Member follower2 = 사용자를_생성한다(3L, "identifier2", "password3!", "name2", "kirikiri@email.com");
        final Member follower3 = 사용자를_생성한다(4L, "identifier3", "password4!", "name3", "kirikiri@email.com");

        goalRoom1.join(follower1);
        goalRoom1.join(follower2);
        goalRoom2.join(follower3);

        when(goalRoomRepository.findAllRecruitingGoalRoomsByStartDateEarlierThan(LocalDate.now()))
                .thenReturn(Collections.emptyList());

        // when
        goalRoomScheduler.startGoalRooms();

        // then
        verify(goalRoomPendingMemberRepository, never()).deleteAllByIdIn(anyList());

        assertAll(
                () -> assertThat(goalRoom1.getStatus()).isEqualTo(RECRUITING),
                () -> assertThat(goalRoom2.getStatus()).isEqualTo(RECRUITING)
        );
    }

    private Member 사용자를_생성한다(final Long memberId, final String identifier, final String password, final String nickname,
                             final String email) {
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, email);

        return new Member(memberId, new Identifier(identifier), null, new EncryptedPassword(new Password(password)),
                new Nickname(nickname), null, memberProfile);
    }

    private Roadmap 로드맵을_생성한다(final Member creator) {
        final RoadmapCategory category = new RoadmapCategory("게임");
        final List<RoadmapNode> roadmapNodes = 로드맵_노드들을_생성한다();
        final RoadmapContent roadmapContent = 로드맵_본문을_생성한다(roadmapNodes);
        final Roadmap roadmap = new Roadmap("로드맵 제목", "로드맵 소개글", 10, RoadmapDifficulty.NORMAL, creator, category);
        roadmap.addContent(roadmapContent);
        return roadmap;
    }

    private List<RoadmapNode> 로드맵_노드들을_생성한다() {
        final RoadmapNode roadmapNode1 = new RoadmapNode("로드맵 1주차", "로드맵 1주차 내용");
        roadmapNode1.addImages(new RoadmapNodeImages(노드_이미지들을_생성한다()));
        final RoadmapNode roadmapNode2 = new RoadmapNode("로드맵 2주차", "로드맵 2주차 내용");
        return List.of(roadmapNode1, roadmapNode2);
    }

    private RoadmapContent 로드맵_본문을_생성한다(final List<RoadmapNode> roadmapNodes) {
        final RoadmapContent roadmapContent = new RoadmapContent("로드맵 본문");
        roadmapContent.addNodes(new RoadmapNodes(roadmapNodes));
        return roadmapContent;
    }

    private List<RoadmapNodeImage> 노드_이미지들을_생성한다() {
        return List.of(
                new RoadmapNodeImage("node-image1.png", "node-image1-save-path", ImageContentType.PNG),
                new RoadmapNodeImage("node-image2.png", "node-image2-save-path", ImageContentType.PNG)
        );
    }

    private GoalRoom 골룸을_생성한다(final Long goalRoomId, final Member creator, final RoadmapContent roadmapContent,
                              final Integer limitedMemberCount) {
        final GoalRoom goalRoom = new GoalRoom(goalRoomId, new GoalRoomName("골룸 이름"),
                new LimitedMemberCount(limitedMemberCount), roadmapContent, creator);
        goalRoom.addAllGoalRoomRoadmapNodes(골룸_로드맵_노드들을_생성한다(roadmapContent.getNodes()));
        return goalRoom;
    }

    private GoalRoomRoadmapNodes 골룸_로드맵_노드들을_생성한다(final RoadmapNodes roadmapNodes) {
        return new GoalRoomRoadmapNodes(List.of(
                new GoalRoomRoadmapNode(new Period(TODAY, TEN_DAY_LATER), 5, roadmapNodes.getValues().get(0)),
                new GoalRoomRoadmapNode(new Period(TEN_DAY_LATER.plusDays(1), TWENTY_DAY_LATER), 5,
                        roadmapNodes.getValues().get(1)))
        );
    }

    private GoalRoomPendingMember 골룸_대기자를_생성한다(final GoalRoom goalRoom, final Member follower,
                                               final GoalRoomRole role) {
        return new GoalRoomPendingMember(role, LocalDateTime.of(2023, 7, 19, 12, 0, 0), goalRoom, follower);
    }
}
