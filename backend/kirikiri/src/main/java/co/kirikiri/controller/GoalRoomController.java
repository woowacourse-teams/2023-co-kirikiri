package co.kirikiri.controller;

import co.kirikiri.service.GoalRoomService;
import co.kirikiri.service.dto.CustomPageRequest;
import co.kirikiri.service.dto.PageResponse;
import co.kirikiri.service.dto.goalroom.GoalRoomFilterTypeDto;
import co.kirikiri.service.dto.goalroom.GoalRoomForListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/goal-rooms")
@RequiredArgsConstructor
public class GoalRoomController {

    private final GoalRoomService goalRoomService;

    @GetMapping
    public ResponseEntity<PageResponse<GoalRoomForListResponse>> findGoalRoomsByFilterType(
            @RequestParam(value = "filterCond", required = false) final GoalRoomFilterTypeDto goalRoomFilterTypeDto,
            @ModelAttribute final CustomPageRequest pageRequest
    ) {
        final PageResponse<GoalRoomForListResponse> goalRoomsPageResponse = goalRoomService.findGoalRoomsByFilterType(
                goalRoomFilterTypeDto, pageRequest);
        return ResponseEntity.ok(goalRoomsPageResponse);
    }
}
