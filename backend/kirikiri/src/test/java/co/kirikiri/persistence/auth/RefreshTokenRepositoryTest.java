package co.kirikiri.persistence.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import co.kirikiri.domain.member.EncryptedPassword;
import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;
import co.kirikiri.persistence.helper.RedisRepositoryTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

@RedisRepositoryTest
class RefreshTokenRepositoryTest {

    private static Member member;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private RefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    void init() {
        refreshTokenRepository = new RefreshTokenRepository(redisTemplate, 2000L);
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
    void 정상적으로_리프레시_토큰을_저장한다() {
        //given
        final String refreshToken = "refreshToken";
        final String memberIdentifier = member.getIdentifier().getValue();

        //when
        //then
        assertDoesNotThrow(() -> refreshTokenRepository.save(refreshToken, memberIdentifier));
    }

    @Test
    void 정상적으로_리프레시_토큰을_찾아온다() {
        //given
        final String refreshToken = "refreshToken";
        final String memberIdentifier = member.getIdentifier().getValue();

        refreshTokenRepository.save(refreshToken, memberIdentifier);

        //when
        final String findMemberIdentifier = refreshTokenRepository.findMemberIdentifierByRefreshToken(refreshToken)
                .get();

        //then
        assertThat(findMemberIdentifier).isEqualTo(memberIdentifier);
    }
}
