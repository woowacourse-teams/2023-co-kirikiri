package co.kirikiri.domain.member;

import co.kirikiri.domain.BaseCreatedTimeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberProfile extends BaseCreatedTimeEntity {

    @Enumerated(value = EnumType.STRING)
    @Column(length = 10, nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private LocalDate birthday;

    @Column(length = 20, nullable = false)
    private String phoneNumber;

    @OneToOne(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            orphanRemoval = true)
    @JoinColumn(name = "member_profile_image_id")
    private MemberImage image;

    public MemberProfile(final Gender gender, final LocalDate birthday, final String phoneNumber) {
        this.gender = gender;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
    }
}
