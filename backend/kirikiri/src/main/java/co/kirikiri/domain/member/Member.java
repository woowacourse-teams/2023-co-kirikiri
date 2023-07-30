package co.kirikiri.domain.member;

import co.kirikiri.domain.BaseUpdatedTimeEntity;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseUpdatedTimeEntity {

    @Embedded
    private Identifier identifier;

    @Embedded
    private EncryptedPassword encryptedPassword;

    @Embedded
    private Nickname nickname;

    @OneToOne(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            orphanRemoval = true)
    @JoinColumn(name = "member_image_id")
    private MemberImage image;

    @OneToOne(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            orphanRemoval = true)
    @JoinColumn(name = "member_profile_id", nullable = false, unique = true)
    private MemberProfile memberProfile;

    public Member(final Identifier identifier, final EncryptedPassword encryptedPassword,
                  final Nickname nickname, final MemberProfile memberProfile) {
        this(null, identifier, encryptedPassword, nickname, memberProfile);
    }

    public Member(final Long id, final Identifier identifier, final EncryptedPassword encryptedPassword,
                  final Nickname nickname, final MemberProfile memberProfile) {
        this.id = id;
        this.identifier = identifier;
        this.encryptedPassword = encryptedPassword;
        this.nickname = nickname;
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
        if (!super.equals(o)) {
            return false;
        }
        final Member member = (Member) o;
        return Objects.equals(identifier, member.identifier) && Objects.equals(nickname,
                member.nickname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), identifier, nickname);
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public Nickname getNickname() {
        return nickname;
    }
}