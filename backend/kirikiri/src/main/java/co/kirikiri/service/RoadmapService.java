package co.kirikiri.service;

import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.roadmap.*;
import co.kirikiri.domain.roadmap.dto.RoadmapFilterType;
import co.kirikiri.exception.AuthenticationException;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.persistence.roadmap.RoadmapCategoryRepository;
import co.kirikiri.persistence.roadmap.RoadmapContentRepository;
import co.kirikiri.persistence.roadmap.RoadmapRepository;
import co.kirikiri.service.dto.CustomPageRequest;
import co.kirikiri.service.dto.PageResponse;
import co.kirikiri.service.dto.roadmap.*;
import co.kirikiri.service.mapper.RoadmapMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RoadmapService {

    private final RoadmapRepository roadmapRepository;
    private final RoadmapCategoryRepository roadmapCategoryRepository;
    private final MemberRepository memberRepository;
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
        return roadmapRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 로드맵입니다. roadmapId = " + id));
    }

    @Transactional
    public Long create(final RoadmapSaveRequest request, final String identifier) {
        final Member member = memberRepository.findByIdentifier(new Identifier(identifier))
                .orElseThrow(() -> new AuthenticationException("존재하지 않은 회원입니다."));
        final RoadmapCategory roadmapCategory = findRoadmapCategoryById(request.categoryId());
        final RoadmapSaveDto roadmapSaveDto = RoadmapMapper.convertToRoadmapSaveDto(request);
        final Roadmap roadmap = makeRoadmap(member, roadmapSaveDto, roadmapCategory);

        return roadmapRepository.save(roadmap).getId();
    }

    private RoadmapCategory findRoadmapCategoryById(final Long categoryId) {
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
