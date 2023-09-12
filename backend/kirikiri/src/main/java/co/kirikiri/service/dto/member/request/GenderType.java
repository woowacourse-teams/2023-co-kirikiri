package co.kirikiri.service.dto.member.request;

import co.kirikiri.exception.BadRequestException;
import java.util.Arrays;

public enum GenderType {

    MALE("M"),
    FEMALE("F"),
    UNDEFINED("U");

    private final String oauthType;

    GenderType(final String oauthType) {
        this.oauthType = oauthType;
    }

    public static GenderType findByOauthType(final String oauthType) {
        return Arrays.stream(values())
                .filter(it -> it.oauthType.equals(oauthType))
                .findAny()
                .orElseThrow(() -> new BadRequestException("존재하지 않는 성별 타입입니다."));
    }
}
