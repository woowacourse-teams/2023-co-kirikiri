package co.kirikiri.service;

import co.kirikiri.service.dto.FileInformation;
import org.springframework.http.HttpMethod;

import java.net.URL;

public interface FileService {

    void save(final String path, final FileInformation fileInformation);

    URL generateUrl(final String path, final HttpMethod httpMethod);
}
