package co.kirikiri.infra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import co.kirikiri.exception.ServerException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.Protocol;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

@ExtendWith(MockitoExtension.class)
class AmazonS3FileServiceTest {

    private static final String PATH = "/test/path/originalFilename.png";

    @Mock
    private AmazonS3 amazonS3;

    @Mock
    private Environment environment;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private AmazonS3FileService amazonS3FileService;

    @Test
    void 정상적으로_파일을_저장한다() throws IOException {
        //given
        when(multipartFile.getInputStream())
                .thenReturn(new ByteArrayInputStream("test-content".getBytes()));
        when(multipartFile.getSize())
                .thenReturn((long) "test-content".length());
        when(multipartFile.getContentType())
                .thenReturn("image/png");
        when(multipartFile.getOriginalFilename())
                .thenReturn("originalFilename.png");
        when(environment.getProperty("cloud.aws.s3.root-directory"))
                .thenReturn("rootDirectory");
        when(environment.getProperty("cloud.aws.s3.sub-directory"))
                .thenReturn("subDirectory");
        when(environment.getProperty("cloud.aws.s3.bucket"))
                .thenReturn("bucket");
        when(amazonS3.putObject(any(), any(), any(), any()))
                .thenReturn(null);

        //when
        //then
        assertDoesNotThrow(() -> amazonS3FileService.save(PATH, multipartFile));
    }

    @Test
    void 파일_저장_시_원본_파일이름이_존재하지_않을떄_예외가_터진다() {
        //given
        when(multipartFile.getOriginalFilename())
                .thenReturn(null);

        //when
        //then
        assertThatThrownBy(() -> amazonS3FileService.save(PATH, multipartFile))
                .isInstanceOf(ServerException.class);
    }

    @Test
    void 파일_저장_시_InputStream을_가져올때_예외가_터진다() throws IOException {
        //given
        when(multipartFile.getOriginalFilename())
                .thenReturn("originalFilename.png");
        when(multipartFile.getInputStream())
                .thenThrow(new IOException());

        //when
        //then
        assertThatThrownBy(() -> amazonS3FileService.save(PATH, multipartFile))
                .isInstanceOf(ServerException.class);
    }

    @Test
    void 파일_저장_시_AWS서버와_연결이_원할하지_않을_경우_예외가_터진다() throws IOException {
        //given
        when(multipartFile.getInputStream())
                .thenReturn(new ByteArrayInputStream("test-content".getBytes()));
        when(multipartFile.getSize())
                .thenReturn((long) "test-content".length());
        when(multipartFile.getContentType())
                .thenReturn("image/png");
        when(multipartFile.getOriginalFilename())
                .thenReturn("originalFilename.png");
        when(amazonS3.putObject(any(), any(), any(), any()))
                .thenThrow(new AmazonServiceException("server가 원할하지 않습니다."));

        //when
        //then
        assertThatThrownBy(() -> amazonS3FileService.save(PATH, multipartFile))
                .isInstanceOf(ServerException.class);
    }

    @Test
    void 파일_저장_시_SDK_CLIENT에서_예상치_못한_예외가_발생한_경우_경우_예외가_터진다() throws IOException {
        //given
        when(multipartFile.getInputStream())
                .thenReturn(new ByteArrayInputStream("test-content".getBytes()));
        when(multipartFile.getSize())
                .thenReturn((long) "test-content".length());
        when(multipartFile.getContentType())
                .thenReturn("image/png");
        when(multipartFile.getOriginalFilename())
                .thenReturn("originalFilename.png");
        when(amazonS3.putObject(any(), any(), any(), any()))
                .thenThrow(new SdkClientException("sdk client 원할하지 않습니다."));

        //when
        //then
        assertThatThrownBy(() -> amazonS3FileService.save(PATH, multipartFile))
                .isInstanceOf(ServerException.class);
    }

    @Test
    void 정상적으로_파일_URL을_생성한다() throws MalformedURLException {
        //given
        final URL url = new URL(Protocol.HTTP.toString(), "host", 80, "file");
        when(environment.getProperty(anyString()))
                .thenReturn("bucket");
        when(environment.getProperty(anyString()))
                .thenReturn("60000");
        when(amazonS3.generatePresignedUrl(any()))
                .thenReturn(url);

        //when
        final URL result = amazonS3FileService.generateUrl("path", HttpMethod.GET);

        //then
        assertThat(result).isEqualTo(url);
    }
}
