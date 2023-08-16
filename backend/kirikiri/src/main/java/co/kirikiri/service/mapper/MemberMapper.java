package co.kirikiri.service.mapper;

import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;
import co.kirikiri.service.dto.member.MemberInformationDto;
import co.kirikiri.service.dto.member.MemberInformationForPublicDto;
import co.kirikiri.service.dto.member.MemberJoinDto;
import co.kirikiri.service.dto.member.request.MemberJoinRequest;
import co.kirikiri.service.dto.member.response.MemberInformationForPublicResponse;
import co.kirikiri.service.dto.member.response.MemberInformationResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MemberMapper {

    public static MemberJoinDto convertToMemberJoinDto(final MemberJoinRequest request) {
        final Identifier identifier = new Identifier(request.identifier());
        final Password password = new Password(request.password());
        final Nickname nickname = new Nickname(request.nickname());
        final Gender gender = Gender.valueOf(request.genderType().name());
        return new MemberJoinDto(identifier, password, nickname, request.phoneNumber(), gender, request.birthday());
    }

    public static MemberInformationResponse convertToMemberInformationResponse(final MemberInformationDto memberInformationDto) {
        return new MemberInformationResponse(memberInformationDto.id(), memberInformationDto.nickname(),
                memberInformationDto.profileImageUrl(), memberInformationDto.gender(), memberInformationDto.identifier(),
                memberInformationDto.phoneNumber(), memberInformationDto.birthday());
    }

    public static MemberInformationForPublicResponse convertToMemberInformationForPublicResponse(
            final MemberInformationForPublicDto memberInformationForPublicDto) {
        return new MemberInformationForPublicResponse(memberInformationForPublicDto.nickname(), memberInformationForPublicDto.profileImageUrl(),
                memberInformationForPublicDto.gender());
    }
}
