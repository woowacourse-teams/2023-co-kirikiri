package co.kirikiri.persistence.auth;

import static co.kirikiri.domain.auth.QRefreshToken.refreshToken;
import static co.kirikiri.domain.member.QMember.member;

import co.kirikiri.domain.auth.EncryptedToken;
import co.kirikiri.domain.auth.RefreshToken;
import co.kirikiri.persistence.QuerydslRepositorySupporter;
import java.util.Optional;

public class RefreshTokenQueryRepositoryImpl extends QuerydslRepositorySupporter implements
        RefreshTokenQueryRepository {

    public RefreshTokenQueryRepositoryImpl() {
        super(RefreshToken.class);
    }

    @Override
    public Optional<RefreshToken> findByTokenAndIsRevokedFalse(final EncryptedToken token) {

        return Optional.ofNullable(selectFrom(refreshToken)
                .join(refreshToken.member, member)
                .fetchJoin()
                .where(refreshToken.token.eq(token))
                .where(refreshToken.isRevoked.isFalse())
                .fetchOne());
    }
}
