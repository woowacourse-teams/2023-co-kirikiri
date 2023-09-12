package co.kirikiri.service.dto.auth;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(value = PropertyNamingStrategies.LowerCaseStrategy.class)
public record NaverMemberProfileResponseDto(
        String id,
        String email,
        String nickname,
        String gender
) {
}
