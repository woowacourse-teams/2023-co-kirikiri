package co.kirikiri.persistence.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import co.kirikiri.common.exception.BadRequestException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class RoadmapSearchTagNameTest {

    @ParameterizedTest
    @CsvSource(value = {"안 녕:안녕", "안녕 :안녕", " 안녕:안녕"}, delimiter = ':')
    void 검색어에_공백이_들어가면_제거한다(final String name, final String removedBlankName) {
        // when
        final RoadmapSearchTagName searchTagName = assertDoesNotThrow(() -> new RoadmapSearchTagName(name));

        // then
        assertThat(searchTagName.value())
                .isEqualTo(removedBlankName);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    void 검색어가_1자이상이면_정상적으로_생성된다(final int length) {
        // given
        final String name = "a".repeat(length);

        // expected
        assertDoesNotThrow(() -> new RoadmapSearchTagName(name));
    }

    @ParameterizedTest
    @ValueSource(ints = {0})
    void 검색어가_1자미만이면_예외가_발생한다(final int length) {
        // given
        final String name = "a".repeat(length);

        // expected
        assertThatThrownBy(() -> new RoadmapSearchTagName(name))
                .isInstanceOf(BadRequestException.class);
    }
}
