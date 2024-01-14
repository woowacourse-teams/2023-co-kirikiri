package co.kirikiri.roadmap.service.scheduler;

import co.kirikiri.roadmap.domain.Roadmap;
import co.kirikiri.roadmap.domain.RoadmapStatus;
import co.kirikiri.roadmap.persistence.RoadmapRepository;
import co.kirikiri.roadmap.service.RoadmapGoalRoomService;
import co.kirikiri.service.aop.ExceptionConvert;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional
@RequiredArgsConstructor
@ExceptionConvert
public class RoadmapScheduler {

    private final RoadmapRepository roadmapRepository;
    private final RoadmapGoalRoomService roadmapGoalRoomService;

    @Scheduled(cron = "0 0 4 * * *")
    public void deleteRoadmaps() {
        final RoadmapStatus status = RoadmapStatus.DELETED;
        final List<Roadmap> deletedStatusRoadmaps = roadmapRepository.findWithRoadmapContentByStatus(status);
        for (final Roadmap roadmap : deletedStatusRoadmaps) {
            delete(roadmap);
        }
    }

    private void delete(final Roadmap roadmap) {
        final boolean canDelete = roadmapGoalRoomService.canDeleteGoalRoomsInRoadmap(roadmap);
        // TODO : GoalRoom 내부의 Roadmap 직접 의존 제거 시 로드맵에 포함된 GoalRoom 따로 제거해주기 (이벤트 활용)
        if (canDelete) {
            roadmapRepository.delete(roadmap);
        }
    }
}