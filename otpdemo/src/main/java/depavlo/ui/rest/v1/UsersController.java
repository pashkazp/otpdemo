package depavlo.ui.rest.v1;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.security.RolesAllowed;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.google.common.collect.Multimap;

import depavlo.model.User;
import depavlo.service.UserService;
import depavlo.ui.rest.v1.model.request.UserCreateRequest;
import depavlo.ui.rest.v1.model.request.UserUpdateRequest;
import depavlo.ui.rest.v1.model.response.AbstractSubInfoResponse;
import depavlo.ui.rest.v1.model.response.InfoResponse;
import depavlo.ui.rest.v1.model.response.ValidationInfoResponse;
import depavlo.util.exception.AuditException;
import depavlo.util.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class UsersController which serves requests at the entry point of user
 * information.
 * 
 * @author Pavlo Degtyaryev
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UsersController {

	/** The Users service. */
	private final UserService userService;

	/**
	 * Gets the User by id.
	 *
	 * @param userId the user id
	 * @return the user
	 */
	@GetMapping(value = "/{userId}", consumes = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE,
					MediaType.APPLICATION_XML_VALUE })
	@RolesAllowed({ "ROLE_USER" })
	public ResponseEntity<?> getUserById(@PathVariable(value = "userId") Long userId) {
		log.debug("getUserById] - Get User by id {}", userId);

		Optional<User> userO = userService.getUserById(userId);

		if (userO.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(userO.get());
	}

	/**
	 * List all users.
	 *
	 * @return the response
	 */
	@GetMapping(value = "", consumes = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_XML_VALUE,
					MediaType.APPLICATION_JSON_VALUE })
	@RolesAllowed({ "ROLE_USER" })
	public ResponseEntity<Object> listAllUsers() {

		log.debug("listAllUsers] - Get the list of  all Users");

		List<User> users = userService.getAllUsers();

		if (users.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.status(HttpStatus.OK).body(users);

	}

	/**
	 * Delete user by id.
	 *
	 * @param userId the user id
	 * @return the response
	 */
	@DeleteMapping(value = "/{userId}", consumes = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE,
					MediaType.APPLICATION_XML_VALUE })
	@RolesAllowed({ "ROLE_USER" })
	public ResponseEntity<String> deleteUserById(@PathVariable(value = "userId") Long userId) {
		log.debug("deleteUserById] - Perform delete User by Id {}", userId);

		try {
			userService.deleteUserById(userId);
		} catch (EmptyResultDataAccessException e) {
			log.debug("deleteUserById] - User with Id {} is not found", userId);
		}

		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	/**
	 * Update user by id.
	 *
	 * @param userId            the user id
	 * @param userUpdateRequest the user update request information
	 * @return the response entity
	 */
	@PutMapping(value = "/{userId}", consumes = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE,
					MediaType.APPLICATION_XML_VALUE })
	@RolesAllowed({ "ROLE_USER" })
	public ResponseEntity<?> updateUserById(@PathVariable(value = "userId") Long userId,
			@RequestBody(required = false) UserUpdateRequest userUpdateRequest) {
		log.debug("updateUserById] - Perform update User by Id {} with data '{}'", userId, userUpdateRequest);

		if (userUpdateRequest == null) {
			log.warn("updateUserById] - User update request must be not null");
			InfoResponse infoResponse = new InfoResponse(HttpStatus.BAD_REQUEST,
					"Request is bad",
					"Bad request. Please check your request for consistent of documentation.");
			return new ResponseEntity<>(infoResponse, infoResponse.getStatus());
		}

		Optional<User> userO = userService.updateUserById(userId, userUpdateRequest);

		return ResponseEntity.status(HttpStatus.OK).body(userO.get());
	}

	/**
	 * Adds the new user.
	 *
	 * @param newUser the new user
	 * @return the response entity
	 */
	@PostMapping(value = "", consumes = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE,
					MediaType.APPLICATION_XML_VALUE })
	@RolesAllowed({ "ROLE_USER" })
	public ResponseEntity<Object> addNewUser(
			@RequestBody(required = false) UserCreateRequest newUser) {

		log.info("addNewUser] - Request register new User: '{}'", newUser);

		if (newUser == null) {
			log.warn("addNewUser] - User creation request must be not null");
			InfoResponse infoResponse = new InfoResponse(HttpStatus.BAD_REQUEST,
					"Request is bad",
					"Bad request. Please check your request for consistent of documentation.");

			return new ResponseEntity<>(infoResponse, infoResponse.getStatus());
		}

		log.info("addNewUser] - Perform register new User");

		Optional<User> userO = userService.registerNewUser(newUser);

		if (userO.isEmpty()) { // Registration is fail
			log.info("addNewUser] - Registration is fail. Inform to the registrant");
			InfoResponse infoResponse = new InfoResponse(HttpStatus.INTERNAL_SERVER_ERROR,
					"Information not available", "Information unavailable due to internal server error");

			return new ResponseEntity<>(infoResponse, infoResponse.getStatus());
		}

		log.info("addNewUser] - Registration is successful. Inform to the registrant");

		URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/users").path("/{id}")
				.buildAndExpand(userO.get().getId()).toUri();
		return ResponseEntity.created(location).body(userO.get());

	}

	/**
	 * Handle user not found exception.
	 *
	 * @param ex      the UserNotFoundException
	 * @param request the WebRequest
	 * @return the response entity
	 */
	@ExceptionHandler(value = { UserNotFoundException.class })
	@ResponseBody()
	public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex,
			WebRequest request) {

		log.debug("handleUserNotFoundException] - Gets exception: UserNotFoundException with message: ",
				ex.getErrMsg());

		InfoResponse infoResponse = new InfoResponse(HttpStatus.NOT_FOUND, ex.getErrMsg(), "");

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
	 * Handle audit exception.
	 *
	 * @param ex      the AuditException
	 * @param request the WebRequest
	 * @return the response entity
	 */
	@ExceptionHandler(value = { AuditException.class })
	@ResponseBody()
	public ResponseEntity<Object> handleAuditException(AuditException ex,
			WebRequest request) {

		log.debug(
				"handleAuditException] - Gets exception: AuditException with message: ",
				ex.getErrMsg());

		InfoResponse infoResponse = new InfoResponse(HttpStatus.BAD_REQUEST, ex.getErrMsg(), "");
		Multimap<String, String> response = ex.getAuditMessages();
		if (!response.isEmpty()) {
			infoResponse.setSubInfos(new ArrayList<AbstractSubInfoResponse>());
			response.forEach((k, v) -> infoResponse.getSubInfos().add(new ValidationInfoResponse(k, v)));
		}
		infoResponse.getSubInfos().add(new ValidationInfoResponse("", infoResponse.getMessage()));

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
	 * Handle other exceptions.
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

}