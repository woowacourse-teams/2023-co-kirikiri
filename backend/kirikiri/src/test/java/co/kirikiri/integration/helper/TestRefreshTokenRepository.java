package co.kirikiri.integration.helper;

import static co.kirikiri.integration.fixture.MemberAPIFixture.DEFAULT_IDENTIFIER;

import co.kirikiri.auth.persistence.RefreshTokenRepository;
import java.util.Optional;

public class TestRefreshTokenRepository implements RefreshTokenRepository {

    @Override
    public void save(final String refreshToken, final String memberIdentifier) {
    }

    @Override
    public Optional<String> findMemberIdentifierByRefreshToken(final String refreshToken) {
        return Optional.of(DEFAULT_IDENTIFIER);
    }
}
