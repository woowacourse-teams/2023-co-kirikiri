package co.kirikiri.service.dto.member.response;

import java.time.LocalDate;

public record MemberMyInfoResponse(
        String nickname,
        String profileImageUrl,
        String gender,
        String identifier,
        String phoneNumber,
        LocalDate birthday
) {

}
