package co.kirikiri.service;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.persistence.dto.GoalRoomLastValueDto;
import co.kirikiri.persistence.dto.RoadmapFilterType;
import co.kirikiri.persistence.dto.RoadmapLastValueDto;
import co.kirikiri.persistence.dto.RoadmapSearchDto;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.persistence.goalroom.dto.RoadmapGoalRoomsFilterType;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.persistence.roadmap.RoadmapCategoryRepository;
import co.kirikiri.persistence.roadmap.RoadmapContentRepository;
import co.kirikiri.persistence.roadmap.RoadmapRepository;
import co.kirikiri.service.dto.CustomScrollRequest;
import co.kirikiri.service.dto.roadmap.RoadmapGoalRoomNumberDto;
import co.kirikiri.service.dto.roadmap.RoadmapGoalRoomsFilterTypeDto;
import co.kirikiri.service.dto.roadmap.request.RoadmapFilterTypeRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapSearchRequest;
import co.kirikiri.service.dto.roadmap.response.MemberRoadmapResponses;
import co.kirikiri.service.dto.roadmap.response.RoadmapCategoryResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapForListResponses;
import co.kirikiri.service.dto.roadmap.response.RoadmapGoalRoomResponses;
import co.kirikiri.service.dto.roadmap.response.RoadmapResponse;
import co.kirikiri.service.mapper.GoalRoomMapper;
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
    private final GoalRoomRepository goalRoomRepository;
    private final MemberRepository memberRepository;

    public RoadmapResponse findRoadmap(final Long id) {
        final Roadmap roadmap = findRoadmapById(id);
        final RoadmapContent recentRoadmapContent = findRecentContent(roadmap);
        final List<GoalRoom> goalRooms = goalRoomRepository.findByRoadmap(roadmap);
        final RoadmapGoalRoomNumberDto roadmapGoalRoomNumberDto = GoalRoomMapper.convertRoadmapGoalRoomDto(goalRooms);
        return RoadmapMapper.convertToRoadmapResponse(roadmap, recentRoadmapContent, roadmapGoalRoomNumberDto);
    }

    private Roadmap findRoadmapById(final Long id) {
        return roadmapRepository.findRoadmapById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 로드맵입니다. roadmapId = " + id));
    }

    private RoadmapContent findRecentContent(final Roadmap roadmap) {
        return roadmapContentRepository.findFirstByRoadmapOrderByCreatedAtDesc(roadmap)
                .orElseThrow(() -> new NotFoundException("로드맵에 컨텐츠가 존재하지 않습니다."));
    }

    public RoadmapForListResponses findRoadmapsByFilterType(final Long categoryId,
                                                            final RoadmapFilterTypeRequest filterType,
                                                            final CustomScrollRequest scrollRequest) {
        final RoadmapCategory category = findCategoryById(categoryId);
        final RoadmapFilterType orderType = RoadmapMapper.convertRoadmapOrderType(filterType);
        final RoadmapLastValueDto roadmapLastValueDto = RoadmapLastValueDto.create(scrollRequest);
        final List<Roadmap> roadmaps = roadmapRepository.findRoadmapsByCategory(category, orderType,
                roadmapLastValueDto, scrollRequest.size());
        return RoadmapMapper.convertRoadmapResponses(roadmaps, scrollRequest.size());
    }

    private RoadmapCategory findCategoryById(final Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return roadmapCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 카테고리입니다. categoryId = " + categoryId));
    }

    public RoadmapForListResponses search(final RoadmapFilterTypeRequest filterTypeRequest,
                                          final RoadmapSearchRequest searchRequest,
                                          final CustomScrollRequest scrollRequest) {
        final RoadmapFilterType orderType = RoadmapMapper.convertRoadmapOrderType(filterTypeRequest);
        final RoadmapSearchDto roadmapSearchDto = RoadmapSearchDto.create(
                searchRequest.creatorId(), searchRequest.roadmapTitle(), searchRequest.tagName());
        final RoadmapLastValueDto roadmapLastValueDto = RoadmapLastValueDto.create(scrollRequest);
        final List<Roadmap> roadmaps = roadmapRepository.findRoadmapsByCond(roadmapSearchDto, orderType,
                roadmapLastValueDto, scrollRequest.size());
        return RoadmapMapper.convertRoadmapResponses(roadmaps, scrollRequest.size());
    }

    public List<RoadmapCategoryResponse> findAllRoadmapCategories() {
        final List<RoadmapCategory> roadmapCategories = roadmapCategoryRepository.findAll();
        return RoadmapMapper.convertRoadmapCategoryResponses(roadmapCategories);
    }

    public MemberRoadmapResponses findAllMemberRoadmaps(final String identifier,
                                                        final CustomScrollRequest scrollRequest) {
        final Member member = findMemberByIdentifier(identifier);
        final RoadmapLastValueDto roadmapLastValueDto = RoadmapLastValueDto.create(scrollRequest);
        final List<Roadmap> roadmaps = roadmapRepository.findRoadmapsWithCategoryByMemberOrderByLatest(member,
                roadmapLastValueDto, scrollRequest.size());
        return RoadmapMapper.convertMemberRoadmapResponses(roadmaps, scrollRequest.size());
    }

    private Member findMemberByIdentifier(final String identifier) {
        return memberRepository.findByIdentifier(new Identifier(identifier))
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));
    }

    public RoadmapGoalRoomResponses findRoadmapGoalRoomsByFilterType(final Long roadmapId,
                                                                     final RoadmapGoalRoomsFilterTypeDto filterTypeDto,
                                                                     final CustomScrollRequest scrollRequest) {
        final Roadmap roadmap = findRoadmapById(roadmapId);
        final RoadmapGoalRoomsFilterType filterType = GoalRoomMapper.convertToGoalRoomFilterType(filterTypeDto);
        final GoalRoomLastValueDto goalRoomLastValueDto = GoalRoomLastValueDto.create(scrollRequest);
        final List<GoalRoom> goalRoomsWithPendingMembers = goalRoomRepository.findGoalRoomsWithPendingMembersByRoadmapAndCond(
                roadmap, filterType, goalRoomLastValueDto, scrollRequest.size());
        return GoalRoomMapper.convertToRoadmapGoalRoomResponses(goalRoomsWithPendingMembers, scrollRequest.size());
    }
}
