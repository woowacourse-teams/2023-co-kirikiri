package co.kirikiri.common.dto;

import java.io.InputStream;

public record FileInformation(
        String originalFileName,
        long size,
        String contentType,
        InputStream inputStream
) {

}
