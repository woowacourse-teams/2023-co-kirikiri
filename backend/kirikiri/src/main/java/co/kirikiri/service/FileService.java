package co.kirikiri.service;

import java.net.URL;
import org.springframework.http.HttpMethod;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    void save(final String path, final MultipartFile multiPartFile);

    URL generateUrl(final String path, final HttpMethod httpMethod);
}
