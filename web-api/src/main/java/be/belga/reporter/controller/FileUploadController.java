package be.belga.reporter.controller;

import be.belga.reporter.entity.FileUpload;
import be.belga.reporter.entity.RestResponse;
import be.belga.reporter.repository.FileUploadRepository;
import be.belga.reporter.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@RestController
@RequestMapping(value = "/fileupload")
public class FileUploadController {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    @Autowired
    private FileUploadRepository uploadRepository;

    @Autowired
    private FileService fileService;

    @GetMapping("/upload/{name:.*}")
    public void downloadFile(@PathVariable("name") String fileName,
                             HttpServletResponse response) throws IOException {
        logger.info("====== downloadFile ======= : " + fileName);
        Path file = fileService.readFile(fileName);
        if (Files.notExists(file)) {
            logger.warn("File not found: " + fileName);
            throw new FileNotFoundException();
        }

        response.setContentType(Files.probeContentType(file));
        response.setHeader("Content-Disposition", "attachment; filename=" + file.getFileName());
        response.setHeader("Content-Length", String.valueOf(Files.size(file)));
        Files.copy(file, response.getOutputStream());

        logger.info("====== downloadFile end =======");
    }

    @RequestMapping(value = "/upload", headers = ("content-type=multipart/*"), method = RequestMethod.POST)
    public ResponseEntity<RestResponse<FileUpload>> uploadFile(@RequestParam("file") MultipartFile uploadfile, HttpServletRequest request) {

        logger.info("====== uploadFile =======");
        if (uploadfile.isEmpty()) {
            return new ResponseEntity<>(new RestResponse<>(HttpStatus.NO_CONTENT.value(), "No data found"), HttpStatus.NO_CONTENT);
        }
        FileUpload fileUpload;
        try {
            String originalName = uploadfile.getOriginalFilename();
            String fileExt = originalName.substring(originalName.lastIndexOf('.') + 1);
            String generatedName = UUID.randomUUID().toString() + "." + fileExt;
            final ServletContext servletContext = request.getServletContext();
            String contentType = servletContext.getMimeType(originalName);

            fileUpload = new FileUpload();
            fileUpload.setOriginalName(originalName);
            fileUpload.setGeneratedName(generatedName);
            fileUpload.setMimetype(contentType);
            fileUpload.setSize(Integer.parseInt(String.valueOf(uploadfile.getSize())));

            fileService.saveFile(generatedName, uploadfile.getInputStream());
            fileUpload.setGeneratedUrl(request.getContextPath() + "/fileupload/upload/" + generatedName);
            uploadRepository.save(fileUpload);
            logger.info("====== uploadFile end =======");
            return new ResponseEntity<>(new RestResponse<>(true, HttpStatus.OK.value(), fileUpload), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            logger.info("====== uploadFile end =======");
            return new ResponseEntity<>(new RestResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
