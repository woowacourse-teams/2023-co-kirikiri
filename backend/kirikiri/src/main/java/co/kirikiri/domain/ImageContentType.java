package co.kirikiri.domain;

import java.util.Arrays;
import java.util.Optional;

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

    public static Optional<ImageContentType> of(final String extension) {
        return Arrays.stream(values())
                .filter(type -> type.extension.equals(extension))
                .findAny();
    }
}
