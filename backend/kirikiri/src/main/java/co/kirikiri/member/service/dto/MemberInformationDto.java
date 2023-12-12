package co.kirikiri.member.service.dto;

public record MemberInformationDto(
        Long id,
        String nickname,
        String profileImageUrl,
        String gender,
        String identifier,
        String email
) {

}
