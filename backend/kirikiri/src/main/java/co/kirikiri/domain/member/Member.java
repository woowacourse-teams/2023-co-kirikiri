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
        this(null, identifier, encryptedPassword, nickname, null, memberProfile);
    }

    public Member(final Identifier identifier, final EncryptedPassword encryptedPassword, final Nickname nickname,
                  final MemberImage image, final MemberProfile memberProfile) {
        this(null, identifier, encryptedPassword, nickname, image, memberProfile);
    }

    public Member(final Long id, final Identifier identifier, final EncryptedPassword encryptedPassword,
                  final Nickname nickname, final MemberProfile memberProfile) {
        this(id, identifier, encryptedPassword, nickname, null, memberProfile);
    }

    public Member(final Long id, final Identifier identifier, final EncryptedPassword encryptedPassword,
                  final Nickname nickname, final MemberImage image, final MemberProfile memberProfile) {
        this.id = id;
        this.identifier = identifier;
        this.encryptedPassword = encryptedPassword;
        this.nickname = nickname;
        this.image = image;
        this.memberProfile = memberProfile;
    }

    public boolean isPasswordMismatch(final Password password) {
        return this.encryptedPassword.isMismatch(password);
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public Nickname getNickname() {
        return nickname;
    }

    public MemberImage getImage() {
        return image;
    }

    public MemberProfile getMemberProfile() {
        return memberProfile;
    }
}
