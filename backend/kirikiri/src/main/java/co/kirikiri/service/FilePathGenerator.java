package co.kirikiri.service;

import co.kirikiri.domain.ImageDirType;

public interface FilePathGenerator {

    String makeFilePath(final Long id, final ImageDirType dirType);
}
