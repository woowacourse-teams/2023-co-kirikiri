package co.kirikiri.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import co.kirikiri.common.exception.BadRequestException;
import co.kirikiri.common.exception.ServerException;
import co.kirikiri.common.service.FilePathGenerator;
import co.kirikiri.common.service.FileService;
import co.kirikiri.common.service.dto.FileInformation;
import co.kirikiri.domain.member.EncryptedPassword;
import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.domain.roadmap.RoadmapDifficulty;
import co.kirikiri.domain.roadmap.RoadmapNode;
import co.kirikiri.domain.roadmap.RoadmapNodes;
import co.kirikiri.persistence.roadmap.RoadmapContentRepository;
import co.kirikiri.service.dto.roadmap.RoadmapNodeSaveDto;
import co.kirikiri.service.dto.roadmap.RoadmapSaveDto;
import co.kirikiri.service.dto.roadmap.RoadmapTagSaveDto;
import co.kirikiri.service.dto.roadmap.request.RoadmapDifficultyType;
import co.kirikiri.service.event.RoadmapCreateEvent;
import co.kirikiri.service.roadmap.RoadmapCreateEventListener;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class RoadmapCreateEventListenerTest {

    private static final Member member = new Member(1L, new Identifier("identifier1"),
            null, new EncryptedPassword(new Password("password1!")), new Nickname("닉네임"), null,
            new MemberProfile(Gender.FEMALE, "kirikiri@email.com"));

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
                RoadmapDifficulty.DIFFICULT, member, new RoadmapCategory("category"));

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
                RoadmapDifficulty.DIFFICULT, member, new RoadmapCategory("category"));

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
                RoadmapDifficulty.DIFFICULT, member, new RoadmapCategory("category"));

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
                RoadmapDifficulty.DIFFICULT, member, new RoadmapCategory("category"));

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
