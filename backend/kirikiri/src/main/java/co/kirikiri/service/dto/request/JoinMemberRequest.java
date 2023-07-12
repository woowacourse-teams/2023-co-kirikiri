package co.kirikiri.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JoinMemberRequest {

    @NotBlank
    private String identifier;

    @NotBlank
    private String password;

    @NotBlank
    private String nickname;

    @NotBlank
    private Gender gender;

    @NotBlank
    @DateTimeFormat(pattern = "yyMMdd")
    private LocalDate birthday;
}
