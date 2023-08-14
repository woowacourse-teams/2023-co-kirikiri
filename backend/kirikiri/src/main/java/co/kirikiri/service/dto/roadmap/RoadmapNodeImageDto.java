package co.kirikiri.service.dto.roadmap;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public record RoadmapNodeImageDto(
        Long roadmapNodeId,
        List<MultipartFile> images
) {
}
