package co.kirikiri.service.dto.member;

import co.kirikiri.service.dto.member.request.GenderType;

public record OauthMemberJoinDto(
        String oauthId,
        String email,
        String nickname,
        GenderType gender
) {
}
