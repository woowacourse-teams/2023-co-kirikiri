package co.kirikiri.service;

import co.kirikiri.domain.ImageContentType;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.domain.roadmap.RoadmapNode;
import co.kirikiri.domain.roadmap.RoadmapNodeImage;
import co.kirikiri.domain.roadmap.RoadmapNodeImages;
import co.kirikiri.exception.BadRequestException;
import co.kirikiri.exception.ServerException;
import co.kirikiri.persistence.roadmap.RoadmapContentRepository;
import co.kirikiri.service.dto.roadmap.RoadmapNodeSaveDto;
import co.kirikiri.service.event.RoadmapCreateEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoadmapCreateEventListener {

    private final RoadmapContentRepository roadmapContentRepository;
    private final FileService fileService;

    @Async
    @TransactionalEventListener
    @Transactional
    public void handleRoadmapCreate(final RoadmapCreateEvent roadmapCreateEvent) {
        saveRoadmapNodeImage(roadmapCreateEvent);
    }

    private void saveRoadmapNodeImage(final RoadmapCreateEvent roadmapCreateEvent) {
        final RoadmapContent lastRoadmapContent = findLastRoadmapContent(roadmapCreateEvent.roadmap());
        for (final RoadmapNodeSaveDto roadmapNodeSaveDto : roadmapCreateEvent.roadmapSaveDto().roadmapNodes()) {
            final RoadmapNode roadmapNode = findRoadmapNodeByTitle(lastRoadmapContent, roadmapNodeSaveDto);
            final RoadmapNodeImages roadmapNodeImages = makeRoadmapNodeImages(roadmapNodeSaveDto, roadmapNode);
            roadmapNode.addImages(roadmapNodeImages);
        }

        roadmapContentRepository.save(lastRoadmapContent);
    }

    private RoadmapContent findLastRoadmapContent(final Roadmap roadmap) {
        return roadmap.findLastRoadmapContent()
                .orElseThrow(() -> new ServerException("로드맵 컨텐츠가 존재하지 않습니다."));
    }

    private RoadmapNode findRoadmapNodeByTitle(final RoadmapContent lastRoadmapContent, final RoadmapNodeSaveDto roadmapNodeSaveDto) {
        return lastRoadmapContent.findRoadmapNodeByTitle(roadmapNodeSaveDto.title())
                .orElseThrow(() -> new BadRequestException("해당 제목을 가지고있는 로드맵 노드가 없습니다. title = " + roadmapNodeSaveDto.title()));
    }

    private RoadmapNodeImages makeRoadmapNodeImages(final RoadmapNodeSaveDto roadmapNodeSaveDto, final RoadmapNode roadmapNode) {
        final List<MultipartFile> images = roadmapNodeSaveDto.multipartFiles();
        final RoadmapNodeImages roadmapNodeImages = new RoadmapNodeImages();
        for (final MultipartFile image : images) {
            final RoadmapNodeImage roadmapNodeImage = saveRoadmapNodeImage(roadmapNode, image);
            roadmapNodeImages.add(roadmapNodeImage);
        }
        return roadmapNodeImages;
    }

    private RoadmapNodeImage saveRoadmapNodeImage(final RoadmapNode roadmapNode, final MultipartFile image) {
        final String path = fileService.uploadFileAndReturnPath(image, ImageDirType.ROADMAP_NODE, roadmapNode.getId());
        final String originalFilename = findOriginalFilename(image);
        final ImageContentType imageContentType = ImageContentType.findByOriginalFileName(originalFilename);
        return new RoadmapNodeImage(findOriginalFilename(image), path, imageContentType);
    }

    private String findOriginalFilename(final MultipartFile image) {
        final String originalFilename = image.getOriginalFilename();
        if (originalFilename == null) {
            throw new BadRequestException("원본 파일 이름이 존재하지 않습니다.");
        }
        return originalFilename;
    }
}
