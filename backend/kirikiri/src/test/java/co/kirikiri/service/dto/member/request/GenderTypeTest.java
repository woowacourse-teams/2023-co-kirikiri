package co.kirikiri.service.dto.member.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class GenderTypeTest {

    @Test
    void oauthGenderType이_M인_경우_MALE을_반환한다() {
        //given
        final String oauthGenderType = "M";

        //when
        final GenderType genderType = GenderType.findByOauthType(oauthGenderType);

        //then
        assertThat(genderType).isEqualTo(GenderType.MALE);
    }

    @Test
    void oauthGenderType이_F인_경우_FEMALE을_반환한다() {
        //given
        final String oauthGenderType = "F";

        //when
        final GenderType genderType = GenderType.findByOauthType(oauthGenderType);

        //then
        assertThat(genderType).isEqualTo(GenderType.FEMALE);
    }

    @Test
    void oauthGenderType이_U인_경우_UNDEFINED을_반환한다() {
        //given
        final String oauthGenderType = "U";

        //when
        final GenderType genderType = GenderType.findByOauthType(oauthGenderType);

        //then
        assertThat(genderType).isEqualTo(GenderType.UNDEFINED);
    }
}
