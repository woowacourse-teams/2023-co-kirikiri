package co.kirikiri.domain.goalroom;

import co.kirikiri.domain.member.Member;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalRoomMember extends BaseGoalRoomMember {

    private final Double accomplishmentRate = 0.0;

    public GoalRoomMember(final GoalRoomRole role, final LocalDateTime joinedAt,
                          final GoalRoom goalRoom, final Member member) {
        super(role, joinedAt, goalRoom, member);
    }
}
