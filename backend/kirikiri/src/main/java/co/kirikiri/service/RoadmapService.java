package co.kirikiri.service;

import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.domain.roadmap.RoadmapDifficulty;
import co.kirikiri.domain.roadmap.RoadmapNode;
import co.kirikiri.domain.roadmap.RoadmapNodes;
import co.kirikiri.domain.roadmap.dto.RoadmapFilterType;
import co.kirikiri.exception.AuthenticationException;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.persistence.roadmap.RoadmapCategoryRepository;
import co.kirikiri.persistence.roadmap.RoadmapContentRepository;
import co.kirikiri.persistence.roadmap.RoadmapRepository;
import co.kirikiri.service.dto.CustomPageRequest;
import co.kirikiri.service.dto.PageResponse;
import co.kirikiri.service.dto.roadmap.RoadmapCategoryResponse;
import co.kirikiri.service.dto.roadmap.RoadmapFilterTypeDto;
import co.kirikiri.service.dto.roadmap.RoadmapNodeSaveDto;
import co.kirikiri.service.dto.roadmap.RoadmapResponse;
import co.kirikiri.service.dto.roadmap.RoadmapSaveDto;
import co.kirikiri.service.dto.roadmap.RoadmapSaveRequest;
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
    private final MemberRepository memberRepository;
    private final RoadmapContentRepository roadmapContentRepository;

    @Transactional
    public Long create(final RoadmapSaveRequest request, final String identifier) {
        final Member member = memberRepository.findByIdentifier(new Identifier(identifier))
                .orElseThrow(() -> new AuthenticationException("존재하지 않은 회원입니다."));
        final RoadmapCategory roadmapCategory = findCategoryById(request.categoryId());
        final RoadmapSaveDto roadmapSaveDto = RoadmapMapper.convertToRoadmapSaveDto(request);
        final Roadmap roadmap = makeRoadmap(member, roadmapSaveDto, roadmapCategory);

        return roadmapRepository.save(roadmap).getId();
    }

    private RoadmapCategory findCategoryById(final Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return roadmapCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 카테고리입니다. categoryId = " + categoryId));
    }

    private Roadmap makeRoadmap(final Member member, final RoadmapSaveDto roadmapSaveDto,
                                final RoadmapCategory roadmapCategory) {
        final RoadmapNodes roadmapNodes = makeRoadmapNodes(roadmapSaveDto.roadmapNodes());
        final RoadmapContent roadmapContent = makeRoadmapContent(roadmapSaveDto, roadmapNodes);
        final Roadmap roadmap = makeRoadmap(roadmapSaveDto, member, roadmapCategory);
        roadmap.addContent(roadmapContent);
        return roadmap;
    }

    private RoadmapNodes makeRoadmapNodes(final List<RoadmapNodeSaveDto> roadmapNodeSaveDtos) {
        return new RoadmapNodes(
                roadmapNodeSaveDtos.stream()
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

    public RoadmapResponse findRoadmap(final Long id) {
        final Roadmap roadmap = findRoadmapById(id);
        final RoadmapContent recentRoadmapContent = findRecentContent(roadmap);

        return RoadmapMapper.convertToRoadmapResponse(roadmap, recentRoadmapContent);
    }

    private Roadmap findRoadmapById(final Long id) {
        return roadmapRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 로드맵입니다. roadmapId = " + id));
    }

    private RoadmapContent findRecentContent(final Roadmap roadmap) {
        return roadmapContentRepository.findFirstByRoadmapOrderByCreatedAtDesc(roadmap)
                .orElseThrow(() -> new NotFoundException("로드맵에 컨텐츠가 존재하지 않습니다."));
    }

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

    public List<RoadmapCategoryResponse> getAllRoadmapCategories() {
        final List<RoadmapCategory> roadmapCategories = roadmapCategoryRepository.findAll();
        return RoadmapMapper.convertRoadmapCategoryResponses(roadmapCategories);
    }
}