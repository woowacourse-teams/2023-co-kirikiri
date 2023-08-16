package co.kirikiri.service.dto.member;

import java.time.LocalDate;

public record MemberInformationDto(
        Long id,
        String nickname,
        String profileImageUrl,
        String gender,
        String identifier,
        String phoneNumber,
        LocalDate birthday
) {
}
