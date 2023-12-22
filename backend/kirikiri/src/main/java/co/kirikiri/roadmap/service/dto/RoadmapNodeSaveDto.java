package co.kirikiri.roadmap.service.dto;

import co.kirikiri.service.dto.FileInformation;
import java.util.List;

public record RoadmapNodeSaveDto(
        String title,
        String content,
        List<FileInformation> fileInformations
) {

}
