package co.kirikiri.persistence.auth;

import co.kirikiri.domain.auth.RefreshToken;
import co.kirikiri.domain.auth.vo.EncryptedToken;

import java.util.Optional;

public interface RefreshTokenQueryRepository {

    Optional<RefreshToken> findByTokenAndIsRevokedFalse(final EncryptedToken token);
}
