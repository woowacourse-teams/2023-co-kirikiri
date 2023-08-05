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

    public static ImageContentType findByOriginalFileName(final String originalFilename) {
        final String extension = findExtension(originalFilename);
        return Arrays.stream(values())
                .filter(it -> it.extension.contains(extension))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("허용되지 않는 확장자입니다."));
    }

    private static String findExtension(final String originalFilename) {
        try {
            return originalFilename.substring(originalFilename.lastIndexOf('.') + 1);
        } catch (final StringIndexOutOfBoundsException exception) {
            throw new BadRequestException("파일 이름에 확장자가 포함되지 않았습니다.");
        }
    }
}
