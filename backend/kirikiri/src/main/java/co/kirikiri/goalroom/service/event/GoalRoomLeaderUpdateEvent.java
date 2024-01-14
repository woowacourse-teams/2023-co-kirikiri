package co.kirikiri.goalroom.service.event;

public record GoalRoomLeaderUpdateEvent(
        Long goalRoomId,
        String leaderIdentifier
) {

}
