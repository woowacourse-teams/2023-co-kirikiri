package co.kirikiri.persistence.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import co.kirikiri.exception.BadRequestException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class RoadmapSearchTitleTest {

    @ParameterizedTest
    @CsvSource(value = {"안 녕:안녕", "안녕 :안녕", " 안녕:안녕"}, delimiter = ':')
    void 검색어에_공백이_들어가면_제거한다(final String title, final String removedBlankTitle) {
        // when
        final RoadmapSearchTitle searchTitle = assertDoesNotThrow(() -> new RoadmapSearchTitle(title));

        // then
        assertThat(searchTitle.getValue())
                .isEqualTo(removedBlankTitle);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    void 검색어가_1자이상이면_정상적으로_생성된다(final int length) {
        // given
        final String title = "a".repeat(length);

        // expected
        assertDoesNotThrow(() -> new RoadmapSearchTitle(title));
    }

    @ParameterizedTest
    @ValueSource(ints = {0})
    void 검색어가_1자미만이면_예외가_발생한다(final int length) {
        // given
        final String title = "a".repeat(length);

        // expected
        assertThatThrownBy(() -> new RoadmapSearchTitle(title))
                .isInstanceOf(BadRequestException.class);
    }
}
