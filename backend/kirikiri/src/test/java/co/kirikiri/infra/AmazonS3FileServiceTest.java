package co.kirikiri.infra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import co.kirikiri.common.exception.ServerException;
import co.kirikiri.common.infra.AmazonS3FileService;
import co.kirikiri.common.infra.CloudFrontService;
import co.kirikiri.service.dto.FileInformation;
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
import java.io.FileInputStream;
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
    private CloudFrontService cloudFrontService;

    @InjectMocks
    private AmazonS3FileService amazonS3FileService;

    @Test
    void 정상적으로_파일을_저장한다() {
        //given
        when(environment.getProperty("cloud.aws.s3.root-directory"))
                .thenReturn("rootDirectory");
        when(environment.getProperty("cloud.aws.s3.sub-directory"))
                .thenReturn("subDirectory");
        when(environment.getProperty("cloud.aws.s3.bucket"))
                .thenReturn("bucket");
        when(amazonS3.putObject(any(), any(), any(), any()))
                .thenReturn(null);
        final FileInformation fileInformation = new FileInformation("originalFileName.png", 100L,
                "image/png", FileInputStream.nullInputStream());

        //when
        //then
        assertDoesNotThrow(() -> amazonS3FileService.save(PATH, fileInformation));
    }

    @Test
    void 파일_저장_시_AWS서버와_연결이_원할하지_않을_경우_예외가_터진다() {
        //given
        when(amazonS3.putObject(any(), any(), any(), any()))
                .thenThrow(new AmazonServiceException("server가 원할하지 않습니다."));
        final FileInformation fileInformation = new FileInformation("originalFileName.png", 100L,
                "image/png", FileInputStream.nullInputStream());

        //when
        //then
        assertThatThrownBy(() -> amazonS3FileService.save(PATH, fileInformation))
                .isInstanceOf(ServerException.class);
    }

    @Test
    void 파일_저장_시_SDK_CLIENT에서_예상치_못한_예외가_발생한_경우_경우_예외가_터진다() {
        //given
        when(amazonS3.putObject(any(), any(), any(), any()))
                .thenThrow(new SdkClientException("sdk client 원할하지 않습니다."));
        final FileInformation fileInformation = new FileInformation("originalFileName.png", 100L,
                "image/png", FileInputStream.nullInputStream());

        //when
        //then
        assertThatThrownBy(() -> amazonS3FileService.save(PATH, fileInformation))
                .isInstanceOf(ServerException.class);
    }

    @Test
    void 정상적으로_파일_GET_URL을_생성한다() throws MalformedURLException {
        //given
        final URL url = new URL(Protocol.HTTP.toString(), "host", 80, "file");
        when(cloudFrontService.generateGetUrl(anyString()))
                .thenReturn(url);

        //when
        final URL result = amazonS3FileService.generateUrl("path", HttpMethod.GET);

        //then
        assertThat(result).isEqualTo(url);
    }

    @Test
    void 정상적으로_파일_POST_URL을_생성한다() throws MalformedURLException {
        //given
        final URL url = new URL(Protocol.HTTP.toString(), "host", 80, "file");
        when(environment.getProperty(anyString()))
                .thenReturn("bucket");
        when(environment.getProperty(anyString()))
                .thenReturn("60000");
        when(amazonS3.generatePresignedUrl(any()))
                .thenReturn(url);

        //when
        final URL result = amazonS3FileService.generateUrl("path", HttpMethod.POST);

        //then
        assertThat(result).isEqualTo(url);
    }

    @Test
    void 정상적으로_파일_DELETE_URL을_생성한다() throws MalformedURLException {
        //given
        final URL url = new URL(Protocol.HTTP.toString(), "host", 80, "file");
        when(environment.getProperty(anyString()))
                .thenReturn("bucket");
        when(environment.getProperty(anyString()))
                .thenReturn("60000");
        when(amazonS3.generatePresignedUrl(any()))
                .thenReturn(url);

        //when
        final URL result = amazonS3FileService.generateUrl("path", HttpMethod.DELETE);

        //then
        assertThat(result).isEqualTo(url);
    }
}
