package co.kirikiri.service;

import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.persistence.roadmap.RoadmapRepository;
import co.kirikiri.service.dto.roadmap.SingleRoadmapResponse;
import co.kirikiri.service.mapper.RoadmapMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RoadmapService {

    private final RoadmapRepository roadmapRepository;

    public SingleRoadmapResponse findSingleRoadmap(final Long roadmapId) {
        final Roadmap roadmap = roadmapRepository.findById(roadmapId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 로드맵입니다. roadmapId = " + roadmapId));

        return RoadmapMapper.convertSingleRoadmapResponse(roadmap);
    }
}
