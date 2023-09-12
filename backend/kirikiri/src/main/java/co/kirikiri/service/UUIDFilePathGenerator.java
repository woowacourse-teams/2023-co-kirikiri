package co.kirikiri.service;

import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class UUIDFilePathGenerator implements FilePathGenerator {

    private static final String DIRECTORY_SEPARATOR = "/";
    private static final String UUID_ORIGINAL_FILE_NAME_SEPARATOR = "_";

    @Override
    public String makeFilePath(final ImageDirType dirType, final String originalFileName) {
        final LocalDate currentDate = LocalDate.now();
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                String.format("yyyy%sMMdd", DIRECTORY_SEPARATOR));
        final String dateString = currentDate.format(formatter);

        return DIRECTORY_SEPARATOR + dateString + DIRECTORY_SEPARATOR + dirType.getDirName()
                + DIRECTORY_SEPARATOR + UUID.randomUUID() + UUID_ORIGINAL_FILE_NAME_SEPARATOR + originalFileName;
    }
}
