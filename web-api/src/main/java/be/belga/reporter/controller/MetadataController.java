package be.belga.reporter.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import be.belga.reporter.entity.Metadata;
import be.belga.reporter.entity.RestResponse;
import be.belga.reporter.repository.MetadataRepository;

@RestController
@RequestMapping(value = "/metadata", produces = { "application/json" })
public class MetadataController {

	private static final Logger logger = LoggerFactory.getLogger(MetadataController.class);

	@Autowired
	private MetadataRepository metadataRepository;

	@RequestMapping(method = RequestMethod.GET, value = "/findById")
	public ResponseEntity<RestResponse<Metadata>> findById(@RequestParam String id) {
		logger.info("====== findById =======");
		try {
			int metaId = Integer.parseInt(id.trim());
			Optional<Metadata> optional = metadataRepository.findById(metaId);
			Metadata metadata = null;

			if (optional != null && optional.isPresent()) {
				metadata = optional.get();
			}

			logger.info("====== findById end =======");
			if (metadata != null) {
				return new ResponseEntity<>(new RestResponse<>(true, HttpStatus.OK.value(), metadata), HttpStatus.OK);
			}

			return new ResponseEntity<>(new RestResponse<>(HttpStatus.NOT_FOUND.value(), "Meta Data is not found"),
					HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			logger.info("====== findById end =======");
			return new ResponseEntity<>(new RestResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
