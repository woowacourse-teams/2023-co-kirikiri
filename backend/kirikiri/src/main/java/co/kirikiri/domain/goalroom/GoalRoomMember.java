package co.kirikiri.domain.goalroom;

import co.kirikiri.domain.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalRoomMember extends BaseGoalRoomMember {

    @Column
    private Double accomplishmentRate = 0.0;

    public GoalRoomMember(final GoalRoomRole role, final LocalDateTime joinedAt,
                          final GoalRoom goalRoom, final Member member) {
        super(role, joinedAt, goalRoom, member);
    }

    public void updateAccomplishmentRate(final Double rate) {
        this.accomplishmentRate = rate;
    }

    public Double getAccomplishmentRate() {
        return accomplishmentRate;
    }
}
