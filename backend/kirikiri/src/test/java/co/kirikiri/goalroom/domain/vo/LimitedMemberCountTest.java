package co.kirikiri.goalroom.domain.vo;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import co.kirikiri.goalroom.domain.exception.GoalRoomException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class LimitedMemberCountTest {

    @ParameterizedTest
    @ValueSource(ints = {1, 5, 10, 20})
    void 제한_인원_수가_1이상_20이하일_때_정상적으로_생성된다(final int value) {
        //given
        //when
        //then
        assertDoesNotThrow(() -> new LimitedMemberCount(value));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 21, 22, 100})
    void 제한_인원_수가_1미만_20초과일_때_예외를_던진다(final int value) {
        //given
        //when
        //then
        assertThatThrownBy(() -> new LimitedMemberCount(value))
                .isInstanceOf(GoalRoomException.class);
    }
}
