package co.kirikiri.service.mapper;

import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberImage;
import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;
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

    public static MemberInformationResponse convertToMemberInformationResponse(final Member member) {
        final MemberImage memberImage = member.getImage();
        final MemberProfile memberProfile = member.getMemberProfile();
        return new MemberInformationResponse(member.getId(), member.getNickname().getValue(),
                memberImage.getServerFilePath(),
                memberProfile.getGender().name(), member.getIdentifier().getValue(), memberProfile.getPhoneNumber(),
                memberProfile.getBirthday());
    }

    public static MemberInformationForPublicResponse convertToMemberInformationForPublicResponse(final Member member) {
        final MemberImage memberImage = member.getImage();
        final MemberProfile memberProfile = member.getMemberProfile();
        return new MemberInformationForPublicResponse(member.getNickname().getValue(), memberImage.getServerFilePath(),
                memberProfile.getGender().name());
    }
}
