package co.kirikiri.service.dto.auth;

import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Password;

public record LoginDto(
        Identifier identifier,
        Password password
) {

}
