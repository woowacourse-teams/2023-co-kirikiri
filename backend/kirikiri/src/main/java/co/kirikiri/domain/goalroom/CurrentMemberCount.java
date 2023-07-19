package co.kirikiri.domain.goalroom;

import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CurrentMemberCount {

    @Column(name = "current_member_count")
    private Integer value;

    public CurrentMemberCount(final Integer value) {
        this.value = value;
    }

    public CurrentMemberCount addMemberCount() {
        return new CurrentMemberCount(value + 1);
    }

    public boolean isLessThan(final Integer value) {
        return this.value < value;
    }

    public Integer getValue() {
        return value;
    }
}
