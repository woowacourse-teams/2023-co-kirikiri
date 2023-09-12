package co.kirikiri.domain.member;

import co.kirikiri.domain.BaseUpdatedTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberProfile extends BaseUpdatedTimeEntity {

    @Enumerated(value = EnumType.STRING)
    @Column(length = 10, nullable = false)
    private Gender gender;

    // todo: 기본값 모두 수정 후 nullable = false로 변경
    @Column(length = 100)
    private String email;

    public MemberProfile(final Gender gender, final String email) {
        this.gender = gender;
        this.email = email;
    }

    public Gender getGender() {
        return gender;
    }

    public String getEmail() {
        return email;
    }
}
