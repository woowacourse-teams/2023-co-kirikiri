package co.kirikiri.domain.member;

import co.kirikiri.domain.BaseTimeEntity;
import co.kirikiri.domain.member.vo.Nickname;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberProfile extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    @Column(length = 10, nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private LocalDate birthday;

    @Embedded
    private Nickname nickname;

    @Column(length = 20, nullable = false)
    private String phoneNumber;

    @OneToOne(fetch = FetchType.LAZY,
        cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
        orphanRemoval = true)
    @JoinColumn(name = "member_profile_image_id")
    private MemberProfileImage image;

    public MemberProfile(final Gender gender, final LocalDate birthday, final Nickname nickname,
                         final String phoneNumber) {
        this(gender, birthday, nickname, phoneNumber, null);
    }

    public MemberProfile(final Gender gender, final LocalDate birthday, final Nickname nickname,
                         final String phoneNumber, final MemberProfileImage image) {
        this.gender = gender;
        this.birthday = birthday;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.image = image;
    }

    public String getNickname() {
        return nickname.getValue();
    }
}
