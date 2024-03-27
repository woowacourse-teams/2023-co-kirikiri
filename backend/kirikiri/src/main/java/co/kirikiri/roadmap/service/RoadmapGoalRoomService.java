package co.kirikiri.roadmap.service;

import co.kirikiri.member.domain.Member;
import co.kirikiri.roadmap.domain.Roadmap;
import co.kirikiri.roadmap.service.dto.RoadmapGoalRoomNumberDto;
import co.kirikiri.roadmap.service.dto.RoadmapGoalRoomsOrderTypeDto;
import co.kirikiri.roadmap.service.dto.response.RoadmapGoalRoomResponses;
import co.kirikiri.service.dto.CustomScrollRequest;

public interface RoadmapGoalRoomService {

    Member findCompletedGoalRoomMember(final Long roadmapId, final String identifier);

    boolean hasGoalRooms(final Long roadmapId);

    RoadmapGoalRoomNumberDto findRoadmapGoalRoomsByRoadmap(final Roadmap roadmap);

    RoadmapGoalRoomResponses makeRoadmapGoalRoomResponsesByOrderType(final Long roadmapId, final RoadmapGoalRoomsOrderTypeDto orderTypeDto, final CustomScrollRequest scrollRequest);

    boolean canDeleteGoalRoomsInRoadmap(final Long roadmapId);
}
