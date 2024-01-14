package co.kirikiri.roadmap.service;

import co.kirikiri.common.dto.CustomScrollRequest;
import co.kirikiri.domain.member.Member;
import co.kirikiri.roadmap.domain.Roadmap;
import co.kirikiri.roadmap.service.dto.RoadmapGoalRoomNumberDto;
import co.kirikiri.roadmap.service.dto.RoadmapGoalRoomsOrderTypeDto;
import co.kirikiri.roadmap.service.dto.response.RoadmapGoalRoomResponses;

public interface RoadmapGoalRoomService {

    Member findCompletedGoalRoomMember(final Long roadmapId, final String identifier);

    boolean hasGoalRooms(final Roadmap roadmap);

    RoadmapGoalRoomNumberDto findRoadmapGoalRoomsByRoadmap(final Roadmap roadmap);

    RoadmapGoalRoomResponses makeRoadmapGoalRoomResponsesByOrderType(final Roadmap roadmap, final RoadmapGoalRoomsOrderTypeDto orderTypeDto, final CustomScrollRequest scrollRequest);

    boolean canDeleteGoalRoomsInRoadmap(final Roadmap roadmap);
}