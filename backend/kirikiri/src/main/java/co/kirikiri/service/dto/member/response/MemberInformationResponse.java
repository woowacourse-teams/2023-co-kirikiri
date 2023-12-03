package co.kirikiri.service.dto.member.response;

import java.time.LocalDate;

public record MemberInformationResponse(
        Long id,
        String nickname,
        String profileImageUrl,
        String gender,
        String identifier,
        String phoneNumber,
        LocalDate birthday
) {

}
