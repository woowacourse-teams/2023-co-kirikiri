package co.kirikiri.roadmap.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import co.kirikiri.member.domain.EncryptedPassword;
import co.kirikiri.member.domain.Gender;
import co.kirikiri.member.domain.Member;
import co.kirikiri.member.domain.MemberProfile;
import co.kirikiri.member.domain.vo.Identifier;
import co.kirikiri.member.domain.vo.Nickname;
import co.kirikiri.member.domain.vo.Password;
import co.kirikiri.roadmap.domain.Roadmap;
import co.kirikiri.roadmap.domain.RoadmapCategory;
import co.kirikiri.roadmap.domain.RoadmapDifficulty;
import co.kirikiri.roadmap.domain.RoadmapStatus;
import co.kirikiri.roadmap.domain.RoadmapTags;
import co.kirikiri.roadmap.persistence.RoadmapRepository;
import co.kirikiri.roadmap.service.scheduler.RoadmapScheduler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        final Member member = new Member(new Identifier("identifier1"),
                new EncryptedPassword(new Password("password1!")), new Nickname("name1"), null,
                new MemberProfile(Gender.FEMALE, "kirikiri@email.com"));

        final RoadmapCategory category = new RoadmapCategory("여행");
        final Roadmap roadmap = new Roadmap(1L, "로드맵2", "로드맵 설명2", 30, RoadmapDifficulty.DIFFICULT,
                RoadmapStatus.CREATED, member.getId(), category, new RoadmapTags(new ArrayList<>()));

        given(roadmapRepository.findByStatus(any()))
                .willReturn(List.of(roadmap));
        given(roadmapGoalRoomService.canDeleteGoalRoomsInRoadmap(anyLong()))
                .willReturn(false);

        // when
        roadmap.delete();

        // then
        assertDoesNotThrow(() -> roadmapScheduler.deleteRoadmaps());
        verify(roadmapRepository, never()).delete(any());
    }

    @Test
    void 삭제된_상태의_로드맵_삭제시_골룸이_종료된지_3개월이_지나지_않으면_삭제되지_않는다() {
        // given
        final Member member = new Member(new Identifier("identifier1"),
                new EncryptedPassword(new Password("password1!")), new Nickname("name1"), null,
                new MemberProfile(Gender.FEMALE, "kirikiri@email.com"));

        final RoadmapCategory category = new RoadmapCategory("여행");
        final Roadmap roadmap = new Roadmap("로드맵1", "로드맵 설명1", 30, RoadmapDifficulty.DIFFICULT, member.getId(), category, new RoadmapTags(new ArrayList<>()));

        given(roadmapRepository.findByStatus(any()))
                .willReturn(List.of(roadmap));
        given(roadmapGoalRoomService.canDeleteGoalRoomsInRoadmap(any()))
                .willReturn(false);

        // when
        roadmap.delete();

        // then
        assertDoesNotThrow(() -> roadmapScheduler.deleteRoadmaps());
        verify(roadmapRepository, never()).delete(any());
    }

    @Test
    void 삭제된_상태의_로드맵이_없는_경우_삭제되지_않는다() {
        // given
        given(roadmapRepository.findByStatus(any()))
                .willReturn(Collections.emptyList());

        // when
        // then
        assertDoesNotThrow(() -> roadmapScheduler.deleteRoadmaps());
        verify(roadmapRepository, never()).deleteAll(any());
    }
}
