package co.kirikiri.service.dto.member.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MemberResponse(
        long id,
        String name,
        String imageUrl
) {

    public MemberResponse(final long id, final String name) {
        this(id, name, null);
    }
}
