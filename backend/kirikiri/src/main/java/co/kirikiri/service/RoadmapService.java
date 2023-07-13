package co.kirikiri.service;

import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.domain.roadmap.dto.RoadmapOrderType;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.persistence.RoadmapCategoryRepository;
import co.kirikiri.persistence.RoadmapRepository;
import co.kirikiri.service.dto.PageResponse;
import co.kirikiri.service.dto.roadmap.RoadmapFilterType;
import co.kirikiri.service.dto.roadmap.RoadmapResponse;
import co.kirikiri.service.mapper.RoadmapMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RoadmapService {

    private static final int PAGE_OFFSET = 1;

    private final RoadmapRepository roadmapRepository;
    private final RoadmapCategoryRepository roadmapCategoryRepository;

    public PageResponse<RoadmapResponse> getRoadmapsByFilterType(final Long categoryId,
                                                                 final RoadmapFilterType filterType,
                                                                 final Pageable pageRequest) {
        final PageRequest pageRequestAppliedOffset = generatePageRequestAppliedOffset(pageRequest);
        final RoadmapCategory category = getCategoryById(categoryId);
        final RoadmapOrderType orderType = RoadmapMapper.convertRoadmapOrderType(filterType);

        final Page<Roadmap> roadmapPages = roadmapRepository.getRoadmapPagesByCond(category, orderType,
            pageRequestAppliedOffset);
        return RoadmapMapper.convertRoadmapPageResponse(roadmapPages, pageRequest);
    }

    private PageRequest generatePageRequestAppliedOffset(final Pageable pageRequest) {
        final int pageNumber = pageRequest.getPageNumber() - PAGE_OFFSET;
        final int pageSize = pageRequest.getPageSize();
        return PageRequest.of(pageNumber, pageSize);
    }

    private RoadmapCategory getCategoryById(final Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return roadmapCategoryRepository.findById(categoryId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 카테고리입니다. categoryId = " + categoryId));
    }
}
