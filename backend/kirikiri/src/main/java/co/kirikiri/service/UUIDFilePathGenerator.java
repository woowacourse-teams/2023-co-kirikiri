package co.kirikiri.service;

import co.kirikiri.exception.BadRequestException;
import co.kirikiri.exception.ServerException;
import org.springframework.stereotype.Component;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class UUIDFilePathGenerator implements FilePathGenerator {

    private static final String DIRECTORY_SEPARATOR = "/";
    private static final String UUID_ORIGINAL_FILE_NAME_SEPARATOR = "_";
    private static final String CHARSET = "UTF-8";
    private static final String IMAGE_TYPE_REGEX = "\\.";
    private static final String FILE_SEPARATOR = ".";

    @Override
    public String makeFilePath(final ImageDirType dirType, final String originalFileName) {
        final LocalDate currentDate = LocalDate.now();
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                String.format("yyyy%sMMdd", DIRECTORY_SEPARATOR));
        final String dateString = currentDate.format(formatter);
        return makePath(dirType, originalFileName, dateString);
    }

    private String makePath(final ImageDirType dirType, final String originalFileName, final String dateString) {
        return DIRECTORY_SEPARATOR + dateString
                + DIRECTORY_SEPARATOR + dirType.getDirName()
                + DIRECTORY_SEPARATOR + UUID.randomUUID()
                + UUID_ORIGINAL_FILE_NAME_SEPARATOR + encodeOriginalFileName(originalFileName);
    }

    private String encodeOriginalFileName(final String originalFileName) {
        try {
            final int fileSeparatorIndex = originalFileName.lastIndexOf(FILE_SEPARATOR);
            final String path = originalFileName.substring(0, fileSeparatorIndex);
            final String imageType = originalFileName.substring(fileSeparatorIndex);
            return URLEncoder.encode(path, CHARSET) + imageType;
        } catch (final UnsupportedEncodingException e) {
            throw new ServerException(CHARSET + "은 지원되지 않는 인코딩 방식입니다.");
        } catch (final IndexOutOfBoundsException e) {
            throw new BadRequestException("원본 파일 이름에 확장자가 포함되지 않았습니다.");
        }
    }
}
