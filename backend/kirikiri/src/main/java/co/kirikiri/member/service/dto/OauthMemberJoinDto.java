package co.kirikiri.member.service.dto;

import co.kirikiri.member.service.dto.request.GenderType;

public record OauthMemberJoinDto(
        String oauthId,
        String email,
        String nickname,
        GenderType gender
) {
}
