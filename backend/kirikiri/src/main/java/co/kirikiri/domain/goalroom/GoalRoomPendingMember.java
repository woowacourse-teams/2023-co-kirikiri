package co.kirikiri.domain.goalroom;

import co.kirikiri.domain.member.Member;
import co.kirikiri.exception.BadRequestException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalRoomPendingMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
        this.role = role;
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
        throw new BadRequestException("골룸을 변경할 수 없습니다.");
    }

    public boolean isEqualMember(final Member member) {
        return this.member.equals(member);
    }

    public Member getMember() {
        return member;
    }
}
