package co.kirikiri.service;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.roadmap.dto.GoalRoomFilterType;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.service.dto.CustomPageRequest;
import co.kirikiri.service.dto.PageResponse;
import co.kirikiri.service.dto.goalroom.GoalRoomFilterTypeDto;
import co.kirikiri.service.dto.goalroom.response.GoalRoomForListResponse;
import co.kirikiri.service.mapper.GoalRoomMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GoalRoomService {

    private final GoalRoomRepository goalRoomRepository;

    public PageResponse<GoalRoomForListResponse> findGoalRoomsByFilterType(final GoalRoomFilterTypeDto filterTypeDto,
                                                                           final CustomPageRequest pageRequest) {
        final GoalRoomFilterType filterType = GoalRoomMapper.convertToGoalRoomFilterType(filterTypeDto);
        final PageRequest generatedPageRequest = PageRequest.of(pageRequest.page(), pageRequest.size());
        final Page<GoalRoom> goalRoomsWithPendingMembersPage = goalRoomRepository.findGoalRoomsWithPendingMembersPageByCond(
                filterType, generatedPageRequest);
        return GoalRoomMapper.convertToGoalRoomsPageResponse(goalRoomsWithPendingMembersPage, pageRequest);
    }
}
