package co.kirikiri.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.kirikiri.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class UUIDFilePathGeneratorTest {

    private final FilePathGenerator filePathGenerator = new UUIDFilePathGenerator();

    @Test
    void 정상적으로_파일_경로를_생성한다() {
        //given
        final ImageDirType imageDirType = ImageDirType.ROADMAP_NODE;
        final String originalFileName = "originalFileName.png";

        //when
        final String filePath = filePathGenerator.makeFilePath(imageDirType, originalFileName);

        //then
        assertTrue(filePath.contains(imageDirType.getDirName()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"가.jpg", "나다.jpg", "랅.jpg", "테스트.jpg"})
    void 파일이름에_한글이_들어간_경우_정상적으로_경로를_쌩성한다(final String originalFileName) {
        //given
        final ImageDirType imageDirType = ImageDirType.ROADMAP_NODE;

        //when
        final String filePath = filePathGenerator.makeFilePath(imageDirType, originalFileName);

        //then
        assertTrue(filePath.contains(imageDirType.getDirName()));
    }

    @ParameterizedTest
    @ValueSource(strings = {" .jpg", "가운데 공백.jpg", " 앞에공백.jpg", "뒤에공백 .jpg"})
    void 파일이름에_공백이_들어간_경우_정상적으로_경로를_쌩성한다(final String originalFileName) {
        //given
        final ImageDirType imageDirType = ImageDirType.ROADMAP_NODE;

        //when
        final String filePath = filePathGenerator.makeFilePath(imageDirType, originalFileName);

        //then
        assertTrue(filePath.contains(imageDirType.getDirName()));
    }

    @Test
    void 파일_이름에_확장자가_없을_경우_예외가_발생한다() {
        //given
        final ImageDirType imageDirType = ImageDirType.ROADMAP_NODE;
        final String originalFileName = "originalFileName";

        //when
        //then
        assertThatThrownBy(() -> filePathGenerator.makeFilePath(imageDirType, originalFileName))
                .isInstanceOf(BadRequestException.class);
    }
}
