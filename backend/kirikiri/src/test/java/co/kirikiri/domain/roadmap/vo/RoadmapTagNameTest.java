package co.kirikiri.domain.roadmap.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import co.kirikiri.exception.BadRequestException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RoadmapTagNameTest {

    @ParameterizedTest
    @ValueSource(strings = {"오", "안녕하세요10글자임"})
    void 로드맵_태그_이름이_1글자에서_10글자_사이면_정상_생성된다(final String name) {
        // when
        final RoadmapTagName roadmapTagName = assertDoesNotThrow(() -> new RoadmapTagName(name));

        // then
        assertThat(roadmapTagName)
                .isInstanceOf(RoadmapTagName.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 11})
    void 로드맵_태그_이름이_1글자_미만이거나_10글자_초과면_예외가_발생한다(final int length) {
        // given
        final String name = "a".repeat(length);

        // expected
        assertThatThrownBy(() -> new RoadmapTagName(name))
                .isInstanceOf(BadRequestException.class);
    }
}
