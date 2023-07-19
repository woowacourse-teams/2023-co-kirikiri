package co.kirikiri.persistence.member;

import co.kirikiri.domain.member.EncryptedPassword;
import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;
import co.kirikiri.persistence.helper.RepositoryTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
class MemberProfileRepositoryTest {

    private static Member member;
    private static MemberProfile memberProfile;

    private final MemberProfileRepository memberProfileRepository;
    private final MemberRepository memberRepository;

    public MemberProfileRepositoryTest(final MemberProfileRepository memberProfileRepository,
                                       final MemberRepository memberRepository) {
        this.memberProfileRepository = memberProfileRepository;
        this.memberRepository = memberRepository;
    }

    @BeforeAll
    static void setUp() {
        final Identifier identifier = new Identifier("identifier1");
        final Password password = new Password("password1!");
        final EncryptedPassword encryptedPassword = new EncryptedPassword(password);
        final Nickname nickname = new Nickname("nickname");
        final String phoneNumber = "010-1234-5678";
        memberProfile = new MemberProfile(Gender.MALE, LocalDate.now(), nickname, phoneNumber);
        member = new Member(identifier, encryptedPassword, memberProfile);
    }

    @Test
    void 닉네임으로_회원프로필을_조회한다() {
        //given
        memberRepository.save(member);

        //when
        final Optional<MemberProfile> optionalMemberProfile = memberProfileRepository.findByNickname(
                new Nickname("nickname"));

        //then
        assertThat(optionalMemberProfile).isNotEmpty();
        final MemberProfile memberProfile = optionalMemberProfile.get();
        final Nickname nickname = new Nickname("nickname");
        final String phoneNumber = "010-1234-5678";
        final MemberProfile expectedMemberProfile = new MemberProfile(Gender.MALE, LocalDate.now(), nickname,
                phoneNumber);
        assertThat(memberProfile).usingRecursiveComparison()
                .ignoringFields("id", "createdAt", "updatedAt")
                .isEqualTo(expectedMemberProfile);
    }
}
