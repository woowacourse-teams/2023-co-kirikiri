package co.kirikiri.service.dto.member.request;

import co.kirikiri.service.dto.member.GenderType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

public record MemberJoinRequest(
    @NotBlank(message = "아이디는 빈 값일 수 없습니다.")
    String identifier,

    @NotBlank(message = "비밀번호는 빈 값일 수 없습니다.")
    String password,

    @NotBlank(message = "닉네임은 빈 값일 수 없습니다.")
    String nickname,

    @NotBlank(message = "전화번호는 빈 값일 수 없습니다.")
    @Pattern(regexp = "^\\d{3}-\\d{4}-\\d{4}$", message = "전화번호 형식이 맞지 않습니다.")
    String phoneNumber,

    GenderType genderType,

    @JsonFormat(pattern = "yyMMdd")
    LocalDate birthday
) {

}
