package co.kirikiri.domain.goalroom;

import co.kirikiri.domain.BaseEntity;
import co.kirikiri.domain.member.Member;
import com.querydsl.core.annotations.QueryInit;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseGoalRoomMember extends BaseEntity {

    @Enumerated(value = EnumType.STRING)
    protected GoalRoomRole role;

    @CreatedDate
    protected LocalDateTime joinedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_room_id", nullable = false)
    @QueryInit(value = {"roadmapContent.roadmap"})
    protected GoalRoom goalRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @QueryInit(value = {"identifier"})
    protected Member member;

    public BaseGoalRoomMember(final GoalRoomRole role, final LocalDateTime joinedAt,
                              final GoalRoom goalRoom, final Member member) {
        this(null, role, joinedAt, goalRoom, member);
    }

    public BaseGoalRoomMember(final Long id, final GoalRoomRole role, final LocalDateTime joinedAt,
                              final GoalRoom goalRoom, final Member member) {
        this.id = id;
        this.role = role;
        this.joinedAt = joinedAt;
        this.goalRoom = goalRoom;
        this.member = member;
    }

    public boolean isLeader() {
        return role == GoalRoomRole.LEADER;
    }

    public boolean isSameMember(final Member member) {
        return this.member.equals(member);
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
        return Objects.equals(goalRoom, that.goalRoom) && Objects.equals(member, that.member);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), goalRoom, member);
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
