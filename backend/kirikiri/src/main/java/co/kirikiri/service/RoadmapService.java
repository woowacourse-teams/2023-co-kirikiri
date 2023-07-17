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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RoadmapService {

    private final RoadmapRepository roadmapRepository;
    private final RoadmapCategoryRepository roadmapCategoryRepository;

    @Transactional
    public Long create(final RoadmapSaveRequest request, final Member member) {
        final RoadmapCategory roadmapCategory = findRoadmapCategoryById(request.categoryId());
        final RoadmapSaveDto roadmapSaveDto = RoadmapMapper.convertToRoadmapSaveDto(request);
        final Roadmap roadmap = makeRoadmap(member, roadmapSaveDto, roadmapCategory);

        return roadmapRepository.save(roadmap).getId();
    }

    private RoadmapCategory findRoadmapCategoryById(final Long categoryId) {
        return roadmapCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 카테고리입니다. categoryId=" + categoryId));
    }

    private Roadmap makeRoadmap(final Member member, final RoadmapSaveDto roadmapSaveDto,
                                final RoadmapCategory roadmapCategory) {
        final RoadmapNodes roadmapNodes = makeRoadmapNodes(roadmapSaveDto.roadmapNodes());
        final RoadmapContent roadmapContent = makeRoadmapContent(roadmapSaveDto, roadmapNodes);
        final Roadmap roadmap = makeRoadmap(roadmapSaveDto, member, roadmapCategory);
        roadmap.addContent(roadmapContent);
        return roadmap;
    }

    private RoadmapNodes makeRoadmapNodes(final List<RoadmapNodesSaveDto> roadmapNodesSaveDtos) {
        return new RoadmapNodes(
                roadmapNodesSaveDtos.stream()
                        .map(node -> new RoadmapNode(node.title(), node.content()))
                        .toList()
        );
    }

    private RoadmapContent makeRoadmapContent(final RoadmapSaveDto roadmapSaveDto, final RoadmapNodes roadmapNodes) {
        final RoadmapContent roadmapContent = new RoadmapContent(roadmapSaveDto.content());
        roadmapContent.addNodes(roadmapNodes);
        return roadmapContent;
    }

    private Roadmap makeRoadmap(final RoadmapSaveDto roadmapSaveDto, final Member member,
                                final RoadmapCategory roadmapCategory) {
        return new Roadmap(roadmapSaveDto.title(), roadmapSaveDto.introduction(), roadmapSaveDto.requiredPeriod(),
                RoadmapDifficulty.valueOf(roadmapSaveDto.difficulty().name()), member, roadmapCategory);
    }
}
