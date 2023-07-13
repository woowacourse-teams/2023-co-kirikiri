package co.kirikiri.controller;

import co.kirikiri.service.RoadmapService;
import co.kirikiri.service.dto.PageResponse;
import co.kirikiri.service.dto.roadmap.RoadmapFilterType;
import co.kirikiri.service.dto.roadmap.RoadmapResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
        @RequestParam(value = "filterCond", required = false) final RoadmapFilterType roadmapFilterType,
        @PageableDefault(page = 1) final Pageable pageable) {
        final PageResponse<RoadmapResponse> roadmapPageResponse = roadmapService.findRoadmapsByFilterType(categoryId,
            roadmapFilterType, pageable);
        return ResponseEntity.ok(roadmapPageResponse);
    }
}
