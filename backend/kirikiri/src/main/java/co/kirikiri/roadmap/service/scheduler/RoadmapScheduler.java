package co.kirikiri.roadmap.service.scheduler;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.roadmap.domain.Roadmap;
import co.kirikiri.roadmap.domain.RoadmapStatus;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.roadmap.persistence.RoadmapRepository;
import co.kirikiri.service.aop.ExceptionConvert;
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
    private final GoalRoomRepository goalRoomRepository;

    @Scheduled(cron = "0 0 4 * * *")
    public void deleteRoadmaps() {
        final RoadmapStatus status = RoadmapStatus.DELETED;
        final List<Roadmap> deletedStatusRoadmaps = roadmapRepository.findWithRoadmapContentByStatus(status);
        for (final Roadmap roadmap : deletedStatusRoadmaps) {
            delete(roadmap);
        }
    }

    private void delete(final Roadmap roadmap) {
        final List<GoalRoom> goalRooms = goalRoomRepository.findByRoadmap(roadmap);
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
