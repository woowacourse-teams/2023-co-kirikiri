package co.kirikiri.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import co.kirikiri.domain.ImageDirType;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    private FilePathGenerator filePathGenerator;

    @InjectMocks
    private FileService fileService;

    @Test
    void 파일을_업로드하고_경로를_반환한다() throws IOException {
        //given
        final Long goalRoomId = 1L;
        final ImageDirType checkFeedDirType = ImageDirType.CHECK_FEED;
        final MockMultipartFile multipartFile = new MockMultipartFile("image", "originalFileName.jpeg",
                "image/jpeg", "test image".getBytes());
        final String generatedFilePath = LocalDate.now().format(DateTimeFormatter.ofPattern("/yyyy/MMdd/"));
        when(filePathGenerator.makeFilePath(anyLong(), any()))
                .thenReturn(generatedFilePath);

        //when
        final String imageUrl = fileService.uploadFileAndReturnPath(multipartFile, checkFeedDirType, goalRoomId);
        테스트용으로_생성된_파일을_제거한다(imageUrl);

        //then
        assertThat(imageUrl).contains(generatedFilePath, multipartFile.getOriginalFilename());
    }

    private void 테스트용으로_생성된_파일을_제거한다(final String filePath) {
        final String rootPath = filePath.substring(0, filePath.indexOf("/"));

        try {
            final File rootDir = new File(rootPath);
            FileUtils.deleteDirectory(rootDir);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
