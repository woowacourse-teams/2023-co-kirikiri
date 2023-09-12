package co.kirikiri.domain.auth;

import co.kirikiri.domain.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private EncryptedToken token;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    @Column(nullable = false)
    private final boolean isRevoked = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public RefreshToken(final EncryptedToken token, final LocalDateTime expiredAt, final Member member) {
        this.token = token;
        this.expiredAt = expiredAt;
        this.member = member;
    }

    public boolean isExpired() {
        return expiredAt.isBefore(LocalDateTime.now());
    }

    public EncryptedToken getToken() {
        return token;
    }

    public Member getMember() {
        return member;
    }
}
