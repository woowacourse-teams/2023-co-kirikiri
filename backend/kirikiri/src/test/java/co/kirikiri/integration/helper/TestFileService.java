package co.kirikiri.integration.helper;

import co.kirikiri.service.FileService;
import org.springframework.http.HttpMethod;
import org.springframework.web.multipart.MultipartFile;
import java.net.MalformedURLException;
import java.net.URL;

public class TestFileService implements FileService {

    @Override
    public void save(final String path, final MultipartFile multiPartFile) {
    }

    @Override
    public URL generateUrl(final String path, final HttpMethod httpMethod) {
        try {
            return new URL("http://example.com/" + path);
        } catch (final MalformedURLException exception) {
            throw new RuntimeException(exception);
        }
    }
}
