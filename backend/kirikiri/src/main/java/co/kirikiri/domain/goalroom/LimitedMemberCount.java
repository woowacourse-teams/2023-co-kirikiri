package co.kirikiri.domain.goalroom;

import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LimitedMemberCount {

    @Column(name = "limited_member_count")
    private Integer value;

    public LimitedMemberCount(final Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
