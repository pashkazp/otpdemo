package depavlo.ui.rest.v1;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import depavlo.service.UserService;
import depavlo.ui.rest.v1.model.request.LoginRequest;
import depavlo.ui.rest.v1.model.request.OTPRequest;
import depavlo.ui.rest.v1.model.response.InfoResponse;
import depavlo.util.exception.OtpMailException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class AuthController which serves requests at the entry point of
 * authentication information.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

	/** The users service. */
	private final UserService userService;

	/** The Bearer token prefix. */
	@Value("${app.auth.tokenPrefix}")
	private String tokenPrefix;

	/**
	 * Authenticate user.
	 *
	 * @param loginRequest the LoginRequest
	 * @return the response entity
	 */
	@PostMapping(value = "/login", consumes = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE,
					MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		log.debug("authenticateUser] - Perform authenticate user with loginRequest: {}", loginRequest);

		String token = userService.createBearerToken(loginRequest.getEmail(), loginRequest.getPassword());

		return ResponseEntity.status(HttpStatus.OK).body(tokenPrefix + token);
	}

	/**
	 * One-time password request
	 *
	 * @param otpRequest the OTPRequest
	 * @return the response entity
	 */
	@PostMapping(value = "/request-otp", consumes = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE,
					MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<?> otpRequest(@Valid @RequestBody OTPRequest otpRequest) {

		log.debug("otpRequest] - Perform request one time password with otpRequest: {}", otpRequest);

		userService.requestOtp(otpRequest.getEmail());

		return ResponseEntity.status(HttpStatus.OK).body("One Time Password was send by email.");
	}

	/**
	 * Handle OtpMailException.
	 *
	 * @param ex      the OtpMailException
	 * @param request the WebRequest
	 */
	@ExceptionHandler(value = { OtpMailException.class })
	@ResponseBody()
	public void handleOtpMailException(OtpMailException ex, WebRequest request) {
		log.error("handleOtpMailException] - Gets exception: {}", ex.getMessage());
	}

	/**
	 * Handle any Exception.
	 *
	 * @param ex      the Exception
	 * @param request the WebRequest
	 * @return the response entity
	 */
	@ExceptionHandler(value = { Exception.class })
	@ResponseBody()
	public ResponseEntity<Object> handleOtherExceptions(Exception ex, WebRequest request) {
		log.debug("handleOtherExceptions] - Gets exception: {}", ex.getMessage());

		InfoResponse infoResponse = new InfoResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex);

		String headers = request.getHeader(HttpHeaders.ACCEPT);

		MediaType mt;
		if (headers.indexOf(MediaType.APPLICATION_XML_VALUE) == -1) {
			mt = MediaType.APPLICATION_JSON;
		} else {
			mt = MediaType.APPLICATION_XML;
		}
		return ResponseEntity.status(infoResponse.getStatus()).contentType(mt).body(infoResponse);
	}

	/**
	 * Handle bad credentials exception .
	 *
	 * @param ex      the BadCredentialsException
	 * @param request the WebRequest
	 * @return the response entity
	 */
	@ExceptionHandler(value = { BadCredentialsException.class })
	@ResponseBody()
	public ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
		log.debug("handleBadCredentialsException] - Gets exception: {}", ex.getMessage());

		InfoResponse infoResponse = new InfoResponse(HttpStatus.FORBIDDEN, "Access denied",
				"Check login and password.");

		String headers = request.getHeader(HttpHeaders.ACCEPT);

		MediaType mt;
		if (headers.indexOf(MediaType.APPLICATION_XML_VALUE) == -1) {
			mt = MediaType.APPLICATION_JSON;
		} else {
			mt = MediaType.APPLICATION_XML;
		}
		return ResponseEntity.status(infoResponse.getStatus()).contentType(mt).body(infoResponse);
	}

}
