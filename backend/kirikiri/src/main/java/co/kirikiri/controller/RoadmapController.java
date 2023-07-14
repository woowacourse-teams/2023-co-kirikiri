package co.kirikiri.controller;

import co.kirikiri.domain.member.Member;
import co.kirikiri.service.RoadmapService;
import co.kirikiri.service.dto.roadmap.RoadmapSaveRequest;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/roadmaps")
public class RoadmapController {

    private final RoadmapService roadmapService;

    public RoadmapController(final RoadmapService roadmapService) {
        this.roadmapService = roadmapService;
    }

    @PostMapping
    public ResponseEntity<Void> create(@AuthPrincipal final Member member,
                                       @RequestBody @Valid final RoadmapSaveRequest request) {
        final Long id = roadmapService.create(request, member);

        return ResponseEntity.created(URI.create("/roadmaps/" + id)).build();
    }
}
