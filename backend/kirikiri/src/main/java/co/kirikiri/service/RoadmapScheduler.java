package co.kirikiri.service;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.persistence.dto.RoadmapStatusType;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.persistence.roadmap.RoadmapRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
public class RoadmapScheduler {

    private final RoadmapRepository roadmapRepository;
    private final GoalRoomRepository goalRoomRepository;

    @Scheduled(cron = "0 0 4 * * *")
    public void deleteRoadmaps() {
        final RoadmapStatusType status = RoadmapStatusType.DELETED;
        final List<Roadmap> deletedStatusRoadmaps = roadmapRepository.findWithRoadmapContentByStatus(status);
        for (final Roadmap roadmap : deletedStatusRoadmaps) {
            deleteRoadmapAndGoalRoomsIfCan(roadmap);
        }
    }

    private void deleteRoadmapAndGoalRoomsIfCan(final Roadmap roadmap) {
        final List<GoalRoom> goalRooms = goalRoomRepository.findByRoadmap(roadmap);
        final boolean canDelete = goalRooms.stream()
                .allMatch(goalRoom -> goalRoom.isCompleted() && goalRoom.isCompletedAfterMonths(3));
        if (canDelete) {
            goalRoomRepository.deleteAll(goalRooms);
            roadmapRepository.delete(roadmap);
        }
    }
}
