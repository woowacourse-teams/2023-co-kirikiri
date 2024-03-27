package co.kirikiri.auth.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

import co.kirikiri.member.domain.EncryptedPassword;
import co.kirikiri.member.domain.Gender;
import co.kirikiri.member.domain.Member;
import co.kirikiri.member.domain.MemberProfile;
import co.kirikiri.member.domain.vo.Identifier;
import co.kirikiri.member.domain.vo.Nickname;
import co.kirikiri.member.domain.vo.Password;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class RefreshTokenRepositoryTest {

    private static final Long refreshTokenValidityInSeconds = 3600000L;

    private static Member member;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private RefreshTokenRepository refreshTokenRepository;

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

    @BeforeEach
    void init() {
        when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);
        refreshTokenRepository = new RefreshTokenRepositoryImpl(redisTemplate, refreshTokenValidityInSeconds);
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

        when(valueOperations.get(refreshToken))
                .thenReturn(memberIdentifier);

        //when
        final String findMemberIdentifier = refreshTokenRepository.findMemberIdentifierByRefreshToken(refreshToken)
                .get();

        //then
        assertThat(findMemberIdentifier).isEqualTo(memberIdentifier);
    }

    @Test
    void 리프레시_토큰을_찾을때_없는경우_빈값을_보낸다() {
        //given
        final String refreshToken = "refreshToken";

        //when
        final Optional<String> memberIdentifier = refreshTokenRepository.findMemberIdentifierByRefreshToken(
                refreshToken);
        //then
        assertThat(memberIdentifier).isEmpty();
    }
}
