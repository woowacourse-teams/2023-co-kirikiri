package co.kirikiri.service;

import org.springframework.http.HttpMethod;
import org.springframework.web.multipart.MultipartFile;
import java.net.URL;

public interface FileService {
    void save(final String path, final MultipartFile multiPartFile);

    URL generateUrl(final String path, final HttpMethod httpMethod);
}
