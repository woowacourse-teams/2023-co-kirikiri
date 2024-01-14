package co.kirikiri.roadmap.domain;

import co.kirikiri.roadmap.domain.exception.RoadmapException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class RoadmapReviewTest {

    @ParameterizedTest
    @ValueSource(doubles = {0, 0.5, 1, 4.5, 5})
    void 별점이_0부터_5사이의_소수점이_5로_끝나는_값이면서_내용이_1000자_이내라면_정상적으로_생성된다(final Double rate) {
        // given
        final String content = "a".repeat(1000);
        final Long memberId = 1L;
        final Long roadmapId = 1L;

        // expected
        final RoadmapReview roadmapReview =
                assertDoesNotThrow(() -> new RoadmapReview(content, rate, memberId, roadmapId));
        assertThat(roadmapReview)
                .isInstanceOf(RoadmapReview.class);
    }

    @Test
    void 리뷰_내용이_1000자를_넘으면_예외가_발생한다() {
        // given
        final String content = "a".repeat(1001);
        final Long memberId = 1L;
        final Long roadmapId = 1L;

        // expected
        assertThatThrownBy(() -> new RoadmapReview(content, null, memberId, roadmapId))
                .isInstanceOf(RoadmapException.class);
    }

    @ParameterizedTest
    @ValueSource(doubles = {-1, -1.5, 5.5, 1.2, 3.7, 4.55})
    void 리뷰_별점이_0과_5사이의_소수점_5_단위의_값이_아니면_예외가_발생한다(final double rate) {
        // given
        final Long memberId = 1L;
        final Long roadmapId = 1L;

        // expected
        assertThatThrownBy(() -> new RoadmapReview("리뷰", rate, memberId, roadmapId))
                .isInstanceOf(RoadmapException.class);
    }
}
