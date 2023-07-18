package co.kirikiri.persistence.auth;

import static org.assertj.core.api.Assertions.assertThat;

import co.kirikiri.domain.auth.EncryptedToken;
import co.kirikiri.domain.auth.RefreshToken;
import co.kirikiri.domain.member.EncryptedPassword;
import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;
import co.kirikiri.persistence.RepositoryTest;
import co.kirikiri.persistence.member.MemberRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class RefreshTokenRepositoryTest extends RepositoryTest {

    private static Member member;
    private static MemberProfile memberProfile;

    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;

    public RefreshTokenRepositoryTest(final RefreshTokenRepository refreshTokenRepository,
                                      final MemberRepository memberRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
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
    void 정상적으로_취소되지_않은_리프레시_토큰을_찾아온다() {
        //given
        final EncryptedToken encryptedToken = new EncryptedToken("refreshToken");
        final LocalDateTime now = LocalDateTime.now();
        final RefreshToken refreshToken = new RefreshToken(encryptedToken, now, member);
        memberRepository.save(member);
        refreshTokenRepository.save(refreshToken);

        //when
        final Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByTokenAndIsRevokedFalse(
                encryptedToken);

        //then
        assertThat(optionalRefreshToken).isNotEmpty();
        final RefreshToken result = optionalRefreshToken.get();
        assertThat(result.getToken()).usingRecursiveComparison()
                .isEqualTo(encryptedToken);
    }
}
