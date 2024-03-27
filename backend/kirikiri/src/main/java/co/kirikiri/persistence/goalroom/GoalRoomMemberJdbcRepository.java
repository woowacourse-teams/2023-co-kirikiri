package co.kirikiri.persistence.goalroom;

import co.kirikiri.domain.goalroom.GoalRoomMember;
import java.util.List;

public interface GoalRoomMemberJdbcRepository {

    void saveAllInBatch(final List<GoalRoomMember> goalRoomMembers);
}
