package co.kirikiri.service;

import co.kirikiri.service.exception.BadRequestException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class UUIDFilePathGenerator implements FilePathGenerator {

    private static final String DIRECTORY_SEPARATOR = "/";
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
                + makeFileExtension(originalFileName);
    }

    private String makeFileExtension(final String originalFileName) {
        try {
            final int fileSeparatorIndex = originalFileName.lastIndexOf(FILE_SEPARATOR);
            return originalFileName.substring(fileSeparatorIndex);
        } catch (final StringIndexOutOfBoundsException exception) {
            throw new BadRequestException("원본 파일이름에 확장자가 존재하지 않습니다");
        }
    }
}
