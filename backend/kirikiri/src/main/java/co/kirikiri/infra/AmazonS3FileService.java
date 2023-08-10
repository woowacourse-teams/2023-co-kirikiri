package co.kirikiri.infra;

import co.kirikiri.exception.ServerException;
import co.kirikiri.service.FileService;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;

@Service
public class AmazonS3FileService implements FileService {

    private static final String DIRECTORY_SEPARATOR = "/";

    private final AmazonS3 amazonS3;
    private final String bucket;
    private final String rootDirectory;
    private final String subDirectory;

    public AmazonS3FileService(final AmazonS3 amazonS3, @Value("${cloud.aws.s3.bucket}") final String bucket,
                               @Value("${cloud.aws.s3.root-directory}") final String rootDirectory,
                               @Value("${cloud.aws.s3.sub-directory}") final String subDirectory) {
        this.amazonS3 = amazonS3;
        this.bucket = bucket;
        this.rootDirectory = rootDirectory;
        this.subDirectory = subDirectory;
    }

    @Override
    public void save(final String path, final MultipartFile multiPartFile) {
        final String key = rootDirectory + DIRECTORY_SEPARATOR + subDirectory + path;
        final InputStream inputStream = getInputStream(multiPartFile);
        final ObjectMetadata objectMetadata = makeObjectMetadata(multiPartFile);
        putObjectToS3(key, inputStream, objectMetadata);
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
            amazonS3.putObject(bucket, key, inputStream, objectMetadata);
        } catch (final SdkClientException sdkClientException) {
            throw new ServerException(sdkClientException.getMessage());
        }
    }
}
