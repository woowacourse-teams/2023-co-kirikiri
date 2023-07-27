package co.kirikiri.domain.goalroom;

import co.kirikiri.domain.BaseEntity;
import co.kirikiri.domain.member.Member;
import co.kirikiri.exception.ServerException;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;
import java.util.Objects;

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

    public GoalRoomPendingMember(final GoalRoomRole role, final Member member) {
        this(null, role, null, null, member);
    }

    public GoalRoomPendingMember(final GoalRoomRole role, final LocalDateTime joinedAt,
                                 final GoalRoom goalRoom, final Member member) {
        this(null, role, joinedAt, goalRoom, member);
    }

    public GoalRoomPendingMember(final Long id, final GoalRoomRole role, final LocalDateTime joinedAt,
                                 final GoalRoom goalRoom, final Member member) {
        this.id = id;
        this.role = role;
        this.joinedAt = joinedAt;
        this.goalRoom = goalRoom;
        this.member = member;
    }

    public void updateGoalRoom(final GoalRoom goalRoom) {
        if (this.goalRoom == null) {
            this.goalRoom = goalRoom;
            return;
        }
        if (this.goalRoom.equals(goalRoom)) {
            return;
        }
        throw new ServerException("골룸을 변경할 수 없습니다.");
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final GoalRoomPendingMember that = (GoalRoomPendingMember) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
