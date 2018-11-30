package be.belga.reporter.service;

import be.belga.reporter.entity.FileUpload;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public interface FileService {
    Path saveFile(String name, InputStream data) throws IOException;
    Path readFile(String name);

    FileUpload createFile(String originalName, int fizeSize);

    long getOffset(int fileId) throws IOException;

    long patchFile(int fileId, long offset, InputStream inputStream) throws IOException;

    FileUpload getFileUpload(int fileId);
}
