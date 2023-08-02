package co.kirikiri.controller;

import co.kirikiri.common.resolver.MemberIdentifier;
import co.kirikiri.service.RoadmapService;
import co.kirikiri.service.dto.CustomScrollRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapFilterTypeRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapSearchRequest;
import co.kirikiri.service.dto.roadmap.response.RoadmapCategoryResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapForListResponse;
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

    @GetMapping("/{roadmapId}")
    public ResponseEntity<RoadmapResponse> findRoadmap(@PathVariable final Long roadmapId) {
        final RoadmapResponse response = roadmapService.findRoadmap(roadmapId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<RoadmapForListResponse>> findRoadmapsByFilterType(
            @RequestParam(value = "categoryId", required = false) final Long categoryId,
            @RequestParam(value = "filterCond", required = false) final RoadmapFilterTypeRequest filterTypeRequest,
            @ModelAttribute @Valid final CustomScrollRequest scrollRequest
    ) {
        final List<RoadmapForListResponse> roadmapResponses = roadmapService.findRoadmapsByFilterType(
                categoryId, filterTypeRequest, scrollRequest);
        return ResponseEntity.ok(roadmapResponses);
    }

    @GetMapping("/search")
    public ResponseEntity<List<RoadmapForListResponse>> search(
            @RequestParam(value = "filterCond", required = false) final RoadmapFilterTypeRequest filterTypeRequest,
            @ModelAttribute final RoadmapSearchRequest searchRequest,
            @ModelAttribute @Valid final CustomScrollRequest scrollRequest
    ) {
        final List<RoadmapForListResponse> roadmapResponses = roadmapService.search(
                filterTypeRequest, searchRequest, scrollRequest);
        return ResponseEntity.ok(roadmapResponses);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<RoadmapCategoryResponse>> findAllRoadmapCategories() {
        final List<RoadmapCategoryResponse> roadmapCategoryResponses = roadmapService.findAllRoadmapCategories();
        return ResponseEntity.ok(roadmapCategoryResponses);
    }
}
