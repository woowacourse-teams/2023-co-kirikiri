package co.kirikiri.integration.helper;

import co.kirikiri.common.service.FileService;
import co.kirikiri.common.service.dto.FileInformation;
import java.net.MalformedURLException;
import java.net.URL;
import org.springframework.http.HttpMethod;

public class TestFileService implements FileService {

    @Override
    public void save(final String path, final FileInformation fileInformation) {
    }

    @Override
    public URL generateUrl(final String path, final HttpMethod httpMethod) {
        try {
            return new URL("http://example.com" + path);
        } catch (final MalformedURLException exception) {
            throw new RuntimeException(exception);
        }
    }
}
