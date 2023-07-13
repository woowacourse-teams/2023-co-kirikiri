package co.kirikiri.service.mapper.member;

import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;
import co.kirikiri.service.dto.member.MemberJoinDto;
import co.kirikiri.service.dto.member.request.MemberJoinRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberMapper {

    public static MemberJoinDto convertToMemberJoinDto(final MemberJoinRequest request) {
        final Identifier identifier = new Identifier(request.identifier());
        final Password password = new Password(request.password());
        final Nickname nickname = new Nickname(request.nickname());
        final Gender gender = Gender.valueOf(request.genderType().name());
        return new MemberJoinDto(identifier, password, nickname, request.phoneNumber(), gender, request.birthday());
    }
}
