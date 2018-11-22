package be.belga.reporter.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import be.belga.reporter.email.EmailSenderService;
import be.belga.reporter.entity.RestResponse;
import be.belga.reporter.entity.User;
import be.belga.reporter.params.UserChangePassword;
import be.belga.reporter.params.UserLogin;
import be.belga.reporter.repository.UserRepository;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping(value = "/user", produces = { "application/json" })
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EmailSenderService emailSenderService;

	@ApiResponses(value = { @ApiResponse(code = 200, message = "Login successfull") })
	@RequestMapping(method = RequestMethod.POST, value = "/login")
	public ResponseEntity<RestResponse<User>> login(@RequestBody UserLogin userInfo) {
		logger.info("====== login =======");
		try {
			User user = userRepository.findByUsernameAndPassword(userInfo.getUsername(), userInfo.getPassword());
			logger.info("====== login end =======");
			if (user != null) {
				return new ResponseEntity<>(new RestResponse<>(true, HttpStatus.OK.value(), user), HttpStatus.OK);
			}
			return new ResponseEntity<>(
					new RestResponse<>(HttpStatus.NOT_FOUND.value(), "Invalid username or password"),
					HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			logger.info("====== login end =======");
			return new ResponseEntity<>(new RestResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/update-profile", method = RequestMethod.POST, consumes = "application/json")
	public ResponseEntity<RestResponse<User>> updateProfile(@RequestBody User user) {
		logger.info("====== updateProfile end =======");
		try {
			User read = userRepository.findOneByUsername(user.getUsername());
			if (read != null) {
				if (user.getPassword() != null) {
					read.setPassword(user.getPassword());
				}
				if (user.getFirstName() != null) {
					read.setFirstName(user.getFirstName());
				}
				if (user.getLastName() != null) {
					read.setLastName(user.getLastName());
				}
				if (user.getGender() != null) {
					read.setGender(user.getGender());
				}
				if (user.getEmail() != null) {
					read.setEmail(user.getEmail());
				}
				if (user.getStatus() != null) {
					read.setStatus(user.getStatus());
				}

				userRepository.save(read);
				logger.info("====== updateProfile end =======");
				return new ResponseEntity<>(new RestResponse<>(true, HttpStatus.OK.value(), user), HttpStatus.OK);
			}

			logger.info("====== updateProfile end =======");
			return new ResponseEntity<>(
					new RestResponse<>(HttpStatus.NOT_FOUND.value(), "Invalid username or password"),
					HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			logger.info("====== updateProfile end =======");
			return new ResponseEntity<>(new RestResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/change-password")
	public ResponseEntity<RestResponse<User>> changePassword(@RequestBody UserChangePassword userInfo) {
		logger.info("====== change-password =======");
		try {
			User user = userRepository.findOneByUsername(userInfo.getUsername());
			if (user != null && user.getPassword().equals(userInfo.getOldPassword())) {
				user.setPassword(userInfo.getNewPassword());

				userRepository.save(user);
				logger.info("====== change-password end =======");

				return new ResponseEntity<>(new RestResponse<>(true, HttpStatus.OK.value(), user), HttpStatus.OK);
			}
			return new ResponseEntity<>(
					new RestResponse<>(HttpStatus.NOT_FOUND.value(), "Invalid username or password"),
					HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			logger.info("====== change-password end =======");
			return new ResponseEntity<>(new RestResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/forget-password")
	public ResponseEntity<RestResponse<User>> forgetPassword(@RequestBody UserLogin userInfo,
			HttpServletRequest request) {
		logger.info("====== forget-password =======");
		try {
			if (userInfo.getEmail() != null && userInfo.getEmail().trim().length() > 0) {
				User read = userRepository.findByEmail(userInfo.getEmail());
				if (read != null) {
					String password = Long.toHexString(System.currentTimeMillis());

					read.setPassword(password);
					userRepository.save(read);

					logger.info("====== forget-password end =======");

					String toAddr = userInfo.getEmail().trim();
					String subject = "Reset your password";
					String content = String.format("Your password reset: %s. Please login and change password",
							password);
					emailSenderService.sendEmail(toAddr, subject, content);

					return new ResponseEntity<>(new RestResponse<>(true, HttpStatus.OK.value(), read), HttpStatus.OK);
				}
			}
			logger.info("====== forget-password end =======");
			return new ResponseEntity<>(
					new RestResponse<>(HttpStatus.NOT_FOUND.value(), "Invalid username or password"),
					HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			logger.info("====== forget-password end =======");
			return new ResponseEntity<>(new RestResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(method = RequestMethod.GET, value = "/all")
	public ResponseEntity<RestResponse<List<User>>> findAll() {
		logger.info("====== findAllUser =======");

		try {
			List<User> lst = userRepository.findAll();
			logger.info("====== findAllUser end =======");
			return new ResponseEntity<>(new RestResponse<>(true, HttpStatus.OK.value(), lst), HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			logger.info("====== findAllUser end =======");
			return new ResponseEntity<>(new RestResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
