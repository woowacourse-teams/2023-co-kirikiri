package co.kirikiri.goalroom.service;

import co.kirikiri.goalroom.domain.GoalRoom;
import co.kirikiri.goalroom.service.dto.response.DashBoardToDoResponse;
import java.util.List;

public interface DashBoardToDoService {

    List<DashBoardToDoResponse> findMemberCheckedGoalRoomToDoIds(final GoalRoom goalRoom, final Long memberId);
}
