package co.kirikiri.domain.goalroom.vo;

import co.kirikiri.exception.BadRequestException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class GoalRoomNameTest {

    @ParameterizedTest
    @ValueSource(ints = {1, 10, 20, 30, 40})
    void 골룸의_이름_길이가_1이상_40이하일_때_정상적으로_생성된다(final int length) {
        //given
        final String value = "a".repeat(length);

        //when
        //then
        assertDoesNotThrow((() -> new GoalRoomName(value)));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 41, 42, 100})
    void 골룸의_이름_길이가_1미만_40초과일_때_정상적으로_생성된다(final int length) {
        //given
        final String value = "a".repeat(length);

        //when
        //then
        assertThatThrownBy(() -> new GoalRoomName(value))
                .isInstanceOf(BadRequestException.class);
    }
}
