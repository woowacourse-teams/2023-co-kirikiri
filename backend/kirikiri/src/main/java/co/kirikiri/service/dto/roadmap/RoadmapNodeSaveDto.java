package co.kirikiri.service.dto.roadmap;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public record RoadmapNodeSaveDto(
        String title,
        String content,
        List<MultipartFile> multipartFiles
) {

}
