package co.kirikiri.domain.auth;

import co.kirikiri.domain.auth.vo.EncryptedToken;
import co.kirikiri.domain.member.Member;
import jakarta.persistence.*;
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
    private boolean isRevoked = false;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

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
