package co.kirikiri.member.service.dto.response;

import java.time.LocalDate;

public record MemberCheckFeedResponse(
        Long id,
        String imageUrl,
        String description,
        LocalDate createdAt
) {
}
