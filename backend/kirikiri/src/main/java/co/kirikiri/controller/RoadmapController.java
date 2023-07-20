package co.kirikiri.controller;

import co.kirikiri.common.resolver.MemberIdentifier;
import co.kirikiri.service.RoadmapService;
import co.kirikiri.service.dto.CustomPageRequest;
import co.kirikiri.service.dto.PageResponse;
import co.kirikiri.service.dto.roadmap.RoadmapCategoryResponse;
import co.kirikiri.service.dto.roadmap.RoadmapFilterTypeRequest;
import co.kirikiri.service.dto.roadmap.RoadmapResponse;
import co.kirikiri.service.dto.roadmap.RoadmapReviewSaveRequest;
import co.kirikiri.service.dto.roadmap.RoadmapSaveRequest;
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

    private final RoadmapService roadmapService;

    @PostMapping
    public ResponseEntity<Void> create(@MemberIdentifier final String identifier,
                                       @RequestBody @Valid final RoadmapSaveRequest request) {
        final Long id = roadmapService.create(request, identifier);

        return ResponseEntity.created(URI.create("/api/roadmaps/" + id)).build();
    }

    @PostMapping("/reviews/{roadmapId}")
    public ResponseEntity<Void> createReview(
            @PathVariable("roadmapId") final Long roadmapId,
            @MemberIdentifier final String identifier,
            @RequestBody @Valid final RoadmapReviewSaveRequest request) {
        roadmapService.createReview(roadmapId, identifier, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<PageResponse<RoadmapResponse>> findRoadmapsByFilterType(
            @RequestParam(value = "categoryId", required = false) final Long categoryId,
            @RequestParam(value = "filterCond", required = false) final RoadmapFilterTypeRequest request,
            @ModelAttribute final CustomPageRequest pageRequest
    ) {
        final PageResponse<RoadmapResponse> roadmapPageResponse = roadmapService.findRoadmapsByFilterType(
                categoryId, request, pageRequest);
        return ResponseEntity.ok(roadmapPageResponse);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<RoadmapCategoryResponse>> getAllRoadmapCategories() {
        final List<RoadmapCategoryResponse> roadmapCategoryResponses = roadmapService.findAllRoadmapCategories();
        return ResponseEntity.ok(roadmapCategoryResponses);
    }

    @GetMapping("/{roadmapId}")
    public ResponseEntity<RoadmapResponse> getRoadmap(@PathVariable final Long roadmapId) {
        final RoadmapResponse response = roadmapService.findRoadmap(roadmapId);
        return ResponseEntity.ok(response);
    }
}
