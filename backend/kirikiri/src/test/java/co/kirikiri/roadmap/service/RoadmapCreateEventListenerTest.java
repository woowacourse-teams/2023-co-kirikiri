package co.kirikiri.roadmap.service;

import co.kirikiri.roadmap.domain.Roadmap;
import co.kirikiri.roadmap.domain.RoadmapCategory;
import co.kirikiri.roadmap.domain.RoadmapContent;
import co.kirikiri.roadmap.domain.RoadmapDifficulty;
import co.kirikiri.roadmap.domain.RoadmapNode;
import co.kirikiri.roadmap.domain.RoadmapNodes;
import co.kirikiri.roadmap.persistence.RoadmapContentRepository;
import co.kirikiri.roadmap.service.dto.RoadmapNodeSaveDto;
import co.kirikiri.roadmap.service.dto.RoadmapSaveDto;
import co.kirikiri.roadmap.service.dto.RoadmapTagSaveDto;
import co.kirikiri.roadmap.service.dto.request.RoadmapDifficultyType;
import co.kirikiri.roadmap.service.event.RoadmapCreateEvent;
import co.kirikiri.service.FilePathGenerator;
import co.kirikiri.service.FileService;
import co.kirikiri.service.dto.FileInformation;
import co.kirikiri.service.exception.BadRequestException;
import co.kirikiri.service.exception.ServerException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RoadmapCreateEventListenerTest {

    private final Long memberId = 1L;

    @Mock
    private RoadmapContentRepository roadmapContentRepository;

    @Mock
    private FilePathGenerator pathGenerator;

    @Mock
    private FileService fileService;

    @InjectMocks
    private RoadmapCreateEventListener roadmapCreateEventListener;

    @Test
    void 정상적으로_로드맵_노드_이미지를_저장한다() throws IOException {
        final RoadmapContent roadmapContent = new RoadmapContent("roadmapContent");
        final RoadmapNode roadmapNode = new RoadmapNode("roadmapNodeTitle", "roadmapNodeContent");
        roadmapContent.addNodes(new RoadmapNodes(List.of(roadmapNode)));

        final Roadmap roadmap = new Roadmap("roadmapTitle", "inroduction", 10,
                RoadmapDifficulty.DIFFICULT, memberId, new RoadmapCategory("category"));

        final MultipartFile imageFile = new MockMultipartFile(roadmapNode.getTitle(),
                "originalFileName.jpeg", "image/jpeg", "tempImage".getBytes());
        final FileInformation fileInformation = new FileInformation(imageFile.getOriginalFilename(),
                imageFile.getSize(), imageFile.getContentType(), imageFile.getInputStream());
        final RoadmapNodeSaveDto roadmapNodeSaveDto = new RoadmapNodeSaveDto(roadmapNode.getTitle(),
                roadmapNode.getContent(), List.of(fileInformation));
        final RoadmapSaveDto roadmapSaveDto = new RoadmapSaveDto(1L, roadmap.getTitle(), roadmap.getIntroduction(),
                roadmapContent.getContent(), RoadmapDifficultyType.DIFFICULT, 10, List.of(roadmapNodeSaveDto),
                List.of(new RoadmapTagSaveDto("tag")));

        roadmap.addContent(roadmapContent);

        final RoadmapCreateEvent roadmapCreateEvent = new RoadmapCreateEvent(roadmap, roadmapSaveDto);

        // When
        roadmapCreateEventListener.handleRoadmapCreate(roadmapCreateEvent);

        // Then
        verify(roadmapContentRepository, times(1)).save(roadmapContent);
    }

    @Test
    void 로드맵에_컨텐츠가_존재하지_않을_경우_예외를_던진다() throws IOException {
        //given
        final Roadmap roadmap = new Roadmap("roadmapTitle", "inroduction", 10,
                RoadmapDifficulty.DIFFICULT, memberId, new RoadmapCategory("category"));

        final MultipartFile imageFile = new MockMultipartFile("roadmapNodeTitle",
                "originalFileName.jpeg", "image/jpeg", "tempImage".getBytes());
        final FileInformation fileInformation = new FileInformation(imageFile.getOriginalFilename(),
                imageFile.getSize(), imageFile.getContentType(), imageFile.getInputStream());
        final RoadmapNodeSaveDto roadmapNodeSaveDto = new RoadmapNodeSaveDto("roadmapNodeTitle", "roadmapNodeContent",
                List.of(fileInformation));
        final RoadmapSaveDto roadmapSaveDto = new RoadmapSaveDto(1L, roadmap.getTitle(), roadmap.getIntroduction(),
                "roadmapNodeContent", RoadmapDifficultyType.DIFFICULT, 10, List.of(roadmapNodeSaveDto),
                List.of(new RoadmapTagSaveDto("tag")));

        final RoadmapCreateEvent roadmapCreateEvent = new RoadmapCreateEvent(roadmap, roadmapSaveDto);

        //when
        //then
        assertThatThrownBy(() -> roadmapCreateEventListener.handleRoadmapCreate(roadmapCreateEvent))
                .isInstanceOf(ServerException.class);
    }

    @Test
    void 로드맵_노드_제목을_가진_노드가_로드맵에_존재하지_않을때_예외를_던진다() throws IOException {
        //given
        final RoadmapContent roadmapContent = new RoadmapContent("roadmapContent");
        final RoadmapNode roadmapNode = new RoadmapNode("roadmapNodeTitle", "roadmapNodeContent");
        roadmapContent.addNodes(new RoadmapNodes(List.of(roadmapNode)));

        final Roadmap roadmap = new Roadmap("roadmapTitle", "inroduction", 10,
                RoadmapDifficulty.DIFFICULT, memberId, new RoadmapCategory("category"));

        final MultipartFile imageFile = new MockMultipartFile(roadmapNode.getTitle(),
                "originalFileName.jpeg", "image/jpeg", "tempImage".getBytes());
        final FileInformation fileInformation = new FileInformation(imageFile.getOriginalFilename(),
                imageFile.getSize(), imageFile.getContentType(), imageFile.getInputStream());
        final RoadmapNodeSaveDto roadmapNodeSaveDto = new RoadmapNodeSaveDto("Wrong Title", roadmapNode.getContent(),
                List.of(fileInformation));
        final RoadmapSaveDto roadmapSaveDto = new RoadmapSaveDto(1L, roadmap.getTitle(), roadmap.getIntroduction(),
                roadmapContent.getContent(), RoadmapDifficultyType.DIFFICULT, 10, List.of(roadmapNodeSaveDto),
                List.of(new RoadmapTagSaveDto("tag")));

        roadmap.addContent(roadmapContent);

        final RoadmapCreateEvent roadmapCreateEvent = new RoadmapCreateEvent(roadmap, roadmapSaveDto);

        //when
        //then
        assertThatThrownBy(() -> roadmapCreateEventListener.handleRoadmapCreate(roadmapCreateEvent))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void 로드맵_노드_이미지에_원본_파일_이름이_없을_경우_예외를_던진다() throws IOException {
        //given
        final RoadmapContent roadmapContent = new RoadmapContent("roadmapContent");
        final RoadmapNode roadmapNode = new RoadmapNode("roadmapNodeTitle", "roadmapNodeContent");
        roadmapContent.addNodes(new RoadmapNodes(List.of(roadmapNode)));

        final Roadmap roadmap = new Roadmap("roadmapTitle", "inroduction", 10,
                RoadmapDifficulty.DIFFICULT, memberId, new RoadmapCategory("category"));

        final MultipartFile imageFile = new MockMultipartFile(roadmapNode.getTitle(), null,
                "image/jpeg", "tempImage".getBytes());
        final FileInformation fileInformation = new FileInformation(imageFile.getOriginalFilename(),
                imageFile.getSize(), imageFile.getContentType(), imageFile.getInputStream());
        final RoadmapNodeSaveDto roadmapNodeSaveDto = new RoadmapNodeSaveDto("Wrong Title", roadmapNode.getContent(),
                List.of(fileInformation));
        final RoadmapSaveDto roadmapSaveDto = new RoadmapSaveDto(1L, roadmap.getTitle(), roadmap.getIntroduction(),
                roadmapContent.getContent(), RoadmapDifficultyType.DIFFICULT, 10, List.of(roadmapNodeSaveDto),
                List.of(new RoadmapTagSaveDto("tag")));

        roadmap.addContent(roadmapContent);

        final RoadmapCreateEvent roadmapCreateEvent = new RoadmapCreateEvent(roadmap, roadmapSaveDto);

        //when
        //then
        assertThatThrownBy(() -> roadmapCreateEventListener.handleRoadmapCreate(roadmapCreateEvent))
                .isInstanceOf(BadRequestException.class);
    }
}
