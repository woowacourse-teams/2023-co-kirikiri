package co.kirikiri.auth.service.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @NotBlank(message = "아이디는 빈 값일 수 없습니다.")
        String identifier,

        @NotBlank(message = "비밀번호는 빈 값일 수 없습니다.")
        String password
) {

}
