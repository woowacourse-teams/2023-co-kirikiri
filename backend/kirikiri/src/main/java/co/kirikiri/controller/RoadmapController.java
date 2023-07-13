package co.kirikiri.controller;

import co.kirikiri.service.RoadmapService;
import co.kirikiri.service.dto.RoadmapCategoryResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/roadmaps")
@RequiredArgsConstructor
public class RoadmapController {

    private final RoadmapService roadmapService;

    @GetMapping("/categories")
    public ResponseEntity<List<RoadmapCategoryResponse>> getAllRoadmapCategories() {
        final List<RoadmapCategoryResponse> roadmapCategoryResponses = roadmapService.getAllRoadmapCategories();
        return ResponseEntity.ok(roadmapCategoryResponses);
    }
}
