package co.kirikiri.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

@Component
public class CheckFeedFilePathGenerator implements FilePathGenerator {

    private static final String DIRECTORY_SEPERATOR = "/";

    @Override
    public String makeFilePath(final Long goalRoomId) {
        final LocalDate currentDate = LocalDate.now();
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MMdd");
        final String dateString = currentDate.format(formatter);

        return dateString + DIRECTORY_SEPERATOR + goalRoomId + DIRECTORY_SEPERATOR;
    }
}
