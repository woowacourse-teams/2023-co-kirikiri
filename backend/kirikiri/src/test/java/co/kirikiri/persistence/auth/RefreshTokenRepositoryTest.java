package co.kirikiri.persistence.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

import co.kirikiri.domain.member.EncryptedPassword;
import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
class RefreshTokenRepositoryTest {

    private static Member member;

    private static final Long refreshTokenValidityInSeconds = 3600000L;

    @Mock
    private RedisTemplate<String, String> redisTemplateMock;

    @Mock
    private ValueOperations<String, String> valueOperationsMock;

    private RefreshTokenRepositoryImpl refreshTokenRepository;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplateMock.opsForValue())
                .thenReturn(valueOperationsMock);
        refreshTokenRepository = new RefreshTokenRepositoryImpl(redisTemplateMock, refreshTokenValidityInSeconds);
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

        when(valueOperationsMock.get(refreshToken))
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
