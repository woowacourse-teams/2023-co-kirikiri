package co.kirikiri.member.service.dto.response;

public record MemberInformationResponse(
        Long id,
        String nickname,
        String profileImageUrl,
        String gender,
        String identifier,
        String email
) {

}
