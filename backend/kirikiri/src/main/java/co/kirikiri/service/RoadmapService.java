package co.kirikiri.service;

import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.domain.roadmap.dto.RoadmapFilterType;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.persistence.roadmap.RoadmapCategoryRepository;
import co.kirikiri.persistence.roadmap.RoadmapRepository;
import co.kirikiri.service.dto.CustomPageRequest;
import co.kirikiri.service.dto.PageResponse;
import co.kirikiri.service.dto.roadmap.RoadmapCategoryResponse;
import co.kirikiri.service.dto.roadmap.RoadmapFilterTypeDto;
import co.kirikiri.service.dto.roadmap.RoadmapResponse;
import co.kirikiri.service.mapper.RoadmapMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RoadmapService {

    private final RoadmapRepository roadmapRepository;
    private final RoadmapCategoryRepository roadmapCategoryRepository;

    public PageResponse<RoadmapResponse> findRoadmapsByFilterType(final Long categoryId,
                                                                  final RoadmapFilterTypeDto filterType,
                                                                  final CustomPageRequest pageRequest) {
        final RoadmapCategory category = findCategoryById(categoryId);
        final RoadmapFilterType orderType = RoadmapMapper.convertRoadmapOrderType(filterType);

        final PageRequest generatedPageRequest = PageRequest.of(pageRequest.page(), pageRequest.size());
        final Page<Roadmap> roadmapPages = roadmapRepository.findRoadmapPagesByCond(category, orderType,
            generatedPageRequest);
        return RoadmapMapper.convertRoadmapPageResponse(roadmapPages, pageRequest);
    }

    private RoadmapCategory findCategoryById(final Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return roadmapCategoryRepository.findById(categoryId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 카테고리입니다. categoryId = " + categoryId));
    }

    public List<RoadmapCategoryResponse> getAllRoadmapCategories() {
        final List<RoadmapCategory> roadmapCategories = roadmapCategoryRepository.findAll();
        return RoadmapMapper.convertRoadmapCategoryResponses(roadmapCategories);
    }
}