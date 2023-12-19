package co.kirikiri.roadmap.service;

import co.kirikiri.domain.ImageContentType;
import co.kirikiri.roadmap.domain.Roadmap;
import co.kirikiri.roadmap.domain.RoadmapContent;
import co.kirikiri.roadmap.domain.RoadmapNode;
import co.kirikiri.roadmap.domain.RoadmapNodeImage;
import co.kirikiri.roadmap.domain.RoadmapNodeImages;
import co.kirikiri.roadmap.persistence.RoadmapContentRepository;
import co.kirikiri.roadmap.service.dto.RoadmapNodeSaveDto;
import co.kirikiri.roadmap.service.event.RoadmapCreateEvent;
import co.kirikiri.service.FilePathGenerator;
import co.kirikiri.service.FileService;
import co.kirikiri.service.ImageDirType;
import co.kirikiri.service.aop.ExceptionConvert;
import co.kirikiri.service.dto.FileInformation;
import co.kirikiri.service.exception.BadRequestException;
import co.kirikiri.service.exception.ServerException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Service
@RequiredArgsConstructor
@ExceptionConvert
public class RoadmapCreateEventListener {

    private final RoadmapContentRepository roadmapContentRepository;
    private final FileService fileService;
    private final FilePathGenerator filePathGenerator;

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

    private RoadmapNode findRoadmapNodeByTitle(final RoadmapContent lastRoadmapContent,
                                               final RoadmapNodeSaveDto roadmapNodeSaveDto) {
        return lastRoadmapContent.findRoadmapNodeByTitle(roadmapNodeSaveDto.title())
                .orElseThrow(() -> new BadRequestException(
                        "해당 제목을 가지고있는 로드맵 노드가 없습니다. title = " + roadmapNodeSaveDto.title()));
    }

    private RoadmapNodeImages makeRoadmapNodeImages(final RoadmapNodeSaveDto roadmapNodeSaveDto,
                                                    final RoadmapNode roadmapNode) {
        final List<FileInformation> fileInformations = roadmapNodeSaveDto.fileInformations();
        final RoadmapNodeImages roadmapNodeImages = new RoadmapNodeImages();
        for (final FileInformation fileInformation : fileInformations) {
            final RoadmapNodeImage roadmapNodeImage = makeRoadmapNodeImage(fileInformation);
            roadmapNodeImages.add(roadmapNodeImage);
            fileService.save(roadmapNodeImage.getServerFilePath(), fileInformation);
        }
        roadmapNode.addImages(roadmapNodeImages);
        return roadmapNodeImages;
    }

    private RoadmapNodeImage makeRoadmapNodeImage(final FileInformation fileInformation) {
        final String originalFileName = findOriginalFileName(fileInformation);
        final ImageContentType imageContentType = ImageContentType.findImageContentType(fileInformation.contentType());
        final String serverFIlePath = filePathGenerator.makeFilePath(ImageDirType.ROADMAP_NODE, originalFileName);
        return new RoadmapNodeImage(originalFileName, serverFIlePath, imageContentType);
    }

    private String findOriginalFileName(final FileInformation fileInformation) {
        final String originalFilename = fileInformation.originalFileName();
        if (originalFilename == null) {
            throw new BadRequestException("원본 파일의 이름이 존재하지 않습니다.");
        }
        return originalFilename;
    }
}
