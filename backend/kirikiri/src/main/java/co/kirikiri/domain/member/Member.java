package co.kirikiri.domain.member;

import co.kirikiri.domain.BaseTimeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, unique = true, nullable = false)
    private String identifier;

    @Column(nullable = false)
    private String password;

    @OneToOne(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            orphanRemoval = true)
    @JoinColumn(name = "member_profile_id", nullable = false, unique = true)
    private MemberProfile memberProfile;

    public Member(final String identifier, final String password, final MemberProfile memberProfile) {
        this.identifier = identifier;
        this.password = password;
        this.memberProfile = memberProfile;
    }
}
