package co.kirikiri.service.dto.member.response;

public record MemberPublicInfoResponse(
        String nickname,
        String profileImageUrl,
        String gender
) {

}
