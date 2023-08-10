package co.kirikiri.controller;

import co.kirikiri.common.interceptor.Authenticated;
import co.kirikiri.common.resolver.MemberIdentifier;
import co.kirikiri.service.RoadmapCreateService;
import co.kirikiri.service.RoadmapReadService;
import co.kirikiri.service.dto.CustomScrollRequest;
import co.kirikiri.service.dto.roadmap.RoadmapGoalRoomsFilterTypeDto;
import co.kirikiri.service.dto.roadmap.request.RoadmapFilterTypeRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapReviewSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapSearchRequest;
import co.kirikiri.service.dto.roadmap.response.MemberRoadmapResponses;
import co.kirikiri.service.dto.roadmap.response.RoadmapCategoryResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapForListResponses;
import co.kirikiri.service.dto.roadmap.response.RoadmapGoalRoomResponses;
import co.kirikiri.service.dto.roadmap.response.RoadmapResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/roadmaps")
@RequiredArgsConstructor
public class RoadmapController {

    private final RoadmapCreateService roadmapCreateService;
    private final RoadmapReadService roadmapReadService;

    @PostMapping
    @Authenticated
    public ResponseEntity<Void> create(@MemberIdentifier final String identifier,
                                       @RequestBody @Valid final RoadmapSaveRequest request) {
        final Long roadmapId = roadmapCreateService.create(request, identifier);
        return ResponseEntity.created(URI.create("/api/roadmaps/" + roadmapId)).build();
    }

    @PostMapping("/{roadmapId}/reviews")
    public ResponseEntity<Void> createReview(
            @PathVariable("roadmapId") final Long roadmapId,
            @MemberIdentifier final String identifier,
            @RequestBody @Valid final RoadmapReviewSaveRequest request) {
        roadmapCreateService.createReview(roadmapId, identifier, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{roadmapId}")
    public ResponseEntity<RoadmapResponse> findRoadmap(@PathVariable final Long roadmapId) {
        final RoadmapResponse response = roadmapReadService.findRoadmap(roadmapId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<RoadmapForListResponses> findRoadmapsByFilterType(
            @RequestParam(value = "categoryId", required = false) final Long categoryId,
            @RequestParam(value = "filterCond", required = false) final RoadmapFilterTypeRequest filterTypeRequest,
            @ModelAttribute @Valid final CustomScrollRequest scrollRequest
    ) {
        final RoadmapForListResponses roadmapResponses = roadmapReadService.findRoadmapsByFilterType(
                categoryId, filterTypeRequest, scrollRequest);
        return ResponseEntity.ok(roadmapResponses);
    }

    @GetMapping("/search")
    public ResponseEntity<RoadmapForListResponses> search(
            @RequestParam(value = "filterCond", required = false) final RoadmapFilterTypeRequest filterTypeRequest,
            @ModelAttribute final RoadmapSearchRequest searchRequest,
            @ModelAttribute @Valid final CustomScrollRequest scrollRequest
    ) {
        final RoadmapForListResponses roadmapResponses = roadmapReadService.search(
                filterTypeRequest, searchRequest, scrollRequest);
        return ResponseEntity.ok(roadmapResponses);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<RoadmapCategoryResponse>> findAllRoadmapCategories() {
        final List<RoadmapCategoryResponse> roadmapCategoryResponses = roadmapReadService.findAllRoadmapCategories();
        return ResponseEntity.ok(roadmapCategoryResponses);
    }

    @GetMapping("/me")
    @Authenticated
    public ResponseEntity<MemberRoadmapResponses> findAllMyRoadmaps(@MemberIdentifier final String identifier,
                                                                    @ModelAttribute final CustomScrollRequest scrollRequest) {
        final MemberRoadmapResponses responses = roadmapReadService.findAllMemberRoadmaps(identifier, scrollRequest);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{roadmapId}/goal-rooms")
    public ResponseEntity<RoadmapGoalRoomResponses> findGoalRoomsByFilterType(
            @PathVariable final Long roadmapId,
            @RequestParam(value = "filterCond", required = false) final RoadmapGoalRoomsFilterTypeDto roadmapGoalRoomsFilterTypeDto,
            @ModelAttribute final CustomScrollRequest scrollRequest
    ) {
        final RoadmapGoalRoomResponses responses = roadmapReadService.findRoadmapGoalRoomsByFilterType(
                roadmapId, roadmapGoalRoomsFilterTypeDto, scrollRequest);
        return ResponseEntity.ok(responses);
    }
}
