package co.kirikiri.domain.goalroom;

import co.kirikiri.domain.BaseEntity;
import co.kirikiri.domain.goalroom.exception.GoalRoomException;
import co.kirikiri.domain.goalroom.vo.Period;
import co.kirikiri.roadmap.domain.RoadmapNode;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalRoomRoadmapNode extends BaseEntity {

    private static final int MIN_CHECK_COUNT = 0;

    @Embedded
    private Period period;

    private Integer checkCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roadmap_node_id", nullable = false)
    private RoadmapNode roadmapNode;

    public GoalRoomRoadmapNode(final Period period, final Integer checkCount, final RoadmapNode roadmapNode) {
        this(null, period, checkCount, roadmapNode);
    }

    public GoalRoomRoadmapNode(final Long id, final Period period, final Integer checkCount,
                               final RoadmapNode roadmapNode) {
        validate(period, checkCount);
        this.id = id;
        this.period = period;
        this.checkCount = checkCount;
        this.roadmapNode = roadmapNode;
    }

    private void validate(final Period period, final Integer checkCount) {
        validateCheckCountPositive(checkCount);
        validateCheckCountWithDaysBetween(period, checkCount);
    }

    private void validateCheckCountPositive(final Integer checkCount) {
        if (checkCount < MIN_CHECK_COUNT) {
            throw new GoalRoomException("골룸 노드의 인증 횟수는 0보다 커야합니다.");
        }
    }

    private void validateCheckCountWithDaysBetween(final Period period, final int checkCount) {
        if (checkCount > period.getDayCount()) {
            throw new GoalRoomException("골룸 노드의 인증 횟수가 설정 기간보다 클 수 없습니다.");
        }
    }

    public boolean isEndDateEqualOrAfterOtherStartDate(final GoalRoomRoadmapNode other) {
        return this.period.isEndDateEqualOrAfterOtherStartDate(other.period);
    }

    public boolean isDayOfNode(final LocalDate date) {
        return period.contains(date);
    }

    public LocalDate getStartDate() {
        return period.getStartDate();
    }

    public LocalDate getEndDate() {
        return period.getEndDate();
    }

    public RoadmapNode getRoadmapNode() {
        return roadmapNode;
    }

    public int getCheckCount() {
        return checkCount;
    }
}
