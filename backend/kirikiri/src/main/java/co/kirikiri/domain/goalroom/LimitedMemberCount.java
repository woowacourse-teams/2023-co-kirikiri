package co.kirikiri.domain.goalroom;

import co.kirikiri.exception.BadRequestException;
import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LimitedMemberCount {

    private static final Integer MIN = 1;
    private static final Integer MAX = 20;

    @Column(name = "limited_member_count")
    private Integer value = 0;

    public LimitedMemberCount(final Integer value) {
        validate(value);
        this.value = value;
    }

    private void validate(final int value) {
        if (value < MIN || value > MAX) {
            throw new BadRequestException("제한 인원 수가 적절하지 않습니다.");
        }
    }

    public Integer getValue() {
        return value;
    }
}
