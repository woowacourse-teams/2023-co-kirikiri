package co.kirikiri.checkfeed.service.event;

public record AccomplishmentRateUpdateEvent(
        Long goalRoomId,
        Long goalRoomMemberId,
        int pastCheckCount
) {

}
