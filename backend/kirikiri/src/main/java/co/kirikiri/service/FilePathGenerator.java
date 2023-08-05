package co.kirikiri.service;

public interface FilePathGenerator {
    String makeFilePath(final Long id, final ImageDirType dirType);
}
