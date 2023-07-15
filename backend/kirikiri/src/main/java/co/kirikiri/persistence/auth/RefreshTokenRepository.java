package co.kirikiri.persistence.auth;

import co.kirikiri.domain.auth.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>, RefreshTokenQueryRepository {
}
