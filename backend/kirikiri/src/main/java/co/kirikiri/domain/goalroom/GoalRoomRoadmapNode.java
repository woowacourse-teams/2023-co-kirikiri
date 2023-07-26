package co.kirikiri.domain.goalroom;

import co.kirikiri.domain.goalroom.vo.Period;
import co.kirikiri.domain.roadmap.RoadmapNode;
import co.kirikiri.exception.BadRequestException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalRoomRoadmapNode {

    private static final int MIN_CHECK_COUNT = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Period period;

    private Integer checkCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roadmap_node_id", nullable = false)
    private RoadmapNode roadmapNode;

    public GoalRoomRoadmapNode(final Period period, final Integer checkCount, final RoadmapNode roadmapNode) {
        validate(period, checkCount);
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
            throw new BadRequestException("골룸 노드의 인증 횟수는 0보다 커야합니다.");
        }
    }

    private void validateCheckCountWithDaysBetween(final Period period, final int checkCount) {
        if (checkCount > period.getTimeInterval()) {
            throw new BadRequestException("골룸 노드의 인증 횟수가 설정 기간보다 클 수 없습니다.");
        }
    }

    public boolean isEndDateEqualOrAfterOtherStartDate(final GoalRoomRoadmapNode nextNode) {
        return this.period.isEndDateEqualOrAfterOtherStartDate(nextNode.period);
    }

    public LocalDate getStartDate() {
        return period.getStartDate();
    }
}
