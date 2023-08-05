package co.kirikiri.service;

import co.kirikiri.exception.ServerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@Transactional
public class FileService {

    private final String storageLocation;
    private final String imagePathPrefix;
    private final FilePathGenerator filePathGenerator;

    public FileService(@Value("${file.upload-dir}") final String storageLocation,
                       @Value("${file.server-path}") final String imagePathPrefix,
                       final FilePathGenerator filePathGenerator) {
        this.storageLocation = storageLocation;
        this.imagePathPrefix = imagePathPrefix;
        this.filePathGenerator = filePathGenerator;
    }

    public String uploadFileAndReturnPath(final MultipartFile multiPartFile, final ImageDirType dirType, final Long id) {
        final String originalName = multiPartFile.getOriginalFilename();
        final String fileName = originalName.substring(originalName.lastIndexOf("\\") + 1);
        final String filePath = filePathGenerator.makeFilePath(id, dirType);
        final String saveFileName = storageLocation + filePath + fileName;
        final Path savePath = Path.of(saveFileName);
        makePathDirectories(savePath);
        trasferFilePath(multiPartFile, savePath);
        return imagePathPrefix + filePath + fileName;
    }

    private void makePathDirectories(final Path path) {
        final Path parentDir = path.getParent();
        if (Files.exists(parentDir)) {
            return;
        }

        try {
            Files.createDirectories(parentDir);
        } catch (final IOException e) {
            throw new ServerException("파일 저장 중에 문제가 생겼습니다. (파일 경로)");
        }
    }

    private void trasferFilePath(final MultipartFile multiPartFile, final Path savePath) {
        try {
            multiPartFile.transferTo(savePath);
        } catch (final IOException e) {
            throw new ServerException("파일 저장 중에 문제가 생겼습니다. (파일 경로 수정)");
        }
    }
}
