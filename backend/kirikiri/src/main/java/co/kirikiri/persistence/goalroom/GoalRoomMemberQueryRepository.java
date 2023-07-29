package co.kirikiri.persistence.goalroom;

import co.kirikiri.domain.goalroom.GoalRoomMember;
import co.kirikiri.domain.goalroom.GoalRoomStatus;
import co.kirikiri.domain.member.vo.Identifier;
import java.util.Optional;
import org.springframework.data.repository.query.Param;

public interface GoalRoomMemberQueryRepository {

    Optional<GoalRoomMember> findByRoadmapIdAndMemberIdentifierAndGoalRoomStatus(
            @Param("roadmapId") final Long roadmapId,
            @Param("identifier") final Identifier identifier,
            @Param("status") final GoalRoomStatus status);
}
