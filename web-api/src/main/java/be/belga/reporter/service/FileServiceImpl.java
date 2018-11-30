package be.belga.reporter.service;

import be.belga.reporter.entity.FileUpload;
import be.belga.reporter.repository.FileUploadRepository;
import be.belga.reporter.service.exception.FileNotFound;
import be.belga.reporter.service.exception.FileOffsetConflict;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
class FileServiceImpl implements FileService {

    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    private final Path dataPath;
    private final FileUploadRepository fileUploadRepository;

    @Autowired
    FileServiceImpl(@Qualifier("dataPath") Path dataPath,
                    FileUploadRepository fileUploadRepository) {
        this.dataPath = dataPath;
        this.fileUploadRepository = fileUploadRepository;
    }

    @Override
    public Path saveFile(String name, InputStream data) throws IOException {
        Path folder = Files.createDirectories(dataPath.resolve("upload"));
        Path file = folder.resolve(name);
        Files.copy(data, file, StandardCopyOption.REPLACE_EXISTING);
        return file;
    }

    @Override
    public Path readFile(String name) {
        return dataPath.resolve("upload").resolve(name);
    }

    @Override
    public FileUpload getFileUpload(int fileId) {
        return fileUploadRepository.findById(fileId)
                .orElseThrow(EntityExistsException::new);
    }

    @Override
    public FileUpload createFile(String originalName, int fileSize) {
        FileUpload fileUpload = new FileUpload();
        fileUpload.setOriginalName(originalName);
        fileUpload.setSize(fileSize);
        fileUploadRepository.save(fileUpload);
        String generatedName = fileUpload.getId() + " - " + originalName;
        fileUpload.setGeneratedName(generatedName);
        fileUploadRepository.save(fileUpload);
        return fileUpload;
    }

    @Override
    public long getOffset(int fileId) throws IOException {
        FileUpload fileUpload = fileUploadRepository.findById(fileId)
                .orElseThrow(FileNotFound::new);
        Path filePath = dataPath.resolve(fileUpload.getGeneratedName());

        if (Files.exists(filePath)) {
            return Files.size(filePath);
        }
        return 0;
    }

    @Override
    public long patchFile(int fileId, long offset, InputStream inputStream) throws IOException {
        FileUpload fileUpload = fileUploadRepository.findById(fileId)
                .orElseThrow(FileNotFound::new);
        Path filePath = dataPath.resolve(fileUpload.getGeneratedName());

        if (Files.exists(filePath)) {
            long fileOffset = Files.size(filePath);

            if (offset != fileOffset)
                throw new FileOffsetConflict("Upload-offset not equal existing one: " + fileOffset);

            try (RandomAccessFile raf = new RandomAccessFile(filePath.toFile(), "rw")) {
                raf.seek(offset);

                byte[] buffer = new byte[4096];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    raf.write(buffer, 0, len);
                }
            }
        } else if (offset == 0) {
        	Files.createDirectories(filePath.getParent());
            Files.copy(inputStream, filePath);
        } else {
            throw new FileOffsetConflict("Offset must be zero for new file");
        }
        return Files.size(filePath);
    }
}
