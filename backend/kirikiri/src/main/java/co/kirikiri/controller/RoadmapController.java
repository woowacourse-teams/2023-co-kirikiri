package co.kirikiri.controller;

import co.kirikiri.service.RoadmapService;
import co.kirikiri.service.dto.CustomPageRequest;
import co.kirikiri.service.dto.PageResponse;
import co.kirikiri.service.dto.roadmap.RoadmapCategoryResponse;
import co.kirikiri.service.dto.roadmap.RoadmapFilterTypeDto;
import co.kirikiri.service.dto.roadmap.RoadmapResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/roadmaps")
@RequiredArgsConstructor
public class RoadmapController {

    private final RoadmapService roadmapService;

    @GetMapping
    public ResponseEntity<PageResponse<RoadmapResponse>> findRoadmapsByFilterType(
        @RequestParam(value = "categoryId", required = false) final Long categoryId,
        @RequestParam(value = "roadmapFilterTypeDto", required = false) final RoadmapFilterTypeDto roadmapFilterTypeDto,
        @ModelAttribute final CustomPageRequest pageRequest
    ) {
        final PageResponse<RoadmapResponse> roadmapPageResponse = roadmapService.findRoadmapsByFilterType(categoryId,
            roadmapFilterTypeDto, pageRequest);
        return ResponseEntity.ok(roadmapPageResponse);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<RoadmapCategoryResponse>> getAllRoadmapCategories() {
        final List<RoadmapCategoryResponse> roadmapCategoryResponses = roadmapService.getAllRoadmapCategories();
        return ResponseEntity.ok(roadmapCategoryResponses);
    }
}
