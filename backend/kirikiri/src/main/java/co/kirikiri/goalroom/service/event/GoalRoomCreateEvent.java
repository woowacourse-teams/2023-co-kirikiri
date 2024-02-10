package co.kirikiri.goalroom.service.event;

public record GoalRoomCreateEvent(
        Long goalRoomId,
        String leaderIdentifier
) {

}
