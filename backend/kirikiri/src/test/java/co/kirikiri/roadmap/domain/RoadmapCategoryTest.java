package co.kirikiri.roadmap.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import co.kirikiri.roadmap.domain.exception.RoadmapException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RoadmapCategoryTest {

    @ParameterizedTest
    @ValueSource(strings = {"글", "여행", "1234567890", "       1234567890        "})
    void 정상적으로_로드맵_카테고리를_생성한다(final String name) {
        //given
        //when
        //then
        assertDoesNotThrow(() -> new RoadmapCategory(name));
    }

    @Test
    void 카테고리_생성시_공백이_들어올_경우_예외를_던진다() {
        //given
        final String space = "";

        //when
        //then
        assertThatThrownBy(() -> new RoadmapCategory(space))
                .isInstanceOf(RoadmapException.class);
    }

    @Test
    void 카테고리_생성시_10글자_초과일_경우_예외를_던진다() {
        //given
        final String space = "12345678901";

        //when
        //then
        assertThatThrownBy(() -> new RoadmapCategory(space))
                .isInstanceOf(RoadmapException.class);
    }
}
