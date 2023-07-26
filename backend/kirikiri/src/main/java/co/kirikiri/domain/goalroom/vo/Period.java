package co.kirikiri.domain.goalroom.vo;

import co.kirikiri.exception.BadRequestException;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Period {

    private LocalDate startDate;

    private LocalDate endDate;

    public Period(final LocalDate startDate, final LocalDate endDate) {
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
            throw new BadRequestException("시작일은 오늘보다 전일 수 없습니다.");
        }
    }

    private void validateStartDateBeforeOrEqualEndDate(final LocalDate startDate, final LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("시작일은 종료일보다 후일 수 없습니다.");
        }
    }

    public boolean isEndDateEqualOrAfterOtherStartDate(final Period other) {
        return this.endDate.isEqual(other.startDate)
                || this.endDate.isAfter(other.startDate);
    }

    public long getTimeInterval() {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    public LocalDate getStartDate() {
        return startDate;
    }
}
