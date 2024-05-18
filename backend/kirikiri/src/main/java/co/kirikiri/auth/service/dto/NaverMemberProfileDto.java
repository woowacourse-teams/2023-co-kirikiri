package co.kirikiri.auth.service.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(value = PropertyNamingStrategies.LowerCaseStrategy.class)
public record NaverMemberProfileDto(
        String resultCode,
        String message,
        NaverMemberProfileResponseDto response
) {
}
