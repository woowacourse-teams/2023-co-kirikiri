package co.kirikiri.domain.goalroom;

import co.kirikiri.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "checkFeedAndGoalRoomMember",
                        columnNames = {
                                "check_feed_id",
                                "goal_room_member_id"
                        }
                ),
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CheckFeedReport extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "check_feed_id", nullable = false)
    private CheckFeed checkFeed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_room_member_id", nullable = false)
    private GoalRoomMember goalRoomMember;

    public CheckFeedReport(final CheckFeed checkFeed, final GoalRoomMember goalRoomMember) {
        this.checkFeed = checkFeed;
        this.goalRoomMember = goalRoomMember;
    }
}



