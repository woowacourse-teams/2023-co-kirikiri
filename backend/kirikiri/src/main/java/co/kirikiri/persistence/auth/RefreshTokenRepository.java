package co.kirikiri.persistence.auth;

import co.kirikiri.domain.auth.RefreshToken;
import co.kirikiri.domain.auth.vo.EncryptedToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenAndIsRevokedFalse(EncryptedToken token);
}
