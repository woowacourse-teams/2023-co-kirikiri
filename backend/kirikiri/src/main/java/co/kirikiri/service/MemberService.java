package co.kirikiri.service;

import co.kirikiri.domain.member.EncryptedPassword;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.exception.ConflictException;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.service.dto.member.MemberJoinDto;
import co.kirikiri.service.dto.member.request.MemberJoinRequest;
import co.kirikiri.service.dto.member.response.MemberMyInfoResponse;
import co.kirikiri.service.dto.member.response.MemberPublicInfoResponse;
import co.kirikiri.service.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public Long join(final MemberJoinRequest memberJoinRequest) {
        final MemberJoinDto memberJoinDto = MemberMapper.convertToMemberJoinDto(memberJoinRequest);
        checkIdentifierDuplicate(memberJoinDto.identifier());
        checkNicknameDuplicate(memberJoinDto.nickname());

        final EncryptedPassword encryptedPassword = new EncryptedPassword(memberJoinDto.password());
        final MemberProfile memberProfile = new MemberProfile(memberJoinDto.gender(),
                memberJoinDto.birthday(), memberJoinDto.phoneNumber());
        final Member member = new Member(memberJoinDto.identifier(), encryptedPassword, memberJoinDto.nickname(),
                memberProfile);
        return memberRepository.save(member).getId();
    }

    private void checkNicknameDuplicate(final Nickname nickname) {
        if (memberRepository.findByNickname(nickname).isPresent()) {
            throw new ConflictException("이미 존재하는 닉네임입니다.");
        }
    }

    private void checkIdentifierDuplicate(final Identifier identifier) {
        if (memberRepository.findByIdentifier(identifier).isPresent()) {
            throw new ConflictException("이미 존재하는 아이디입니다.");
        }
    }

    public MemberMyInfoResponse findMyInfo(final String identifier) {
        final Member member = findMemberByIdentifier(identifier);
        final Member memberWithInfo = memberRepository.findWithMemberProfileAndImageById(member.getId()).get();
        return MemberMapper.convertToMemberMyInfoResponse(memberWithInfo);
    }

    private Member findMemberByIdentifier(final String identifier) {
        return memberRepository.findByIdentifier(new Identifier(identifier))
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));
    }

    public MemberPublicInfoResponse findMemberPublicInfo(final String identifier, final Long memberId) {
        findMemberByIdentifier(identifier);
        final Member memberWithPublicInfo = findMemberInfoByMemberId(memberId);
        return MemberMapper.convertToMemberPublicInfoResponse(memberWithPublicInfo);
    }

    private Member findMemberInfoByMemberId(final Long memberId) {
        return memberRepository.findWithMemberProfileAndImageById(memberId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다. memberId = " + memberId));
    }
}
