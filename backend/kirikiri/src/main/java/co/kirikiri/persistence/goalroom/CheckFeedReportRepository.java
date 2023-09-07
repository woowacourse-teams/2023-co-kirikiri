package co.kirikiri.persistence.goalroom;

import co.kirikiri.domain.goalroom.CheckFeed;
import co.kirikiri.domain.goalroom.CheckFeedReport;
import co.kirikiri.domain.goalroom.GoalRoomMember;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckFeedReportRepository extends JpaRepository<CheckFeedReport, Long> {

    public Optional<CheckFeedReport> findByGoalRoomMemberAndCheckFeed(final GoalRoomMember goalRoomMember, final CheckFeed checkFeed);

    public int countAllByCheckFeed(final CheckFeed checkFeed);
}
