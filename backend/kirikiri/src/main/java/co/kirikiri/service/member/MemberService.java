package co.kirikiri.service.member;

import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.vo.EncryptedPassword;
import co.kirikiri.exception.ConflictException;
import co.kirikiri.persistence.member.MemberProfileRepository;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.service.dto.member.JoinMemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberProfileRepository memberProfileRepository;

    public void join(final JoinMemberDto joinMemberDto) {
        checkDuplicate(joinMemberDto);
        final EncryptedPassword encryptedPassword = new EncryptedPassword(joinMemberDto.getPassword());
        final MemberProfile memberProfile = new MemberProfile(joinMemberDto.getGender(), joinMemberDto.getBirthday(),
            joinMemberDto.getNickname(), joinMemberDto.getPhoneNumber());
        final Member member = new Member(joinMemberDto.getIdentifier(), encryptedPassword, memberProfile);
        memberRepository.save(member);
    }

    private void checkDuplicate(final JoinMemberDto joinMemberDto) {
        if (memberRepository.findByIdentifier(joinMemberDto.getIdentifier()).isPresent()) {
            throw new ConflictException("이미 존재하는 아이디입니다.");
        }
        if (memberProfileRepository.findByNickname(joinMemberDto.getNickname()).isPresent()) {
            throw new ConflictException("이미 존재하는 닉네임입니다.");
        }
    }
}
