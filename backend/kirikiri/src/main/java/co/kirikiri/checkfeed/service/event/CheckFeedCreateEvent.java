package co.kirikiri.checkfeed.service.event;

public record CheckFeedCreateEvent(
        Long checkFeedId,
        Long goalRoomId
) {

}
