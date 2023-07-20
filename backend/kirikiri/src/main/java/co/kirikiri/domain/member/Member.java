package co.kirikiri.domain.member;

import co.kirikiri.domain.BaseTimeEntity;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Identifier identifier;

    @Embedded
    private EncryptedPassword encryptedPassword;

    @OneToOne(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            orphanRemoval = true)
    @JoinColumn(name = "member_profile_id", nullable = false, unique = true)
    private MemberProfile memberProfile;

    public Member(final Identifier identifier, final EncryptedPassword encryptedPassword,
                  final MemberProfile memberProfile) {
        this.identifier = identifier;
        this.encryptedPassword = encryptedPassword;
        this.memberProfile = memberProfile;
    }

    public boolean isPasswordMismatch(final Password password) {
        return this.encryptedPassword.isMismatch(password);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Member member = (Member) o;
        return Objects.equals(id, member.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public Long getId() {
        return id;
    }

    public Nickname getNickname() {
        return memberProfile.getNickname();
    }
}
