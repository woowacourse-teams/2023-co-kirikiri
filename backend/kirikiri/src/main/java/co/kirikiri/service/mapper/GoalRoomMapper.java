package co.kirikiri.service.mapper;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNodes;
import co.kirikiri.service.dto.goalroom.response.GoalRoomCertifiedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomNodeResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomResponse;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GoalRoomMapper {

    public static GoalRoomResponse convertGoalRoomResponse(final GoalRoom goalRoom) {
        final GoalRoomRoadmapNodes nodes = goalRoom.getGoalRoomRoadmapNodes();
        final List<GoalRoomNodeResponse> roadmapNodeResponses = convertGoalRoomNodeResponses(nodes.getValues());
        final int period = goalRoom.calculateTotalPeriod();
        return new GoalRoomResponse(goalRoom.getName(), roadmapNodeResponses, period);
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

    public static GoalRoomCertifiedResponse convertGoalRoomCertifiedResponse(final GoalRoom goalRoom,
                                                                             final boolean isJoined) {
        final GoalRoomRoadmapNodes nodes = goalRoom.getGoalRoomRoadmapNodes();
        final List<GoalRoomNodeResponse> roadmapNodeResponses = convertGoalRoomNodeResponses(nodes.getValues());
        final int period = goalRoom.calculateTotalPeriod();
        return new GoalRoomCertifiedResponse(goalRoom.getName(), roadmapNodeResponses, period, isJoined);
    }

}
