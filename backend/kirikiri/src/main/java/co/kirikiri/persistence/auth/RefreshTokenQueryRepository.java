package co.kirikiri.persistence.auth;

import co.kirikiri.domain.auth.EncryptedToken;
import co.kirikiri.domain.auth.RefreshToken;
import java.util.Optional;

public interface RefreshTokenQueryRepository {

    Optional<RefreshToken> findByTokenAndIsRevokedFalse(final EncryptedToken token);
}
