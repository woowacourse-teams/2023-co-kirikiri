package co.kirikiri.service.dto.roadmap.request;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RoadmapNodeSaveRequest {

    @NotBlank(message = "로드맵 노드의 제목을 입력해주세요.")
    private String title;

    @NotBlank(message = "로드맵 노드의 설명을 입력해주세요.")
    private String content;

    private List<MultipartFile> images;

    public void setImages(final List<MultipartFile> images) {
        this.images = images;
    }
}
