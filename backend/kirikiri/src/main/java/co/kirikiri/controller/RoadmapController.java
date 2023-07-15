package co.kirikiri.controller;

import co.kirikiri.service.RoadmapService;
import co.kirikiri.service.dto.roadmap.SingleRoadmapResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/roadmaps")
@RequiredArgsConstructor
public class RoadmapController {

    private final RoadmapService roadmapService;

    @GetMapping("/{roadmapId}")
    public ResponseEntity<SingleRoadmapResponse> getRoadmap(@PathVariable final Long roadmapId) {
        final SingleRoadmapResponse response = roadmapService.findSingleRoadmap(roadmapId);
        return ResponseEntity.ok(response);
    }
}
