package co.kirikiri.service;

import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.persistence.roadmap.RoadmapCategoryRepository;
import co.kirikiri.persistence.roadmap.RoadmapContentRepository;
import co.kirikiri.persistence.roadmap.RoadmapRepository;
import co.kirikiri.persistence.roadmap.dto.RoadmapFilterType;
import co.kirikiri.service.dto.CustomScrollRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapFilterTypeRequest;
import co.kirikiri.service.dto.roadmap.response.RoadmapCategoryResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapForListResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapResponse;
import co.kirikiri.service.mapper.RoadmapMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RoadmapReadService {

    private final RoadmapRepository roadmapRepository;
    private final RoadmapCategoryRepository roadmapCategoryRepository;
    private final RoadmapContentRepository roadmapContentRepository;

    public RoadmapResponse findRoadmap(final Long id) {
        final Roadmap roadmap = findRoadmapById(id);
        final RoadmapContent recentRoadmapContent = findRecentContent(roadmap);

        return RoadmapMapper.convertToRoadmapResponse(roadmap, recentRoadmapContent);
    }

    private RoadmapContent findRecentContent(final Roadmap roadmap) {
        return roadmapContentRepository.findFirstByRoadmapOrderByCreatedAtDesc(roadmap)
                .orElseThrow(() -> new NotFoundException("로드맵에 컨텐츠가 존재하지 않습니다."));
    }

    private Roadmap findRoadmapById(final Long id) {
        return roadmapRepository.findRoadmapById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 로드맵입니다. roadmapId = " + id));
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
