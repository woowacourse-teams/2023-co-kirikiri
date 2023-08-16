package co.kirikiri.service.dto.roadmap;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public record RoadmapNodeImageDto(
        Long roadmapNodeId,
        List<MultipartFile> images
) {

}
