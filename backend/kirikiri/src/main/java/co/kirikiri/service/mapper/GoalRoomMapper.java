package co.kirikiri.service.mapper;

import co.kirikiri.domain.goalroom.GoalRoomToDo;
import co.kirikiri.domain.goalroom.vo.GoalRoomName;
import co.kirikiri.domain.goalroom.vo.LimitedMemberCount;
import co.kirikiri.service.dto.goalroom.GoalRoomCreateDto;
import co.kirikiri.service.dto.goalroom.GoalRoomRoadmapNodeDto;
import co.kirikiri.service.dto.goalroom.request.GoalRoomCreateRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomRoadmapNodeRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomTodoRequest;

import java.util.List;

public class GoalRoomMapper {

    public static GoalRoomCreateDto convertToGoalRoomCreateDto(final GoalRoomCreateRequest goalRoomCreateRequest) {
        final GoalRoomTodoRequest goalRoomTodoRequest = goalRoomCreateRequest.goalRoomTodo();
        final GoalRoomToDo goalRoomToDo = makeGoalRoomToDo(goalRoomTodoRequest);
        final List<GoalRoomRoadmapNodeRequest> goalRoomRoadmapNodeRequests = goalRoomCreateRequest.goalRoomRoadmapNodePeriods();
        final List<GoalRoomRoadmapNodeDto> goalRoomRoadmapNodeDtos = makeGoalRoomRoadmapNodeDtos(goalRoomRoadmapNodeRequests);
        return new GoalRoomCreateDto(goalRoomCreateRequest.roadmapContentId(), new GoalRoomName(goalRoomCreateRequest.name()),
                new LimitedMemberCount(goalRoomCreateRequest.limitedMemberCount()), goalRoomToDo, goalRoomRoadmapNodeDtos);
    }

    private static GoalRoomToDo makeGoalRoomToDo(final GoalRoomTodoRequest goalRoomTodoRequest) {
        final GoalRoomToDo goalRoomToDo = new GoalRoomToDo(goalRoomTodoRequest.content(),
                goalRoomTodoRequest.startDate(), goalRoomTodoRequest.endDate());
        return goalRoomToDo;
    }

    private static List<GoalRoomRoadmapNodeDto> makeGoalRoomRoadmapNodeDtos(final List<GoalRoomRoadmapNodeRequest> goalRoomRoadmapNodeRequests) {
        return goalRoomRoadmapNodeRequests
                .stream()
                .map(it -> new GoalRoomRoadmapNodeDto(it.roadmapNodeId(), it.checkCount(), it.startDate(), it.endDate()))
                .toList();
    }

}
