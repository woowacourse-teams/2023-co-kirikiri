package co.kirikiri.auth.service.dto;

import co.kirikiri.member.domain.vo.Identifier;
import co.kirikiri.member.domain.vo.Password;

public record LoginDto(
        Identifier identifier,
        Password password
) {

}
