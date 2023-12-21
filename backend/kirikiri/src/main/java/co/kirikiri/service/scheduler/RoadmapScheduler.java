package co.kirikiri.service.scheduler;

import co.kirikiri.common.aop.ExceptionConvert;
import co.kirikiri.common.exception.NotFoundException;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.domain.roadmap.RoadmapStatus;
import co.kirikiri.goalroom.domain.GoalRoom;
import co.kirikiri.goalroom.persistence.GoalRoomRepository;
import co.kirikiri.persistence.roadmap.RoadmapContentRepository;
import co.kirikiri.persistence.roadmap.RoadmapRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
@ExceptionConvert
public class RoadmapScheduler {

    private static final int DELETE_AFTER_MONTH = 3;

    private final RoadmapRepository roadmapRepository;
    private final RoadmapContentRepository roadmapContentRepository;
    private final GoalRoomRepository goalRoomRepository;

    @Scheduled(cron = "0 0 4 * * *")
    public void deleteRoadmaps() {
        final RoadmapStatus status = RoadmapStatus.DELETED;
        final List<Roadmap> deletedStatusRoadmaps = roadmapRepository.findWithRoadmapContentByStatus(status);
        for (final Roadmap roadmap : deletedStatusRoadmaps) {
            final RoadmapContent roadmapContent = findRecentContent(roadmap);
            delete(roadmap, roadmapContent);
        }
    }

    private RoadmapContent findRecentContent(final Roadmap roadmap) {
        return roadmapContentRepository.findFirstByRoadmapOrderByCreatedAtDesc(roadmap)
                .orElseThrow(() -> new NotFoundException("로드맵에 컨텐츠가 존재하지 않습니다."));
    }

    private void delete(final Roadmap roadmap, final RoadmapContent roadmapContent) {
        final List<GoalRoom> goalRooms = goalRoomRepository.findByRoadmapContentId(roadmapContent.getId());
        final boolean canDelete = canDeleteRoadmapBasedOnGoalRooms(goalRooms);
        if (canDelete) {
            deleteGoalRooms(goalRooms);
            deleteRoadmap(roadmap);
        }
    }

    private boolean canDeleteRoadmapBasedOnGoalRooms(final List<GoalRoom> goalRooms) {
        return goalRooms.stream()
                .allMatch(goalRoom -> goalRoom.isCompleted() && goalRoom.isCompletedAfterMonths(DELETE_AFTER_MONTH));
    }

    private void deleteGoalRooms(final List<GoalRoom> goalRooms) {
        goalRoomRepository.deleteAll(goalRooms);
    }

    private void deleteRoadmap(final Roadmap roadmap) {
        roadmapRepository.delete(roadmap);
    }
}
