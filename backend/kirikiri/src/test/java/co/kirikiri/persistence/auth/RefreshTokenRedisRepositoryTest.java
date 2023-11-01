package co.kirikiri.persistence.auth;

import static org.assertj.core.api.Assertions.assertThat;

import co.kirikiri.domain.auth.RefreshToken;
import co.kirikiri.domain.member.EncryptedPassword;
import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;
import co.kirikiri.persistence.helper.RedisRepositoryTest;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@RedisRepositoryTest
class RefreshTokenRedisRepositoryTest {

    private static Member member;

    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    public RefreshTokenRedisRepositoryTest(final RefreshTokenRedisRepository refreshTokenRedisRepository) {
        this.refreshTokenRedisRepository = refreshTokenRedisRepository;
    }

    @BeforeAll
    static void setUp() {
        final Identifier identifier = new Identifier("identifier1");
        final Password password = new Password("password1!");
        final EncryptedPassword encryptedPassword = new EncryptedPassword(password);
        final Nickname nickname = new Nickname("nickname");
        final String email = "kirikiri1@email.com";
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, email);
        member = new Member(identifier, encryptedPassword, nickname, null, memberProfile);
    }

    @Test
    void 정상적으로_리프레시_토큰을_찾아온다() {
        //given
        final String rawToken = "refreshToken";
        final Long expirationInSeconds = 100L;
        final String memberIdentifier = member.getIdentifier().getValue();

        final RefreshToken refreshToken = new RefreshToken(rawToken, expirationInSeconds, memberIdentifier);
        refreshTokenRedisRepository.save(refreshToken);

        //when
        final RefreshToken findRefreshToken = refreshTokenRedisRepository.findById(refreshToken.getRefreshToken())
                .get();

        //then
        assertThat(findRefreshToken.getRefreshToken())
                .isEqualTo(refreshToken.getRefreshToken());
        assertThat(findRefreshToken.getMemberIdentifier())
                .isEqualTo(memberIdentifier);
    }

    @Test
    void 정상적으로_리프레시_토큰을_삭제한다() {
        //given
        final String rawToken = "refreshToken";
        final Long expirationInSeconds = 100L;
        final String memberIdentifier = member.getIdentifier().getValue();

        final RefreshToken refreshToken = new RefreshToken(rawToken, expirationInSeconds, memberIdentifier);
        refreshTokenRedisRepository.save(refreshToken);

        //when
        refreshTokenRedisRepository.delete(refreshToken);
        final Optional<RefreshToken> findRefreshToken = refreshTokenRedisRepository.findById(
                refreshToken.getRefreshToken());

        //then
        assertThat(findRefreshToken).isEmpty();
    }
}
