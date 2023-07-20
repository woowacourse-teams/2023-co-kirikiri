package co.kirikiri.service;

import co.kirikiri.domain.member.EncryptedPassword;
import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;
import co.kirikiri.exception.ConflictException;
import co.kirikiri.persistence.member.MemberProfileRepository;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.service.dto.member.GenderType;
import co.kirikiri.service.dto.member.request.MemberJoinRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private MemberProfileRepository memberProfileRepository;
    @InjectMocks
    private MemberService memberService;

    @Test
    void 회원가입을_한다() {
        //given
        final MemberJoinRequest request = new MemberJoinRequest("identifier1", "password1!", "nickname",
                "010-1234-5678", GenderType.MALE, LocalDate.now());

        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.empty());
        given(memberProfileRepository.findByNickname(any()))
                .willReturn(Optional.empty());

        //when
        //then
        assertDoesNotThrow(() -> memberService.join(request));
    }

    @Test
    void 회원가입_시_이미_존재하는_아이디가_존재할때_예외를_던진다() {
        //given
        final MemberJoinRequest request = new MemberJoinRequest("identifier1", "password1!", "nickname",
                "010-1234-5678", GenderType.MALE, LocalDate.now());
        final Identifier identifier = new Identifier("identifier1");
        final Password password = new Password("password1!");
        final Nickname nickname = new Nickname("nickname");
        final String phoneNumber = "010-1234-5678";

        final Member member = new Member(identifier, new EncryptedPassword(password),
                new MemberProfile(Gender.MALE, LocalDate.now(), nickname, phoneNumber));
        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(member));

        //when
        //then
        assertThatThrownBy(() -> memberService.join(request))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void 회원가입_시_이미_존재하는_닉네임_존재할때_예외를_던진다() {
        //given
        final MemberJoinRequest request = new MemberJoinRequest("identifier1", "password1!", "nickname",
                "010-1234-5678", GenderType.MALE, LocalDate.now());
        final Nickname nickname = new Nickname("nickname");
        final String phoneNumber = "010-1234-5678";

        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, LocalDate.now(), nickname, phoneNumber);
        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.empty());
        given(memberProfileRepository.findByNickname(any()))
                .willReturn(Optional.of(memberProfile));

        //when
        //then
        assertThatThrownBy(() -> memberService.join(request))
                .isInstanceOf(ConflictException.class);
    }
}
