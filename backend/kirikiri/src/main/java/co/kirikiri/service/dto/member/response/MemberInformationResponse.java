package co.kirikiri.service.dto.member.response;

public record MemberInformationResponse(
        Long id,
        String nickname,
        String profileImageUrl,
        String gender,
        String identifier,
        String email
) {

}
