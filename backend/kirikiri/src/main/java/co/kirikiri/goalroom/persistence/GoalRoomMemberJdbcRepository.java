package co.kirikiri.goalroom.persistence;

import co.kirikiri.goalroom.domain.GoalRoomMember;
import java.util.List;

public interface GoalRoomMemberJdbcRepository {

    void saveAllInBatch(final List<GoalRoomMember> goalRoomMembers);
}
