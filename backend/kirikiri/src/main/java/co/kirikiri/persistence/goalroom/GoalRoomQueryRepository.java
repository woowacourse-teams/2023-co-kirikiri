package co.kirikiri.persistence.goalroom;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomStatus;
import co.kirikiri.domain.member.Member;
import co.kirikiri.persistence.goalroom.dto.GoalRoomFilterType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GoalRoomQueryRepository {

    Optional<GoalRoom> findByIdWithRoadmapContent(final Long goalRoomId);

    Optional<GoalRoom> findByIdWithContentAndNodesAndTodos(final Long goalRoomId);

    Page<GoalRoom> findGoalRoomsWithPendingMembersPageByCond(final GoalRoomFilterType filterType,
                                                             final Pageable pageable);

    List<GoalRoom> findAllByStartDateWithGoalRoomRoadmapNode();

    List<GoalRoom> findByMember(final Member member);

    List<GoalRoom> findByMemberAndStatus(final Member member, final GoalRoomStatus goalRoomStatus);
}
