package co.kirikiri.controller;

import co.kirikiri.common.resolver.MemberIdentifier;
import co.kirikiri.service.RoadmapService;
import co.kirikiri.service.dto.CustomPageRequest;
import co.kirikiri.service.dto.PageResponse;
import co.kirikiri.service.dto.roadmap.request.RoadmapFilterTypeRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapSaveRequest;
import co.kirikiri.service.dto.roadmap.response.RoadmapCategoryResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
        final Long roadmapId = roadmapService.create(request, identifier);
        return ResponseEntity.created(URI.create("/api/roadmaps/" + roadmapId)).build();
    }

    @GetMapping
    public ResponseEntity<PageResponse<RoadmapResponse>> findRoadmapsByFilterType(
            @RequestParam(value = "categoryId", required = false) final Long categoryId,
            @RequestParam(value = "filterCond", required = false) final RoadmapFilterTypeRequest roadmapFilterTypeRequest,
            @ModelAttribute final CustomPageRequest pageRequest
    ) {
        final PageResponse<RoadmapResponse> roadmapPageResponse = roadmapService.findRoadmapsByFilterType(
                categoryId,
                roadmapFilterTypeRequest, pageRequest);
        return ResponseEntity.ok(roadmapPageResponse);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<RoadmapCategoryResponse>> findAllRoadmapCategories() {
        final List<RoadmapCategoryResponse> roadmapCategoryResponses = roadmapService.findAllRoadmapCategories();
        return ResponseEntity.ok(roadmapCategoryResponses);
    }

    @GetMapping("/{roadmapId}")
    public ResponseEntity<RoadmapResponse> getRoadmap(@PathVariable final Long roadmapId) {
        final RoadmapResponse response = roadmapService.findRoadmap(roadmapId);
        return ResponseEntity.ok(response);
    }
}
