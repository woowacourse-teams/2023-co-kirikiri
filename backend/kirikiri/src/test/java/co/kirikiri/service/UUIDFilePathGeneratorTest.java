package co.kirikiri.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

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
        assertAll(
                () -> assertTrue(filePath.contains(imageDirType.getDirName())),
                () -> assertTrue(filePath.contains(originalFileName))
        );
    }
}
