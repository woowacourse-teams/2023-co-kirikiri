package co.kirikiri.infra;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import co.kirikiri.exception.ServerException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@ExtendWith(MockitoExtension.class)
class AmazonS3FileServiceTest {

    private static final String PATH = "/test/path/originalFilename.png";

    @Mock
    private AmazonS3 amazonS3;

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
        when(amazonS3.putObject(any(), any(), any(), any()))
                .thenReturn(null);

        //when
        //then
        assertDoesNotThrow(() -> amazonS3FileService.save(PATH, multipartFile));
    }

    @Test
    void 파일_저장_시_InputStream을_가져올때_예외가_터진다() throws IOException {
        //given
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
        when(amazonS3.putObject(any(), any(), any(), any()))
                .thenThrow(new SdkClientException("sdk client 원할하지 않습니다."));

        //when
        //then
        assertThatThrownBy(() -> amazonS3FileService.save(PATH, multipartFile))
                .isInstanceOf(ServerException.class);
    }
}
