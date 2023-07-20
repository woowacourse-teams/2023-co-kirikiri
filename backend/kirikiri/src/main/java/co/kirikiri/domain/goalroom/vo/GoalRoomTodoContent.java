package co.kirikiri.domain.goalroom.vo;

import co.kirikiri.exception.BadRequestException;
import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalRoomTodoContent {

    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 250;

    @Column(name = "content", nullable = false, length = 300)
    private String value;

    public GoalRoomTodoContent(final String value) {
        validate(value);
        this.value = value;
    }

    private void validate(final String value) {
        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new BadRequestException("투두 컨텐츠의 길이가 적절하지 않습니다.");
        }
    }
}
