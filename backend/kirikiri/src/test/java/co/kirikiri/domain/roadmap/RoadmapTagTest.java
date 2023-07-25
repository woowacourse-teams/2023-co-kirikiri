package co.kirikiri.domain.roadmap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import co.kirikiri.exception.BadRequestException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RoadmapTagTest {

    @ParameterizedTest
    @ValueSource(strings = {"오", "안녕하세요10글자임"})
    void 로드맵_태그_이름이_1글자에서_10글자_사이면_정상_생성된다(final String name) {
        // when
        final RoadmapTag roadmapTag = assertDoesNotThrow(() -> new RoadmapTag(name));

        // then
        assertThat(roadmapTag)
                .isInstanceOf(RoadmapTag.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 11})
    void 로드맵_태그_이름이_1글자_미만이거나_10글자_초과면_예외가_발생한다(final int length) {
        // given
        final String name = "a".repeat(length);

        // expected
        assertThatThrownBy(() -> new RoadmapTag(name))
                .isInstanceOf(BadRequestException.class);
    }
}
