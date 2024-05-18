package co.kirikiri.member.service.mapper;

import co.kirikiri.member.domain.Gender;
import co.kirikiri.member.domain.vo.Identifier;
import co.kirikiri.member.domain.vo.Nickname;
import co.kirikiri.member.domain.vo.Password;
import co.kirikiri.member.service.dto.MemberInformationDto;
import co.kirikiri.member.service.dto.MemberInformationForPublicDto;
import co.kirikiri.member.service.dto.MemberJoinDto;
import co.kirikiri.member.service.dto.request.MemberJoinRequest;
import co.kirikiri.member.service.dto.response.MemberInformationForPublicResponse;
import co.kirikiri.member.service.dto.response.MemberInformationResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MemberMapper {

    public static MemberJoinDto convertToMemberJoinDto(final MemberJoinRequest request) {
        final Identifier identifier = new Identifier(request.identifier());
        final Password password = new Password(request.password());
        final Nickname nickname = new Nickname(request.nickname());
        final Gender gender = Gender.valueOf(request.genderType().name());
        return new MemberJoinDto(identifier, password, nickname, gender, request.email());
    }

    public static MemberInformationResponse convertToMemberInformationResponse(
            final MemberInformationDto memberInformationDto) {
        return new MemberInformationResponse(memberInformationDto.id(), memberInformationDto.nickname(),
                memberInformationDto.profileImageUrl(), memberInformationDto.gender(),
                memberInformationDto.identifier(), memberInformationDto.email());
    }

    public static MemberInformationForPublicResponse convertToMemberInformationForPublicResponse(
            final MemberInformationForPublicDto memberInformationForPublicDto) {
        return new MemberInformationForPublicResponse(memberInformationForPublicDto.nickname(),
                memberInformationForPublicDto.profileImageUrl(),
                memberInformationForPublicDto.gender());
    }
}
