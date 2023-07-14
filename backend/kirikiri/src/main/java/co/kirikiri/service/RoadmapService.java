package co.kirikiri.service;

import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.domain.roadmap.RoadmapDifficulty;
import co.kirikiri.domain.roadmap.RoadmapNode;
import co.kirikiri.domain.roadmap.RoadmapNodes;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.persistence.RoadmapCategoryRepository;
import co.kirikiri.persistence.RoadmapRepository;
import co.kirikiri.service.dto.roadmap.RoadmapNodesSaveDto;
import co.kirikiri.service.dto.roadmap.RoadmapSaveDto;
import co.kirikiri.service.dto.roadmap.RoadmapSaveRequest;
import co.kirikiri.service.mapper.RoadmapMapper;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RoadmapService {

    private final RoadmapRepository roadmapRepository;
    private final RoadmapCategoryRepository roadmapCategoryRepository;

    public RoadmapService(final RoadmapRepository roadmapRepository,
                          final RoadmapCategoryRepository roadmapCategoryRepository) {
        this.roadmapRepository = roadmapRepository;
        this.roadmapCategoryRepository = roadmapCategoryRepository;
    }

    @Transactional
    public Long create(final RoadmapSaveRequest request, final Member member) {
        final RoadmapSaveDto roadmapSaveDto = RoadmapMapper.convertToRoadmapSaveDto(request);
        final RoadmapCategory roadmapCategory = roadmapCategoryRepository.findById(roadmapSaveDto.categoryId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 카테고리입니다. categoryId=" + roadmapSaveDto.categoryId()));

        final RoadmapNodes roadmapNodes = makeRoadmapNodes(roadmapSaveDto.roadmapNodes());
        final RoadmapContent roadmapContent = makeRoadmapContent(roadmapSaveDto, roadmapNodes);
        final Roadmap roadmap = makeRoadmap(roadmapSaveDto, member, roadmapCategory, roadmapContent);

        return roadmapRepository.save(roadmap).getId();
    }

    private RoadmapNodes makeRoadmapNodes(final List<RoadmapNodesSaveDto> roadmapNodesSaveDtos) {
        return new RoadmapNodes(
                roadmapNodesSaveDtos.stream()
                        .map(node -> new RoadmapNode(node.title(), node.content()))
                        .toList()
        );
    }

    private RoadmapContent makeRoadmapContent(final RoadmapSaveDto roadmapSaveDto, final RoadmapNodes roadmapNodes) {
        return new RoadmapContent(roadmapSaveDto.content(), roadmapNodes);
    }

    private Roadmap makeRoadmap(final RoadmapSaveDto roadmapSaveDto, final Member member,
                                final RoadmapCategory roadmapCategory, final RoadmapContent roadmapContent) {
        return new Roadmap(roadmapSaveDto.title(), roadmapSaveDto.introduction(), roadmapSaveDto.requiredPeriod(),
                RoadmapDifficulty.valueOf(roadmapSaveDto.difficulty().name()), member, roadmapCategory, roadmapContent);
    }
}
