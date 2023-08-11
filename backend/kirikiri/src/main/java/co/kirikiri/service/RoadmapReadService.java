package co.kirikiri.service;

import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.domain.roadmap.RoadmapNode;
import co.kirikiri.domain.roadmap.RoadmapNodes;
import co.kirikiri.domain.roadmap.RoadmapTags;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.persistence.roadmap.RoadmapCategoryRepository;
import co.kirikiri.persistence.roadmap.RoadmapContentRepository;
import co.kirikiri.persistence.roadmap.RoadmapRepository;
import co.kirikiri.persistence.roadmap.dto.RoadmapFilterType;
import co.kirikiri.service.dto.CustomScrollRequest;
import co.kirikiri.service.dto.member.MemberDto;
import co.kirikiri.service.dto.roadmap.RoadmapCategoryDto;
import co.kirikiri.service.dto.roadmap.RoadmapContentDto;
import co.kirikiri.service.dto.roadmap.RoadmapDto;
import co.kirikiri.service.dto.roadmap.RoadmapNodeDto;
import co.kirikiri.service.dto.roadmap.RoadmapTagDto;
import co.kirikiri.service.dto.roadmap.request.RoadmapFilterTypeRequest;
import co.kirikiri.service.dto.roadmap.response.RoadmapCategoryResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapForListResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapResponse;
import co.kirikiri.service.mapper.RoadmapMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RoadmapReadService {

    private final RoadmapRepository roadmapRepository;
    private final RoadmapCategoryRepository roadmapCategoryRepository;
    private final RoadmapContentRepository roadmapContentRepository;
    private final FileService fileService;

    public RoadmapResponse findRoadmap(final Long id) {
        final Roadmap roadmap = findRoadmapById(id);
        final RoadmapContent recentRoadmapContent = findRecentContent(roadmap);

        final RoadmapDto roadmapDto = makeRoadmapDto(roadmap, recentRoadmapContent);
        return RoadmapMapper.convertToRoadmapResponse(roadmapDto);
    }

    private Roadmap findRoadmapById(final Long id) {
        return roadmapRepository.findRoadmapById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 로드맵입니다. roadmapId = " + id));
    }

    private RoadmapContent findRecentContent(final Roadmap roadmap) {
        return roadmapContentRepository.findFirstByRoadmapOrderByCreatedAtDesc(roadmap)
                .orElseThrow(() -> new NotFoundException("로드맵에 컨텐츠가 존재하지 않습니다."));
    }

    private RoadmapDto makeRoadmapDto(final Roadmap roadmap, final RoadmapContent roadmapContent) {
        final RoadmapCategory category = roadmap.getCategory();
        final Member creator = roadmap.getCreator();
        final RoadmapContentDto roadmapContentDto = new RoadmapContentDto(
                roadmapContent.getId(),
                roadmapContent.getContent(),
                makeRoadmapNodeDtos(roadmapContent.getNodes()));
        return new RoadmapDto(roadmap.getId(), new RoadmapCategoryDto(category.getId(), category.getName()),
                roadmap.getTitle(), roadmap.getIntroduction(), new MemberDto(creator.getId(), creator.getNickname().getValue()),
                roadmapContentDto, roadmap.getDifficulty().name(), roadmap.getRequiredPeriod(), makeRoadmapTagDtos(roadmap.getTags()));
    }

    private List<RoadmapNodeDto> makeRoadmapNodeDtos(final RoadmapNodes nodes) {
        return nodes.getValues()
                .stream()
                .map(this::makeRoadmapNodeDto)
                .toList();
    }

    private RoadmapNodeDto makeRoadmapNodeDto(final RoadmapNode roadmapNode) {
        final List<String> imageUrls = roadmapNode.getRoadmapNodeImages()
                .getValues()
                .stream()
                .map(it -> fileService.generateUrl(it.getServerFilePath(), HttpMethod.GET).getPath())
                .toList();
        return new RoadmapNodeDto(roadmapNode.getId(), roadmapNode.getTitle(), roadmapNode.getContent(), imageUrls);
    }

    private List<RoadmapTagDto> makeRoadmapTagDtos(final RoadmapTags roadmapTags) {
        return roadmapTags.getValues()
                .stream()
                .map(it -> new RoadmapTagDto(it.getId(), it.getName().getValue()))
                .toList();
    }

    public List<RoadmapForListResponse> findRoadmapsByFilterType(final Long categoryId,
                                                                 final RoadmapFilterTypeRequest filterType,
                                                                 final CustomScrollRequest scrollRequest) {
        final RoadmapCategory category = findCategoryById(categoryId);
        final RoadmapFilterType orderType = RoadmapMapper.convertRoadmapOrderType(filterType);

        final List<Roadmap> roadmapPages = roadmapRepository.findRoadmapsByCond(category, orderType,
                scrollRequest.lastId(), scrollRequest.size());
        return RoadmapMapper.convertRoadmapPageResponse(roadmapPages);
    }

    private RoadmapCategory findCategoryById(final Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return roadmapCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 카테고리입니다. categoryId = " + categoryId));
    }

    public List<RoadmapCategoryResponse> findAllRoadmapCategories() {
        final List<RoadmapCategory> roadmapCategories = roadmapCategoryRepository.findAll();
        return RoadmapMapper.convertRoadmapCategoryResponses(roadmapCategories);
    }
}
