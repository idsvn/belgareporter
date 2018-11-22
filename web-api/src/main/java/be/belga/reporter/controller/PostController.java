package be.belga.reporter.controller;

import java.util.Date;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import be.belga.reporter.entity.Post;
import be.belga.reporter.entity.RestResponse;
import be.belga.reporter.repository.PostRepository;
import be.belga.reporter.type.StatusTypeEnum;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping(value = "/post")
public class PostController {

    private static final Logger logger = LoggerFactory.getLogger(PostController.class);

    @Autowired
    private PostRepository postRepository;

    @ApiResponses(value = { @ApiResponse(code = 404, message = "Post is not Found"), @ApiResponse(code = 201, message = "Create post successfully") })
    @RequestMapping(value = "/create", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<RestResponse<Post>> create(@RequestBody Post post) {
        logger.info("====== create =======");

        if (post == null) {
            return new ResponseEntity<>(new RestResponse<>(HttpStatus.NO_CONTENT.value(), "Post was not found"), HttpStatus.NO_CONTENT);
        }

        if (post.getMetadata() == null) {
            return new ResponseEntity<>(new RestResponse<>(HttpStatus.NOT_ACCEPTABLE.value(), "Meta data is null"), HttpStatus.NOT_ACCEPTABLE);
        }

        if (post.getType() == null) {
            return new ResponseEntity<>(new RestResponse<>(HttpStatus.NOT_ACCEPTABLE.value(), "Post type is null"), HttpStatus.NOT_ACCEPTABLE);
        }

        post.setCreateDate(new Date());
        post.setStatus(StatusTypeEnum.A);

        post.getMetadata().setPost(post);

        try {
            post = postRepository.save(post);
            logger.info("====== create end =======");
            return new ResponseEntity<>(new RestResponse<>(true, HttpStatus.OK.value(), post), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            logger.info("====== create end =======");
            return new ResponseEntity<>(new RestResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<RestResponse<Post>> update(@RequestBody Post post) {
        logger.info("====== update =======");

        logger.info("data: " + post);
        Optional<Post> optional = postRepository.findById(post.getId());

        if (!optional.isPresent()) {
            return new ResponseEntity<>(new RestResponse<>(HttpStatus.NOT_FOUND.value(), "Post was not found"), HttpStatus.NOT_FOUND);
        }
        Post p = optional.get();
        p.setTopic(post.getTopic());
        p.setTitle(post.getTitle());
        p.setCaption(post.getCaption());

        if (post.getMetadata() != null) {
            p.setMetadata(post.getMetadata());
        }

        try {
            p = postRepository.save(p);
            logger.info("====== update end =======");
            return new ResponseEntity<>(new RestResponse<>(true, HttpStatus.OK.value(), p), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            logger.info("====== update end =======");
            return new ResponseEntity<>(new RestResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}
