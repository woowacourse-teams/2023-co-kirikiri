package co.kirikiri.roadmap.service;

import co.kirikiri.domain.member.Member;
import co.kirikiri.roadmap.domain.Roadmap;

public interface RoadmapGoalRoomService {

    Member findCompletedGoalRoomMember(final Long roadmapId, final String identifier);

    boolean hasGoalRooms(final Roadmap roadmap);
}
