package co.kirikiri.domain.roadmap;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import co.kirikiri.domain.ImageContentType;
import co.kirikiri.domain.roadmap.exception.RoadmapException;
import java.util.List;
import org.junit.jupiter.api.Test;

class RoadmapNodeImagesTest {

    @Test
    void 정상적으로_로드맵_노드_이미지들을_생성한다() {
        //given
        final RoadmapNodeImage roadmapNodeImage1 = new RoadmapNodeImage("originalFIleName1.png", "server/file/path1",
                ImageContentType.PNG);
        final RoadmapNodeImage roadmapNodeImage2 = new RoadmapNodeImage("originalFIleName2.png", "server/file/path2",
                ImageContentType.PNG);

        //when
        //then
        assertDoesNotThrow(() -> new RoadmapNodeImages(List.of(roadmapNodeImage1, roadmapNodeImage2)));
    }

    @Test
    void 로드맵_노드_이미지들을_생성할때_2장_이상이면_예외를_던진다() {
        //given
        final RoadmapNodeImage roadmapNodeImage1 = new RoadmapNodeImage("originalFIleName1.png", "server/file/path1",
                ImageContentType.PNG);
        final RoadmapNodeImage roadmapNodeImage2 = new RoadmapNodeImage("originalFIleName2.png", "server/file/path2",
                ImageContentType.PNG);
        final RoadmapNodeImage roadmapNodeImage3 = new RoadmapNodeImage("originalFIleName3.png", "server/file/path3",
                ImageContentType.PNG);

        //when
        //then
        assertThatThrownBy(
                () -> new RoadmapNodeImages(List.of(roadmapNodeImage1, roadmapNodeImage2, roadmapNodeImage3)))
                .isInstanceOf(RoadmapException.class);
    }

    @Test
    void 로드맵_노드_이미지들을_추가할_때_2장_이상이면_예외를_던진다() {
        //given
        final RoadmapNodeImage roadmapNodeImage1 = new RoadmapNodeImage("originalFIleName1.png", "server/file/path1",
                ImageContentType.PNG);
        final RoadmapNodeImage roadmapNodeImage2 = new RoadmapNodeImage("originalFIleName2.png", "server/file/path2",
                ImageContentType.PNG);
        final RoadmapNodeImage roadmapNodeImage3 = new RoadmapNodeImage("originalFIleName3.png", "server/file/path3",
                ImageContentType.PNG);
        final RoadmapNodeImages roadmapNodeImages = new RoadmapNodeImages(
                List.of(roadmapNodeImage1, roadmapNodeImage2));

        //when
        //then
        assertThatThrownBy(() -> roadmapNodeImages.add(roadmapNodeImage3))
                .isInstanceOf(RoadmapException.class);

    }

    @Test
    void 로드맵_노드_이미지들을_여러장_추가할_때_2장_이상이면_예외를_던진다() {
        //given
        final RoadmapNodeImage roadmapNodeImage1 = new RoadmapNodeImage("originalFIleName1.png", "server/file/path1",
                ImageContentType.PNG);
        final RoadmapNodeImage roadmapNodeImage2 = new RoadmapNodeImage("originalFIleName2.png", "server/file/path2",
                ImageContentType.PNG);
        final RoadmapNodeImage roadmapNodeImage3 = new RoadmapNodeImage("originalFIleName3.png", "server/file/path3",
                ImageContentType.PNG);
        final RoadmapNodeImage roadmapNodeImage4 = new RoadmapNodeImage("originalFIleName4.png", "server/file/path4",
                ImageContentType.PNG);
        final RoadmapNodeImages roadmapNodeImages = new RoadmapNodeImages(
                List.of(roadmapNodeImage1, roadmapNodeImage2));

        //when
        //then
        assertThatThrownBy(
                () -> roadmapNodeImages.addAll(new RoadmapNodeImages(List.of(roadmapNodeImage3, roadmapNodeImage4))))
                .isInstanceOf(RoadmapException.class);

    }
}
