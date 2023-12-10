package co.kirikiri.service.dto.member;

import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;

public record MemberJoinDto(
        Identifier identifier,
        Password password,
        Nickname nickname,
        Gender gender,
        String email
) {

}
