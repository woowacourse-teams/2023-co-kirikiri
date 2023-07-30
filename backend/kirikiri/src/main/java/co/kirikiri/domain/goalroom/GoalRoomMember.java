package co.kirikiri.domain.goalroom;

import co.kirikiri.domain.BaseEntity;
import co.kirikiri.domain.member.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalRoomMember extends BaseEntity {

    @Enumerated(value = EnumType.STRING)
    private GoalRoomRole role;

    private LocalDateTime joinedAt;

    private final Double accomplishmentRate = 0.0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_room_id", nullable = false)
    private GoalRoom goalRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public GoalRoomMember(final GoalRoomRole role, final LocalDateTime joinedAt,
                          final GoalRoom goalRoom, final Member member) {
        this.role = role;
        this.joinedAt = joinedAt;
        this.goalRoom = goalRoom;
        this.member = member;
    }

    public Member getMember() {
        return member;
    }

    public Double getAccomplishmentRate() {
        return accomplishmentRate;
    }
}
