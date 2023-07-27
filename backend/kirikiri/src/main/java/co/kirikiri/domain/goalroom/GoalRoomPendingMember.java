package co.kirikiri.domain.goalroom;

import co.kirikiri.domain.BaseEntity;
import co.kirikiri.domain.member.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalRoomPendingMember extends BaseEntity {

    @Enumerated(value = EnumType.STRING)
    private GoalRoomRole role;

    @CreatedDate
    private LocalDateTime joinedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_room_id", nullable = false)
    private GoalRoom goalRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public GoalRoomPendingMember(final GoalRoomRole role, final LocalDateTime joinedAt, final GoalRoom goalRoom,
                                 final Member member) {
        this.role = role;
        this.joinedAt = joinedAt;
        this.goalRoom = goalRoom;
        this.member = member;
    }

    public GoalRoomRole getRole() {
        return role;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public GoalRoom getGoalRoom() {
        return goalRoom;
    }

    public Member getMember() {
        return member;
    }
}
