package co.kirikiri.persistence.goalroom;

import co.kirikiri.domain.goalroom.CheckFeed;
import co.kirikiri.domain.goalroom.CheckFeedReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckFeedReportRepository extends JpaRepository<CheckFeedReport, Long> {

    public int countAllByCheckFeed(final CheckFeed checkFeed);
}
