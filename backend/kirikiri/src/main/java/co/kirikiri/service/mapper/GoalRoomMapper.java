package co.kirikiri.service.mapper;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNodes;
import co.kirikiri.service.dto.goalroom.GoalRoomNodeResponse;
import co.kirikiri.service.dto.goalroom.GoalRoomResponse;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GoalRoomMapper {

    private static final int DATE_OFFSET = 1;

    public static GoalRoomResponse convertGoalRoomResponse(final GoalRoom goalRoom, final Boolean isJoined) {
        final GoalRoomRoadmapNodes nodes = goalRoom.getGoalRoomRoadmapNodes();
        final List<GoalRoomNodeResponse> roadmapNodeResponses = convertGoalRoomNodeResponses(nodes.getValues());
        final int period = calculateGoalRoomTotalPeriod(goalRoom);
        return new GoalRoomResponse(goalRoom.getName(), roadmapNodeResponses, period, isJoined);
    }

    private static List<GoalRoomNodeResponse> convertGoalRoomNodeResponses(
            final List<GoalRoomRoadmapNode> roadmapNodes) {
        return roadmapNodes.stream()
                .map(GoalRoomMapper::convertGoalRoomNodeResponse)
                .toList();
    }

    private static GoalRoomNodeResponse convertGoalRoomNodeResponse(final GoalRoomRoadmapNode node) {
        return new GoalRoomNodeResponse(node.getRoadmapNode().getTitle(), node.getStartDate(), node.getEndDate(),
                node.getCheckCount());
    }

    private static int calculateGoalRoomTotalPeriod(final GoalRoom goalRoom) {
        final List<GoalRoomRoadmapNode> nodes = goalRoom.getGoalRoomRoadmapNodes().getValues();
        return nodes.stream()
                .mapToInt(GoalRoomMapper::calculatePeriod)
                .sum();
    }

    private static int calculatePeriod(final GoalRoomRoadmapNode node) {
        return (int) ChronoUnit.DAYS.between(node.getStartDate(), node.getEndDate()) + DATE_OFFSET;
    }
}
