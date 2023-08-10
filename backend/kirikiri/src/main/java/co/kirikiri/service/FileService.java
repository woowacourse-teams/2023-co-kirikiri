package co.kirikiri.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    void save(final String path, final MultipartFile multiPartFile);
}
