package co.kirikiri.service.dto.member.request;

import co.kirikiri.exception.BadRequestException;
import java.util.Arrays;

public enum GenderType {

    MALE("M"),
    FEMALE("F"),
    UNDEFINED("U");

    private final String oauthGenderType;

    GenderType(final String oauthGenderType) {
        this.oauthGenderType = oauthGenderType;
    }

    public static GenderType findByOauthType(final String oauthGenderType) {
        return Arrays.stream(values())
                .filter(it -> it.oauthGenderType.equals(oauthGenderType))
                .findAny()
                .orElseThrow(() -> new BadRequestException("존재하지 않는 성별 타입입니다."));
    }
}
