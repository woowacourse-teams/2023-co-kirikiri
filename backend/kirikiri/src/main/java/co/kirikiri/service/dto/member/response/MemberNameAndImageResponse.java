package co.kirikiri.service.dto.member.response;

public record MemberNameAndImageResponse(
        Long id,
        String nickname,
        String imageUrl
) {

}
