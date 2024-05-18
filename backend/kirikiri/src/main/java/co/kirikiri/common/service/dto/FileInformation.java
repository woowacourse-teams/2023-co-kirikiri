package co.kirikiri.common.service.dto;

import co.kirikiri.common.exception.ServerException;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.web.multipart.MultipartFile;

public record FileInformation(
        String originalFileName,
        long size,
        String contentType,
        InputStream inputStream
) {

    public static FileInformation from(final MultipartFile multipartFile) {
        try {
            return new FileInformation(multipartFile.getOriginalFilename(), multipartFile.getSize(),
                    multipartFile.getContentType(), multipartFile.getInputStream());
        } catch (final IOException exception) {
            throw new ServerException(exception.getMessage());
        }
    }
}
