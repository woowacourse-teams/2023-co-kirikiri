package co.kirikiri.persistence.auth;

import co.kirikiri.domain.auth.RefreshToken;
import co.kirikiri.domain.auth.vo.EncryptedToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenAndIsRevokedFalse(final EncryptedToken token);

}
