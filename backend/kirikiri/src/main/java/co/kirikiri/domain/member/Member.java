package co.kirikiri.domain.member;

import co.kirikiri.domain.BaseTimeEntity;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;
import jakarta.persistence.*;
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
