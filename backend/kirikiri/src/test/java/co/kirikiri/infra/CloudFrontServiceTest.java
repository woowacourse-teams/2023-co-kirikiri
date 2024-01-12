package co.kirikiri.infra;

import co.kirikiri.service.exception.ServerException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import java.net.MalformedURLException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CloudFrontServiceTest {

    @Mock
    private Environment environment;

    @InjectMocks
    private CloudFrontService cloudFrontService;

    @Test
    void 정상적으로_GET_URL을_생성한다() throws MalformedURLException {
        //given
        when(environment.getProperty("cloud.aws.cloud-front.distribution-domain"))
                .thenReturn("https://test.com");
        final String path = "/test/path/originalFilename.png";

        //when
        final URL url = cloudFrontService.generateGetUrl(path);

        //then
        assertThat(url).isEqualTo(new URL("https://test.com" + path));
    }

    @Test
    void cloudFront_배포_도메인에_프로토콜이_정의되지_않은_경우_예외를_던진다() {
        //given
        when(environment.getProperty("cloud.aws.cloud-front.distribution-domain"))
                .thenReturn("test.com");
        final String path = "/test/path/originalFilename.png";

        //when
        //then
        assertThatThrownBy(() -> cloudFrontService.generateGetUrl(path))
                .isInstanceOf(ServerException.class);
    }
}
