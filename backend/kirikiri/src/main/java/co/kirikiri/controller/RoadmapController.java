package co.kirikiri.controller;

import co.kirikiri.common.resolver.MemberIdentifier;
import co.kirikiri.service.RoadmapService;
import co.kirikiri.service.dto.CustomPageRequest;
import co.kirikiri.service.dto.PageResponse;
import co.kirikiri.service.dto.roadmap.RoadmapCategoryResponse;
import co.kirikiri.service.dto.roadmap.RoadmapFilterTypeDto;
import co.kirikiri.service.dto.roadmap.RoadmapResponse;
import co.kirikiri.service.dto.roadmap.RoadmapSaveRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/roadmaps")
@RequiredArgsConstructor
public class RoadmapController {

    private final RoadmapService roadmapService;

    // TODO: Member 직접 받지 않도록 수정
    @PostMapping
    public ResponseEntity<Void> create(@MemberIdentifier final String identifier,
                                       @RequestBody @Valid final RoadmapSaveRequest request) {
        final Long id = roadmapService.create(request, identifier);

        return ResponseEntity.created(URI.create("/roadmaps/" + id)).build();
    }

    @GetMapping
    public ResponseEntity<PageResponse<RoadmapResponse>> findRoadmapsByFilterType(
            @RequestParam(value = "categoryId", required = false) final Long categoryId,
            @RequestParam(value = "roadmapFilterTypeDto", required = false) final RoadmapFilterTypeDto roadmapFilterTypeDto,
            @ModelAttribute final CustomPageRequest pageRequest
    ) {
        final PageResponse<RoadmapResponse> roadmapPageResponse = roadmapService.findRoadmapsByFilterType(
                categoryId,
                roadmapFilterTypeDto, pageRequest);
        return ResponseEntity.ok(roadmapPageResponse);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<RoadmapCategoryResponse>> getAllRoadmapCategories() {
        final List<RoadmapCategoryResponse> roadmapCategoryResponses = roadmapService.getAllRoadmapCategories();
        return ResponseEntity.ok(roadmapCategoryResponses);
    }
}
