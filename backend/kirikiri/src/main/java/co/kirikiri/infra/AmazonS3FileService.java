package co.kirikiri.infra;

import co.kirikiri.exception.ServerException;
import co.kirikiri.service.FileService;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
        final byte[] compressedBytes = compressFile(multiPartFile);
        final InputStream compressedInputStream = new ByteArrayInputStream(compressedBytes);
        final ObjectMetadata objectMetadata = makeObjectMetadata(compressedBytes);
        putObjectToS3(key, compressedInputStream, objectMetadata);
    }

    private byte[] compressFile(final MultipartFile multiPartFile) {
        try {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);

            final ZipEntry zipEntry = new ZipEntry(getFilename(multiPartFile));
            zipOutputStream.putNextEntry(zipEntry);
            IOUtils.copy(multiPartFile.getInputStream(), zipOutputStream);

            zipOutputStream.closeEntry();
            zipOutputStream.close();

            return byteArrayOutputStream.toByteArray();
        } catch (final IOException exception) {
            throw new ServerException(exception.getMessage());
        }
    }

    private String getFilename(final MultipartFile multiPartFile) {
        final String originalFilename = multiPartFile.getOriginalFilename();
        if (originalFilename == null) {
            throw new ServerException("원본 파일 이름이 존재하지 않습니다.");
        }
        return originalFilename;
    }

    private String findProperty(final String property) {
        return environment.getProperty(property);
    }

    private ObjectMetadata makeObjectMetadata(final byte[] compressedBytes) {
        final ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(compressedBytes.length);
        objectMetadata.setContentType("application/zip");
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
