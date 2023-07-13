package co.kirikiri.service.mapper.member;

import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;
import co.kirikiri.service.dto.member.JoinMemberDto;
import co.kirikiri.service.dto.member.request.JoinMemberRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JoinMemberMapper {

    public static JoinMemberDto convert(final JoinMemberRequest request) {
        return new JoinMemberDto(new Identifier(request.identifier()), new Password(request.password()),
            new Nickname(request.nickname()), request.phoneNumber(), request.genderType().getGender(),
            request.birthday());
    }
}
