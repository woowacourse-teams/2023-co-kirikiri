package co.kirikiri.common.service;

import co.kirikiri.common.type.ImageDirType;

public interface FilePathGenerator {

    String makeFilePath(final ImageDirType dirType, String originalFileName);
}
