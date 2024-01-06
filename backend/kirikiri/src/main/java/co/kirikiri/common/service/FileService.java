package co.kirikiri.common.service;

import co.kirikiri.service.dto.FileInformation;
import java.net.URL;
import org.springframework.http.HttpMethod;

public interface FileService {

    void save(final String path, final FileInformation fileInformation);

    URL generateUrl(final String path, final HttpMethod httpMethod);
}
