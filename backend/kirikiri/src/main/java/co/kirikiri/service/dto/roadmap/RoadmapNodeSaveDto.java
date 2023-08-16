package co.kirikiri.service.dto.roadmap;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public record RoadmapNodeSaveDto(
        String title,
        String content,
        List<MultipartFile> multipartFiles
) {

}
