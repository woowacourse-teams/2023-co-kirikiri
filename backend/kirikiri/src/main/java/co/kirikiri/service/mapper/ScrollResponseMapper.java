package co.kirikiri.service.mapper;

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ScrollResponseMapper {

    public static <T> List<T> getSubResponses(final List<T> responses, final int requestSize) {
        final int endIndex = Math.min(responses.size(), requestSize);
        return responses.subList(0, endIndex);
    }

    public static boolean hasNext(final int responseSize, final int requestSize) {
        return responseSize > requestSize;
    }
}
