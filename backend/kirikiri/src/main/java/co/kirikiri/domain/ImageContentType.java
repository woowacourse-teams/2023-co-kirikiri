package co.kirikiri.domain;

import co.kirikiri.exception.BadRequestException;
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

    public static ImageContentType of(final String extension) {
        return Arrays.stream(values())
                .filter(type -> type.extension.equals(extension))
                .findAny()
                .orElseThrow(() -> new BadRequestException(extension + "는 요청할 수 없는 파일 확장자 형식입니다."));
    }
}
