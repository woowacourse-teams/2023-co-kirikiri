package co.kirikiri.service.member;

import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.vo.EncryptedPassword;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.exception.ConflictException;
import co.kirikiri.persistence.member.MemberProfileRepository;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.service.dto.member.JoinMemberDto;
import co.kirikiri.service.dto.member.request.JoinMemberRequest;
import co.kirikiri.service.mapper.member.JoinMemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberProfileRepository memberProfileRepository;

    public void join(final JoinMemberRequest joinMemberRequest) {
        final JoinMemberDto joinMemberDto = JoinMemberMapper.convert(joinMemberRequest);
        checkDuplicate(joinMemberDto.identifier(), joinMemberDto.nickname());
        final EncryptedPassword encryptedPassword = new EncryptedPassword(joinMemberDto.password());
        final MemberProfile memberProfile = new MemberProfile(joinMemberDto.gender(), joinMemberDto.birthday(),
            joinMemberDto.nickname(), joinMemberDto.phoneNumber());
        final Member member = new Member(joinMemberDto.identifier(), encryptedPassword, memberProfile);
        memberRepository.save(member);
    }

    private void checkDuplicate(final Identifier identifier, final Nickname nickname) {
        if (memberRepository.findByIdentifier(identifier).isPresent()) {
            throw new ConflictException("이미 존재하는 아이디입니다.");
        }
        if (memberProfileRepository.findByNickname(nickname).isPresent()) {
            throw new ConflictException("이미 존재하는 닉네임입니다.");
        }
    }
}
