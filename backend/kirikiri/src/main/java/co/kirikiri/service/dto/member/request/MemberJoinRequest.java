package co.kirikiri.service.dto.member.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record MemberJoinRequest(

        @Pattern(regexp = "^[a-z0-9]+$", message = "형식에 맞지 않는 아이디입니다.")
        String identifier,

        @NotBlank(message = "비밀번호는 빈 값일 수 없습니다.")
        String password,

        @NotBlank(message = "닉네임은 빈 값일 수 없습니다.")
        String nickname,

        @NotNull(message = "성별은 빈 값일 수 없습니다.")
        GenderType genderType,

        @NotBlank(message = "이메일은 빈 값일 수 없습니다.")
        @Email(message = "이메일 형식이 아닙니다.")
        String email
) {

}
