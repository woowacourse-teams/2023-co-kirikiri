package co.kirikiri.goalroom.service;

import co.kirikiri.goalroom.domain.GoalRoom;
import co.kirikiri.goalroom.service.dto.response.DashBoardCheckFeedResponse;
import java.util.List;

public interface DashBoardCheckFeedService {

    List<DashBoardCheckFeedResponse> findCheckFeedsByNodeAndGoalRoomStatus(final GoalRoom goalRoom);
}
