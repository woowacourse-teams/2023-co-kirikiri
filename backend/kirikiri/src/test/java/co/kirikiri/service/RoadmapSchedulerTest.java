package co.kirikiri.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNodes;
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
import co.kirikiri.domain.roadmap.RoadmapDifficulty;
import co.kirikiri.domain.roadmap.RoadmapNode;
import co.kirikiri.domain.roadmap.RoadmapNodes;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.persistence.roadmap.RoadmapRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RoadmapSchedulerTest {

    private static final LocalDate TODAY = LocalDate.now();
    private static final LocalDate TEN_DAY_LATER = TODAY.plusDays(10);

    @Mock
    private RoadmapRepository roadmapRepository;

    @Mock
    private GoalRoomRepository goalRoomRepository;

    @InjectMocks
    private RoadmapScheduler roadmapScheduler;

    @Test
    void 삭제된_상태의_로드맵_삭제시_골룸이_종료된지_3개월이_지나지_않았다면_삭제되지_않는다() {
        // given
        final Member member1 = new Member(new Identifier("identifier1"),
                new EncryptedPassword(new Password("password1!")), new Nickname("name1"),
                new MemberProfile(Gender.FEMALE, LocalDate.of(1900, 1, 1), "010-1111-1111"));
        final Member member2 = new Member(new Identifier("identifier2"),
                new EncryptedPassword(new Password("password2!")), new Nickname("name2"),
                new MemberProfile(Gender.FEMALE, LocalDate.of(1900, 1, 1), "010-1111-2222"));

        final RoadmapCategory category = new RoadmapCategory("여행");
        final RoadmapContent roadmapContent1 = new RoadmapContent("로드맵 본문1");
        final RoadmapContent roadmapContent2 = new RoadmapContent("로드맵 본문2");
        final RoadmapContent roadmapContent2_1 = new RoadmapContent("로드맵 본문2 - 수정본");
        final RoadmapNode roadmapNode = new RoadmapNode("로드맵 노드", "로드맵 노드 내용");
        roadmapContent1.addNodes(new RoadmapNodes(List.of(roadmapNode)));
        roadmapContent2.addNodes(new RoadmapNodes(List.of(roadmapNode)));
        roadmapContent2_1.addNodes(new RoadmapNodes(List.of(roadmapNode)));
        final Roadmap roadmap1 = new Roadmap("로드맵1", "로드맵 설명1", 30, RoadmapDifficulty.DIFFICULT, member1, category);
        final Roadmap roadmap2 = new Roadmap("로드맵2", "로드맵 설명2", 30, RoadmapDifficulty.DIFFICULT, member1, category);
        roadmap1.addContent(roadmapContent1);
        roadmap2.addContent(roadmapContent2);
        roadmap2.addContent(roadmapContent2_1);

        roadmap1.delete();
        roadmap2.delete();

        final GoalRoom goalRoom1 = new GoalRoom(new GoalRoomName("골룸1"), new LimitedMemberCount(10), roadmapContent1,
                member2);
        final GoalRoom goalRoom2 = new GoalRoom(new GoalRoomName("골룸2"), new LimitedMemberCount(10), roadmapContent2,
                member2);
        final GoalRoom goalRoom2_1 = new GoalRoom(new GoalRoomName("골룸2-1"), new LimitedMemberCount(10),
                roadmapContent2_1,
                member2);
        final GoalRoomRoadmapNodes goalRoomRoadmapNodes = new GoalRoomRoadmapNodes(List.of(
                new GoalRoomRoadmapNode(new Period(TODAY, TEN_DAY_LATER), 5, roadmapNode)));
        goalRoom1.addAllGoalRoomRoadmapNodes(goalRoomRoadmapNodes);
        goalRoom2.addAllGoalRoomRoadmapNodes(goalRoomRoadmapNodes);
        goalRoom2_1.addAllGoalRoomRoadmapNodes(goalRoomRoadmapNodes);
        goalRoom1.complete();
        goalRoom2.complete();

        given(roadmapRepository.findWithRoadmapContentByStatus(any()))
                .willReturn(List.of(roadmap1, roadmap2));
        given(goalRoomRepository.findByRoadmap(roadmap1))
                .willReturn(List.of(goalRoom1));
        given(goalRoomRepository.findByRoadmap(roadmap2))
                .willReturn(List.of(goalRoom2, goalRoom2_1));

        // when
        // then
        assertDoesNotThrow(() -> roadmapScheduler.deleteRoadmaps());
        verify(goalRoomRepository, never()).deleteAll(any());
        verify(roadmapRepository, never()).delete(any());
    }

    @Test
    void 삭제된_상태의_로드맵이_없는_경우_삭제되지_않는다() {
        // given
        final Member member1 = new Member(new Identifier("identifier1"),
                new EncryptedPassword(new Password("password1!")), new Nickname("name1"),
                new MemberProfile(Gender.FEMALE, LocalDate.of(1900, 1, 1), "010-1111-1111"));
        final Member member2 = new Member(new Identifier("identifier2"),
                new EncryptedPassword(new Password("password2!")), new Nickname("name2"),
                new MemberProfile(Gender.FEMALE, LocalDate.of(1900, 1, 1), "010-1111-2222"));

        final RoadmapCategory category = new RoadmapCategory("여행");
        final RoadmapContent roadmapContent1 = new RoadmapContent("로드맵 본문1");
        final RoadmapNode roadmapNode = new RoadmapNode("로드맵 노드", "로드맵 노드 내용");
        final Roadmap roadmap1 = new Roadmap("로드맵1", "로드맵 설명1", 30, RoadmapDifficulty.DIFFICULT, member1, category);
        roadmapContent1.addNodes(new RoadmapNodes(List.of(roadmapNode)));
        roadmap1.addContent(roadmapContent1);

        final GoalRoom goalRoom1 = new GoalRoom(new GoalRoomName("골룸1"), new LimitedMemberCount(10), roadmapContent1,
                member2);

        final GoalRoomRoadmapNodes goalRoomRoadmapNodes = new GoalRoomRoadmapNodes(List.of(
                new GoalRoomRoadmapNode(new Period(TODAY, TEN_DAY_LATER), 5, roadmapNode)));
        goalRoom1.addAllGoalRoomRoadmapNodes(goalRoomRoadmapNodes);
        goalRoom1.complete();

        given(roadmapRepository.findWithRoadmapContentByStatus(any()))
                .willReturn(List.of());

        // when
        // then
        assertDoesNotThrow(() -> roadmapScheduler.deleteRoadmaps());
        verify(goalRoomRepository, never()).findByRoadmap(any());
        verify(roadmapRepository, never()).deleteAll(any());
    }
}
