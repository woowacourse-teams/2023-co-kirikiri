package co.kirikiri.domain.goalroom;

import co.kirikiri.common.entity.BaseEntity;
import co.kirikiri.member.domain.Member;
import com.querydsl.core.annotations.QueryInit;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

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
    @OnDelete(action = OnDeleteAction.CASCADE)
    @QueryInit(value = {"roadmapContent.roadmap"})
    protected GoalRoom goalRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @QueryInit(value = {"identifier"})
    protected Member member;

    protected BaseGoalRoomMember(final GoalRoomRole role, final LocalDateTime joinedAt,
                                 final GoalRoom goalRoom, final Member member) {
        this(null, role, joinedAt, goalRoom, member);
    }

    protected BaseGoalRoomMember(final Long id, final GoalRoomRole role, final LocalDateTime joinedAt,
                                 final GoalRoom goalRoom, final Member member) {
        this.id = id;
        this.role = role;
        this.joinedAt = joinedAt;
        this.goalRoom = goalRoom;
        this.member = member;
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
