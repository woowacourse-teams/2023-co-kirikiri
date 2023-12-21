package co.kirikiri.checkfeed.service.dto.request;

import org.springframework.web.multipart.MultipartFile;

public record CheckFeedRequest(
        MultipartFile image,
        String description
) {

}
