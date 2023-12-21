package co.kirikiri.goalroom.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalRoomMember extends BaseGoalRoomMember {

    @Column
    private Double accomplishmentRate = 0.0;

    public GoalRoomMember(final GoalRoomRole role, final LocalDateTime joinedAt,
                          final GoalRoom goalRoom, final Long memberId) {
        super(role, joinedAt, goalRoom, memberId);
    }

    public GoalRoomMember(final Long id, final GoalRoomRole role, final LocalDateTime joinedAt, final GoalRoom goalRoom,
                          final Long memberId) {
        super(id, role, joinedAt, goalRoom, memberId);
    }

    public void updateAccomplishmentRate(final Double rate) {
        this.accomplishmentRate = rate;
    }

    public Double getAccomplishmentRate() {
        return accomplishmentRate;
    }
}
