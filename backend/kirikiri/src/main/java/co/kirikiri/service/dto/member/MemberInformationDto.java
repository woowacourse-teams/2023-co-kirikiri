package co.kirikiri.service.dto.member;

public record MemberInformationDto(
        Long id,
        String nickname,
        String profileImageUrl,
        String gender,
        String identifier,
        String email
) {

}
