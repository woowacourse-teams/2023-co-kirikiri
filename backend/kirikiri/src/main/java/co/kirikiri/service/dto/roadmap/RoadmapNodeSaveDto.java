package co.kirikiri.service.dto.roadmap;

import co.kirikiri.common.service.dto.FileInformation;
import java.util.List;

public record RoadmapNodeSaveDto(
        String title,
        String content,
        List<FileInformation> fileInformations
) {

}
