package co.kirikiri.goalroom.domain;

import co.kirikiri.common.entity.BaseEntity;
import co.kirikiri.goalroom.domain.exception.GoalRoomException;
import co.kirikiri.goalroom.domain.vo.Period;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalRoomRoadmapNode extends BaseEntity {

    private static final int MIN_CHECK_COUNT = 0;

    @Embedded
    private Period period;

    private Integer checkCount;

    private Long roadmapNodeId;

    public GoalRoomRoadmapNode(final Period period, final Integer checkCount, final Long roadmapNodeId) {
        this(null, period, checkCount, roadmapNodeId);
    }

    public GoalRoomRoadmapNode(final Long id, final Period period, final Integer checkCount,
                               final Long roadmapNodeId) {
        validate(period, checkCount);
        this.id = id;
        this.period = period;
        this.checkCount = checkCount;
        this.roadmapNodeId = roadmapNodeId;
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

    public Long getRoadmapNodeId() {
        return roadmapNodeId;
    }

    public int getCheckCount() {
        return checkCount;
    }
}
