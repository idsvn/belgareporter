package be.belga.reporter.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import be.belga.reporter.entity.FileUpload;
import be.belga.reporter.service.FileService;
import be.belga.reporter.service.exception.FileNotFound;
import be.belga.reporter.service.exception.FileOffsetConflict;

@Controller
@RequestMapping("file")
public class FileController {
	private static final String TUS_HEADER_NAME = "Tus-Resumable";
	private static final String TUS_HEADER_VALUE = "1.0.0";

	private final FileService fileService;

	@Autowired
	public FileController(FileService fileService) {
		this.fileService = fileService;
	}

	@PostMapping
	public ResponseEntity createFile(@RequestHeader("upload-metadata") String metadata,
									@RequestHeader("upload-length") int fileSize) {
		Map<String, String> map = parse(metadata);
		FileUpload fileUpload = fileService.createFile(map.get("filename"), fileSize);
		URI location = URI.create("file/" + fileUpload.getId());
		return ResponseEntity.created(location).header(TUS_HEADER_NAME, TUS_HEADER_VALUE).build();
	}

	private Map<String, String> parse(String metadata) {
		Map<String, String> map = new HashMap<>();
		String[] entries = metadata.split(",");
		for (String entry : entries) {
			String[] pair = entry.split(" ");
			map.put(pair[0], new String(Base64.getDecoder().decode(pair[1])));
		}
		return map;
	}

	@RequestMapping(value = "{fileId}", method = RequestMethod.GET)
	public ResponseEntity<FileUpload> getFileUpload(@PathVariable("fileId") int fileId) {
		try {
			FileUpload fileUpload = fileService.getFileUpload(fileId);
			return ResponseEntity.ok().header(TUS_HEADER_NAME, TUS_HEADER_VALUE).body(fileUpload);
		} catch (FileNotFound e) {
			return ResponseEntity.notFound().build();
		}
	}

	@RequestMapping(value = "{fileId}", method = RequestMethod.HEAD)
	public ResponseEntity getOffset(@PathVariable int fileId) throws IOException {

		long offset = 0;
		try {
			offset = fileService.getOffset(fileId);
		} catch (FileNotFound e) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok().header("upload-offset", String.valueOf(offset))
				.header(TUS_HEADER_NAME, TUS_HEADER_VALUE).build();
	}

	@RequestMapping(value = "{fileId}", method = RequestMethod.PATCH)
	public ResponseEntity patchFile(@PathVariable int fileId, InputStream inputStream,
			@RequestHeader("upload-offset") long offset) throws IOException {
		long newOffset = 0;
		try {
			newOffset = fileService.patchFile(fileId, offset, inputStream);
		} catch (FileNotFound e) {
			return ResponseEntity.notFound().build();
		} catch (FileOffsetConflict e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
		return ResponseEntity.noContent().header("upload-offset", String.valueOf(newOffset))
				.header(TUS_HEADER_NAME, TUS_HEADER_VALUE).build();
	}
}
