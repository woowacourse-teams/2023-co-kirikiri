package co.kirikiri.integration.helper;

import co.kirikiri.common.dto.FileInformation;
import co.kirikiri.service.FileService;
import org.springframework.http.HttpMethod;

import java.net.MalformedURLException;
import java.net.URL;

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
