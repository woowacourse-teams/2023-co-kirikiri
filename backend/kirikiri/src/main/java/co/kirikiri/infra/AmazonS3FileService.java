package co.kirikiri.infra;

import co.kirikiri.exception.ServerException;
import co.kirikiri.service.FileService;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

@Service
public class AmazonS3FileService implements FileService {

    private static final String ROOT_DIRECTORY_PROPERTY = "cloud.aws.s3.root-directory";
    private static final String SUB_DIRECTORY_PROPERTY = "cloud.aws.s3.sub-directory";
    private static final String BUCKET_PROPERTY = "cloud.aws.s3.bucket";
    private static final String EXPIRATION_PROPERTY = "cloud.aws.s3.url-expiration";
    private static final String DIRECTORY_SEPARATOR = "/";

    private final AmazonS3 amazonS3;
    private final Environment environment;

    public AmazonS3FileService(final AmazonS3 amazonS3, final Environment environment) {
        this.amazonS3 = amazonS3;
        this.environment = environment;
    }

    @Override
    public void save(final String path, final MultipartFile multiPartFile) {
        final String key = findProperty(ROOT_DIRECTORY_PROPERTY) + DIRECTORY_SEPARATOR
                + findProperty(SUB_DIRECTORY_PROPERTY) + path;
        final InputStream inputStream = getInputStream(multiPartFile);
        final ObjectMetadata objectMetadata = makeObjectMetadata(multiPartFile);
        putObjectToS3(key, inputStream, objectMetadata);
    }

    private String findProperty(final String property) {
        return environment.getProperty(property);
    }

    private InputStream getInputStream(final MultipartFile multiPartFile) {
        try {
            return multiPartFile.getInputStream();
        } catch (final IOException exception) {
            throw new ServerException(exception.getMessage());
        }
    }

    private ObjectMetadata makeObjectMetadata(final MultipartFile multiPartFile) {
        final ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multiPartFile.getSize());
        objectMetadata.setContentType(multiPartFile.getContentType());
        return objectMetadata;
    }

    private void putObjectToS3(final String key, final InputStream inputStream, final ObjectMetadata objectMetadata) {
        try {
            amazonS3.putObject(findProperty(BUCKET_PROPERTY), key, inputStream, objectMetadata);
        } catch (final SdkClientException sdkClientException) {
            throw new ServerException(sdkClientException.getMessage());
        }
    }

    @Override
    public URL generateUrl(final String path, final HttpMethod httpMethod) {
        final Date expiration = createExpiration(Long.parseLong(findProperty(EXPIRATION_PROPERTY)));
        final GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(findProperty(BUCKET_PROPERTY), path)
                        .withMethod(com.amazonaws.HttpMethod.valueOf(httpMethod.name()))
                        .withExpiration(expiration);
        return amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
    }

    private Date createExpiration(final Long validity) {
        final long now = new Date().getTime();
        return new Date(now + validity);
    }
}
