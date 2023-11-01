package co.kirikiri.domain;

import co.kirikiri.service.exception.BadRequestException;
import java.util.Arrays;

public enum ImageContentType {

    JPG("image/jpg"),
    JPEG("image/jpeg"),
    PNG("image/png"),
    WEBP("image/webp"),
    ;

    private final String extension;

    ImageContentType(final String extension) {
        this.extension = extension;
    }

    public static ImageContentType findImageContentType(final String imageContentType) {
        return Arrays.stream(values())
                .filter(it -> it.extension.equals(imageContentType))
                .findAny()
                .orElseThrow(() -> new BadRequestException("허용되지 않는 확장자입니다."));
    }
}
