package co.kirikiri.persistence.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import co.kirikiri.domain.ImageContentType;
import co.kirikiri.domain.member.EncryptedPassword;
import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberImage;
import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;
import co.kirikiri.persistence.helper.RepositoryTest;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@RepositoryTest
class MemberRepositoryTest {

    private static Member member;

    private final MemberRepository memberRepository;

    public MemberRepositoryTest(final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @BeforeAll
    static void setUp() {
        final Identifier identifier = new Identifier("identifier1");
        final Password password = new Password("password1!");
        final EncryptedPassword encryptedPassword = new EncryptedPassword(password);
        final Nickname nickname = new Nickname("nickname");
        final String phoneNumber = "010-1234-5678";
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, LocalDate.now(), phoneNumber);
        final MemberImage memberImage = new MemberImage("originalFileName", "serverFilePath", ImageContentType.PNG);
        member = new Member(identifier, encryptedPassword, nickname, memberImage, memberProfile);
    }

    @Test
    void 아이디로_사용자를_찾는다() {
        //given
        memberRepository.save(member);

        //when
        final Optional<Member> optionalMember = memberRepository.findByIdentifier(new Identifier("identifier1"));

        //then
        assertThat(optionalMember).isNotEmpty();
    }

    @Test
    void 아이디로_사용자의_프로필과_이미지를_함께_조회한다() {
        // given
        final Member savedMember = memberRepository.save(member);

        // when
        final Member findMember = memberRepository.findWithMemberProfileAndImageById(savedMember.getId());

        // then
        final MemberProfile memberProfile = findMember.getMemberProfile();
        final MemberImage memberImage = findMember.getImage();

        assertAll(
                () -> assertThat(member.getIdentifier().getValue()).isEqualTo("identifier1"),
                () -> assertThat(memberProfile.getGender()).isEqualTo(Gender.MALE),
                () -> assertThat(memberProfile.getPhoneNumber()).isEqualTo("010-1234-5678"),
                () -> assertThat(memberProfile.getBirthday()).isEqualTo(LocalDate.now()),
                () -> assertThat(memberImage.getServerFilePath()).isEqualTo("serverFilePath")
        );
    }
}
