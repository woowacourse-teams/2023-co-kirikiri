package co.kirikiri.service.dto.member;

import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JoinMemberDto {

    private Identifier identifier;

    private Password password;

    private Nickname nickname;

    private String phoneNumber;

    private Gender gender;

    private LocalDate birthday;
}
