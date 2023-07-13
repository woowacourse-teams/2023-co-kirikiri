package co.kirikiri.service.dto.member;

import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;
import java.time.LocalDate;

public record JoinMemberDto(
    Identifier identifier,
    Password password,
    Nickname nickname,
    String phoneNumber,
    Gender gender,
    LocalDate birthday
) {

}
