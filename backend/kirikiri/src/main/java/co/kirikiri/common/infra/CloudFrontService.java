package co.kirikiri.common.infra;

import co.kirikiri.common.exception.ServerException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import java.net.MalformedURLException;
import java.net.URL;

@Service
@RequiredArgsConstructor
public class CloudFrontService {

    private static final String CLOUD_FRONT_DISTRIBUTION_DOMAIN = "cloud.aws.cloud-front.distribution-domain";
    private static final String DIRECTORY_SEPARATOR = "/";

    private final Environment environment;

    public URL generateGetUrl(final String path) {
        final String cloudFrontDistributionDomain = findProperty(CLOUD_FRONT_DISTRIBUTION_DOMAIN);
        final String realPath = makeRealPath(path);
        final String policyResourcePath = cloudFrontDistributionDomain + realPath;
        return generateToUrl(policyResourcePath);
    }

    private String findProperty(final String property) {
        return environment.getProperty(property);
    }

    private String makeRealPath(final String path) {
        if (path.startsWith(DIRECTORY_SEPARATOR)) {
            return path;
        }
        return DIRECTORY_SEPARATOR + path;
    }

    private URL generateToUrl(final String url) {
        try {
            return new URL(url);
        } catch (final MalformedURLException exception) {
            throw new ServerException(exception.getMessage());
        }
    }
}
