package co.kirikiri.service.dto.member.request;

import co.kirikiri.service.dto.member.GenderType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public record JoinMemberRequest(
    @NotBlank
    String identifier,

    @NotBlank
    String password,

    @NotBlank
    String nickname,

    @NotBlank
    @Pattern(regexp = "^\\d{3}-\\d{4}-\\d{4}$", message = "전화번호 형식이 맞지 않습니다.")
    String phoneNumber,

    @NotBlank
    GenderType genderType,

    @NotBlank
    @DateTimeFormat(pattern = "yyMMdd")
    LocalDate birthday

) {

}
