package co.kirikiri.domain.roadmap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import co.kirikiri.domain.member.EncryptedPassword;
import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;
import co.kirikiri.exception.BadRequestException;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RoadmapReviewTest {

    @ParameterizedTest
    @ValueSource(doubles = {0, 0.5, 1, 4.5, 5})
    void 별점이_0부터_5사이의_소수점이_5로_끝나는_값이면서_내용이_1000자_이내라면_정상적으로_생성된다(final Double rate) {
        // given
        final String content = "a".repeat(1000);
        final MemberProfile profile = new MemberProfile(Gender.FEMALE, LocalDate.of(1999, 6, 8),
                new Nickname("nickname"), "01011112222");
        final Member member = new Member(new Identifier("creator"),
                new EncryptedPassword(new Password("password1")), profile);

        // expected
        final RoadmapReview roadmapReview =
                assertDoesNotThrow(() -> new RoadmapReview(content, rate, member));
        assertThat(roadmapReview)
                .isInstanceOf(RoadmapReview.class);
    }

    @Test
    void 리뷰_내용이_1000자를_넘으면_예외가_발생한다() {
        // given
        final String content = "a".repeat(1001);
        final MemberProfile profile = new MemberProfile(Gender.FEMALE, LocalDate.of(1999, 6, 8),
                new Nickname("nickname"), "01011112222");
        final Member member = new Member(new Identifier("creator"),
                new EncryptedPassword(new Password("password1")), profile);

        // expected
        assertThatThrownBy(() -> new RoadmapReview(content, null, member))
                .isInstanceOf(BadRequestException.class);
    }

    @ParameterizedTest
    @ValueSource(doubles = {-1, -1.5, 5.5, 1.2, 3.7, 4.55})
    void 리뷰_별점이_0과_5사이의_소수점_5_단위의_값이_아니면_예외가_발생한다(final double rate) {
        // given
        final MemberProfile profile = new MemberProfile(Gender.FEMALE, LocalDate.of(1999, 6, 8),
                new Nickname("nickname"), "01011112222");
        final Member member = new Member(new Identifier("creator"),
                new EncryptedPassword(new Password("password1")), profile);

        // expected
        assertThatThrownBy(() -> new RoadmapReview("리뷰", rate, member))
                .isInstanceOf(BadRequestException.class);
    }
}
