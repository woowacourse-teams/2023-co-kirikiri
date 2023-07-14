package co.kirikiri.controller;

import co.kirikiri.service.RoadmapService;
import co.kirikiri.service.dto.CustomPageRequest;
import co.kirikiri.service.dto.PageResponse;
import co.kirikiri.service.dto.roadmap.RoadmapFilterType;
import co.kirikiri.service.dto.roadmap.RoadmapResponse;
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
        @RequestParam(value = "filterCond", required = false) final RoadmapFilterType roadmapFilterType,
        @ModelAttribute final CustomPageRequest pageRequest
    ) {
        final PageResponse<RoadmapResponse> roadmapPageResponse = roadmapService.findRoadmapsByFilterType(categoryId,
            roadmapFilterType, pageRequest);
        return ResponseEntity.ok(roadmapPageResponse);
    }
}
