package co.kirikiri.member.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import co.kirikiri.common.type.ImageContentType;
import co.kirikiri.member.domain.EncryptedPassword;
import co.kirikiri.member.domain.Gender;
import co.kirikiri.member.domain.Member;
import co.kirikiri.member.domain.MemberImage;
import co.kirikiri.member.domain.MemberProfile;
import co.kirikiri.member.domain.vo.Identifier;
import co.kirikiri.member.domain.vo.Nickname;
import co.kirikiri.member.domain.vo.Password;
import co.kirikiri.persistence.helper.RepositoryTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.Optional;

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
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, "kirikiri1@email.com");
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
    void 사용쟈의_아이디로_사용자의_프로필과_이미지를_함께_조회한다() {
        // given
        final Member savedMember = memberRepository.save(member);

        // when
        final Member findMember = memberRepository.findWithMemberProfileAndImageByIdentifier(
                savedMember.getIdentifier().getValue()).get();

        // then
        final MemberProfile memberProfile = findMember.getMemberProfile();
        final MemberImage memberImage = findMember.getImage();

        assertAll(
                () -> assertThat(member.getIdentifier().getValue()).isEqualTo("identifier1"),
                () -> assertThat(memberProfile.getGender()).isEqualTo(Gender.MALE),
                () -> assertThat(memberProfile.getEmail()).isEqualTo("kirikiri1@email.com"),
                () -> assertThat(memberImage.getServerFilePath()).isEqualTo("serverFilePath")
        );
    }

    @Test
    void 식별자_아이디로_사용자의_프로필과_이미지를_함께_조회한다() {
        // given
        final Member savedMember = memberRepository.save(member);

        // when
        final Member findMember = memberRepository.findWithMemberProfileAndImageById(savedMember.getId()).get();

        // then
        final MemberProfile memberProfile = findMember.getMemberProfile();
        final MemberImage memberImage = findMember.getImage();

        assertAll(
                () -> assertThat(member.getIdentifier().getValue()).isEqualTo("identifier1"),
                () -> assertThat(memberProfile.getGender()).isEqualTo(Gender.MALE),
                () -> assertThat(memberProfile.getEmail()).isEqualTo("kirikiri1@email.com"),
                () -> assertThat(memberImage.getServerFilePath()).isEqualTo("serverFilePath")
        );
    }
}
