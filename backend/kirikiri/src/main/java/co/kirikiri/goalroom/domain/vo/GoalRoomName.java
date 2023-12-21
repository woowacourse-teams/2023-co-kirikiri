package co.kirikiri.goalroom.domain.vo;

import co.kirikiri.goalroom.domain.exception.GoalRoomException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalRoomName {

    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 40;

    @Column(nullable = false, length = 50, name = "name")
    private String value;

    public GoalRoomName(final String value) {
        validate(value);
        this.value = value;
    }

    private void validate(final String value) {
        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new GoalRoomException("골룸 이름의 길이가 적절하지 않습니다.");
        }
    }

    public String getValue() {
        return value;
    }
}
