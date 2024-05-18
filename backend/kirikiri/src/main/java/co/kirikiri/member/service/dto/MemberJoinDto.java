package co.kirikiri.member.service.dto;

import co.kirikiri.member.domain.Gender;
import co.kirikiri.member.domain.vo.Identifier;
import co.kirikiri.member.domain.vo.Nickname;
import co.kirikiri.member.domain.vo.Password;

public record MemberJoinDto(
        Identifier identifier,
        Password password,
        Nickname nickname,
        Gender gender,
        String email
) {

}
