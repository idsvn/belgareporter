package be.belga.reporter.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import be.belga.reporter.entity.RestResponse;
import be.belga.reporter.entity.User;
import be.belga.reporter.entity.UserMetadata;
import be.belga.reporter.repository.UserMetadataRepository;

@RestController
@RequestMapping(value = "/user-metadata", produces = { "application/json" })
public class UserMetadataController {

    private static final Logger logger = LoggerFactory.getLogger(UserMetadataController.class);

    @Autowired
    private UserMetadataRepository userMetadataRepository;

    @RequestMapping(method = RequestMethod.GET, value = "/findByUsername")
    public ResponseEntity<RestResponse<UserMetadata>> findByUsername(@RequestBody User userInfo) {
        logger.info("====== findByUsername =======");
        try {
            UserMetadata userMetadata = userMetadataRepository.findByUser_Username(userInfo.getUsername());
            logger.info("====== findByUsername end =======");
            if (userMetadata != null) {
                return new ResponseEntity<>(new RestResponse<>(true, HttpStatus.OK.value(), userMetadata), HttpStatus.OK);
            }
            return new ResponseEntity<>(new RestResponse<>(HttpStatus.NOT_FOUND.value(), "UserMetadata was not found"), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            logger.info("====== findByUsername end =======");
            return new ResponseEntity<>(new RestResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
