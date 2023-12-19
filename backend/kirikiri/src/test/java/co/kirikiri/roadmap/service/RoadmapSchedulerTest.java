package co.kirikiri.roadmap.service;

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
import co.kirikiri.roadmap.domain.RoadmapDifficulty;
import co.kirikiri.roadmap.domain.RoadmapNode;
import co.kirikiri.roadmap.domain.RoadmapNodes;
import co.kirikiri.roadmap.persistence.RoadmapRepository;
import co.kirikiri.roadmap.service.scheduler.RoadmapScheduler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RoadmapSchedulerTest {

    private static final LocalDate TODAY = LocalDate.now();
    private static final LocalDate TEN_DAY_LATER = TODAY.plusDays(10);

    @Mock
    private RoadmapRepository roadmapRepository;

    @Mock
    private RoadmapGoalRoomService roadmapGoalRoomService;

    @InjectMocks
    private RoadmapScheduler roadmapScheduler;

    @Test
    void 삭제된_상태의_로드맵_삭제시_종료되지_않은_골룸이_있으면_삭제되지_않는다() {
        // given
        final Member member1 = new Member(new Identifier("identifier1"),
                new EncryptedPassword(new Password("password1!")), new Nickname("name1"), null,
                new MemberProfile(Gender.FEMALE, "kirikiri@email.com"));

        final RoadmapCategory category = new RoadmapCategory("여행");
        final RoadmapContent roadmapContent1_1 = new RoadmapContent("로드맵 본문2");
        final RoadmapContent roadmapContent1_2 = new RoadmapContent("로드맵 본문2 - 수정본");
        final RoadmapNode roadmapNode1 = new RoadmapNode("로드맵1 노드", "로드맵 노드 내용");
        roadmapContent1_1.addNodes(new RoadmapNodes(List.of(roadmapNode1)));
        roadmapContent1_2.addNodes(new RoadmapNodes(List.of(roadmapNode1)));

        final Roadmap roadmap1 = new Roadmap("로드맵2", "로드맵 설명2", 30, RoadmapDifficulty.DIFFICULT, member1, category);
        roadmap1.addContent(roadmapContent1_1);
        roadmap1.addContent(roadmapContent1_2);

        given(roadmapRepository.findWithRoadmapContentByStatus(any()))
                .willReturn(List.of(roadmap1));
        given(roadmapGoalRoomService.canDeleteGoalRoomsInRoadmap(any()))
                .willReturn(false);

        // when
        roadmap1.delete();

        // then
        assertDoesNotThrow(() -> roadmapScheduler.deleteRoadmaps());
        verify(roadmapRepository, never()).delete(any());
    }

    @Test
    void 삭제된_상태의_로드맵_삭제시_골룸이_종료된지_3개월이_지나지_않으면_삭제되지_않는다() {
        // given
        final Member member1 = new Member(new Identifier("identifier1"),
                new EncryptedPassword(new Password("password1!")), new Nickname("name1"), null,
                new MemberProfile(Gender.FEMALE, "kirikiri@email.com"));

        final RoadmapCategory category = new RoadmapCategory("여행");
        final RoadmapContent roadmapContent1 = new RoadmapContent("로드맵 본문1");
        final RoadmapNode roadmapNode1 = new RoadmapNode("로드맵1 노드", "로드맵 노드 내용");
        roadmapContent1.addNodes(new RoadmapNodes(List.of(roadmapNode1)));

        final Roadmap roadmap1 = new Roadmap("로드맵1", "로드맵 설명1", 30, RoadmapDifficulty.DIFFICULT, member1, category);
        roadmap1.addContent(roadmapContent1);

        given(roadmapRepository.findWithRoadmapContentByStatus(any()))
                .willReturn(List.of(roadmap1));
        given(roadmapGoalRoomService.canDeleteGoalRoomsInRoadmap(any()))
                .willReturn(false);

        // when
        roadmap1.delete();

        // then
        assertDoesNotThrow(() -> roadmapScheduler.deleteRoadmaps());
        verify(roadmapRepository, never()).delete(any());
    }

    @Test
    void 삭제된_상태의_로드맵이_없는_경우_삭제되지_않는다() {
        // given
        final Member member1 = new Member(new Identifier("identifier1"),
                new EncryptedPassword(new Password("password1!")), new Nickname("name1"), null,
                new MemberProfile(Gender.FEMALE, "kirikiri@email.com"));

        final RoadmapCategory category = new RoadmapCategory("여행");
        final RoadmapContent roadmapContent1 = new RoadmapContent("로드맵 본문1");
        final RoadmapNode roadmapNode = new RoadmapNode("로드맵 노드", "로드맵 노드 내용");
        roadmapContent1.addNodes(new RoadmapNodes(List.of(roadmapNode)));

        final Roadmap roadmap1 = new Roadmap("로드맵1", "로드맵 설명1", 30, RoadmapDifficulty.DIFFICULT, member1, category);
        roadmap1.addContent(roadmapContent1);

        given(roadmapRepository.findWithRoadmapContentByStatus(any()))
                .willReturn(Collections.emptyList());

        // when
        // then
        assertDoesNotThrow(() -> roadmapScheduler.deleteRoadmaps());
        verify(roadmapRepository, never()).deleteAll(any());
    }
}
