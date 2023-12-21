package co.kirikiri.goalroom.domain;

import co.kirikiri.common.entity.BaseEntity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseGoalRoomMember extends BaseEntity {

    protected static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSSSSS";

    @Enumerated(value = EnumType.STRING)
    protected GoalRoomRole role;

    @CreatedDate
    protected LocalDateTime joinedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_room_id", nullable = false)
    protected GoalRoom goalRoom;

    protected Long memberId;

    protected BaseGoalRoomMember(final GoalRoomRole role, final LocalDateTime joinedAt,
                                 final GoalRoom goalRoom, final Long memberId) {
        this(null, role, joinedAt, goalRoom, memberId);
    }

    protected BaseGoalRoomMember(final Long id, final GoalRoomRole role, final LocalDateTime joinedAt,
                                 final GoalRoom goalRoom, final Long memberId) {
        this.id = id;
        this.role = role;
        this.joinedAt = joinedAt;
        this.goalRoom = goalRoom;
        this.memberId = memberId;
    }

    @PrePersist
    private void prePersist() {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_FORMAT);
        final String formattedTime = LocalDateTime.now().format(formatter);
        joinedAt = LocalDateTime.parse(formattedTime, formatter);
    }

    public boolean isLeader() {
        return role == GoalRoomRole.LEADER;
    }

    public boolean isSameMember(final Long memberId) {
        return this.memberId.equals(memberId);
    }

    public void becomeLeader() {
        this.role = GoalRoomRole.LEADER;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final BaseGoalRoomMember that = (BaseGoalRoomMember) o;
        return role == that.role && Objects.equals(joinedAt, that.joinedAt) && Objects.equals(goalRoom,
                that.goalRoom) && Objects.equals(memberId, that.memberId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), role, joinedAt, goalRoom, memberId);
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

    public Long getMemberId() {
        return memberId;
    }
}
