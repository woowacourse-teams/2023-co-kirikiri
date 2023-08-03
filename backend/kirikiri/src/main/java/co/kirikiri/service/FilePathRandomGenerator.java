package co.kirikiri.service;

import co.kirikiri.domain.ImageDirType;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

@Component
public class FilePathRandomGenerator implements FilePathGenerator {

    private static final String DIRECTORY_SEPARATOR = "/";

    @Override
    public String makeFilePath(final Long id, final ImageDirType dirType) {
        final LocalDate currentDate = LocalDate.now();
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                String.format("yyyy%sMMdd", DIRECTORY_SEPARATOR));
        final String dateString = currentDate.format(formatter);

        return dateString + DIRECTORY_SEPARATOR + dirType.getDirName() + DIRECTORY_SEPARATOR + id
                + DIRECTORY_SEPARATOR + System.currentTimeMillis() + "_";
    }
}
