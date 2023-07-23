package co.kirikiri.service.mapper;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNodes;
import co.kirikiri.domain.goalroom.GoalRoomToDo;
import co.kirikiri.domain.member.Member;
import co.kirikiri.service.dto.CustomPageRequest;
import co.kirikiri.service.dto.PageResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomForListResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomNodeResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomTodoResponse;
import co.kirikiri.service.dto.member.response.MemberResponse;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GoalRoomMapper {

    private static final int DATE_OFFSET = 1;

    public static GoalRoomResponse convertGoalRoomResponse(final GoalRoom goalRoom, final String roadmapTitle,
                                                           final Boolean isJoined) {
        final GoalRoomRoadmapNodes nodes = goalRoom.getGoalRoomRoadmapNodes();
        final List<GoalRoomNodeResponse> roadmapNodeResponses = convertGoalRoomNodeResponses(nodes.getValues());
        final List<GoalRoomTodoResponse> roadmapTodoResponses = convertGoalRoomTodoResponse(goalRoom.getTodos());
        final int period = calculateGoalRoomTotalPeriod(goalRoom);
        return new GoalRoomResponse(goalRoom.getName(), roadmapTitle, goalRoom.getStatus().name(),
                roadmapNodeResponses, roadmapTodoResponses, period, isJoined);
    }

    private static List<GoalRoomNodeResponse> convertGoalRoomNodeResponses(
            final List<GoalRoomRoadmapNode> roadmapNodes) {
        return roadmapNodes.stream()
                .map(GoalRoomMapper::convertGoalRoomNodeResponse)
                .toList();
    }

    private static GoalRoomNodeResponse convertGoalRoomNodeResponse(final GoalRoomRoadmapNode node) {
        return new GoalRoomNodeResponse(node.getRoadmapNode().getTitle(), node.getStartDate(), node.getEndDate(),
                node.getCheckCount());
    }

    private static List<GoalRoomTodoResponse> convertGoalRoomTodoResponse(
            final List<GoalRoomToDo> roadmapTodos) {
        return roadmapTodos.stream()
                .map(GoalRoomMapper::convertGoalRoomTodoResponse)
                .toList();
    }

    private static GoalRoomTodoResponse convertGoalRoomTodoResponse(final GoalRoomToDo todo) {
        return new GoalRoomTodoResponse(todo.getContent(), todo.getStartDate(), todo.getEndDate());
    }

    private static int calculateGoalRoomTotalPeriod(final GoalRoom goalRoom) {
        final LocalDate startDate = goalRoom.getGoalRoomStartDate();
        final LocalDate endDate = goalRoom.getGoalRoomEndDate();

        return (int) ChronoUnit.DAYS.between(startDate, endDate) + DATE_OFFSET;
    }

    public static PageResponse<GoalRoomForListResponse> convertGoalRoomsPageResponse(
            final Page<GoalRoom> goalRoomsPage, final CustomPageRequest pageRequest) {
        final int currentPage = pageRequest.getOriginPage();
        final int totalPages = goalRoomsPage.getTotalPages();
        final List<GoalRoomForListResponse> goalRoomForListResponses = goalRoomsPage.getContent().stream()
                .map(GoalRoomMapper::convertToGoalRoomForListResponse)
                .toList();
        return new PageResponse<>(currentPage, totalPages, goalRoomForListResponses);
    }

    private static GoalRoomForListResponse convertToGoalRoomForListResponse(final GoalRoom goalRoom) {
        return new GoalRoomForListResponse(goalRoom.getId(), goalRoom.getName(),
                goalRoom.getCurrentMemberCount(),
                goalRoom.getLimitedMemberCount(), goalRoom.getCreatedAt(), goalRoom.getGoalRoomStartDate(),
                goalRoom.getGoalRoomEndDate(), convertToMemberResponse(goalRoom), goalRoom.getStatus().name());
    }

    private static MemberResponse convertToMemberResponse(final GoalRoom goalRoom) {
        final Member goalRoomLeader = goalRoom.findLeader().getMember();
        return new MemberResponse(goalRoomLeader.getId(), goalRoomLeader.getNickname().getValue());
    }
}
