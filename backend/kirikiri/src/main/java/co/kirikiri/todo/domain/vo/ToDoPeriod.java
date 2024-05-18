package co.kirikiri.todo.domain.vo;

import co.kirikiri.todo.domain.exception.GoalRoomToDoException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ToDoPeriod {

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    public ToDoPeriod(final LocalDate startDate, final LocalDate endDate) {
        validate(startDate, endDate);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    private void validate(final LocalDate startDate, final LocalDate endDate) {
        validateStartDateAfterNow(startDate);
        validateStartDateBeforeOrEqualEndDate(startDate, endDate);
    }

    private void validateStartDateAfterNow(final LocalDate startDate) {
        if (startDate.isBefore(LocalDate.now())) {
            throw new GoalRoomToDoException("시작일은 오늘보다 전일 수 없습니다.");
        }
    }

    private void validateStartDateBeforeOrEqualEndDate(final LocalDate startDate, final LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new GoalRoomToDoException("시작일은 종료일보다 후일 수 없습니다.");
        }
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}
