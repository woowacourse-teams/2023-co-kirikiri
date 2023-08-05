package co.kirikiri.service.dto.goalroom.request;

import org.springframework.web.multipart.MultipartFile;

public record CheckFeedRequest(
        MultipartFile image,
        String description
) {

}
