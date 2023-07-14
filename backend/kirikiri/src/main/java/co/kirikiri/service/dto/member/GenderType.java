package co.kirikiri.service.dto.member;

import co.kirikiri.domain.member.Gender;
import lombok.Getter;

@Getter
public enum GenderType {

    MALE(Gender.MALE),
    FEMALE(Gender.FEMALE);

    private final Gender gender;

    GenderType(final Gender gender) {
        this.gender = gender;
    }
}
