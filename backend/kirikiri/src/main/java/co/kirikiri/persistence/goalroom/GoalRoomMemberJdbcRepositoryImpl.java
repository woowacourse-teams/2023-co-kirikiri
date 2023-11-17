package co.kirikiri.persistence.goalroom;

import co.kirikiri.domain.goalroom.GoalRoomMember;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class GoalRoomMemberJdbcRepositoryImpl implements GoalRoomMemberJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public GoalRoomMemberJdbcRepositoryImpl(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void saveAllInBatch(final List<GoalRoomMember> goalRoomMembers) {
        final String sql = "INSERT INTO goal_room_member "
                + "(goal_room_id, member_id, role, joined_at, accomplishment_rate) "
                + "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, goalRoomMembers, goalRoomMembers.size(), ((ps, goalRoomMember) -> {
            ps.setLong(1, goalRoomMember.getGoalRoom().getId());
            ps.setLong(2, goalRoomMember.getMember().getId());
            ps.setString(3, goalRoomMember.getRole().name());
            ps.setObject(4, goalRoomMember.getJoinedAt());
            ps.setDouble(5, goalRoomMember.getAccomplishmentRate());
        }));
    }
}
