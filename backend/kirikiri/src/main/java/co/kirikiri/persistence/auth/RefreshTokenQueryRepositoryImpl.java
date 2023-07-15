package co.kirikiri.persistence.auth;

import co.kirikiri.domain.auth.QRefreshToken;
import co.kirikiri.domain.auth.RefreshToken;
import co.kirikiri.domain.auth.vo.EncryptedToken;
import co.kirikiri.domain.member.QMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.Optional;

public class RefreshTokenQueryRepositoryImpl implements RefreshTokenQueryRepository {

    private final JPAQueryFactory queryFactory;

    public RefreshTokenQueryRepositoryImpl(final EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Optional<RefreshToken> findByTokenAndIsRevokedFalse(final EncryptedToken token) {
        final RefreshToken refreshToken = queryFactory
                .selectFrom(QRefreshToken.refreshToken)
                .join(QRefreshToken.refreshToken.member, QMember.member)
                .fetchJoin()
                .where(QRefreshToken.refreshToken.token.eq(token))
                .where(QRefreshToken.refreshToken.isRevoked.isFalse())
                .fetchOne();
        return Optional.ofNullable(refreshToken);
    }
}
