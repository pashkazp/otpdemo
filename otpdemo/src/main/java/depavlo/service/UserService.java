package depavlo.service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.security.RolesAllowed;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import depavlo.model.Otp;
import depavlo.model.User;
import depavlo.security.UserPrincipal;
import depavlo.security.jwt.JwtTokenProvider;
import depavlo.ui.rest.v1.model.request.UserCreateRequest;
import depavlo.ui.rest.v1.model.request.UserUpdateRequest;
import depavlo.util.AuditResponse;
import depavlo.util.MaritalStatus;
import depavlo.util.exception.OtpMailException;
import depavlo.util.exception.UserCreateRequestAuditException;
import depavlo.util.exception.UserNotFoundException;
import depavlo.util.exception.UserUpdateRequestAuditException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class UserService that make all work.
 * 
 * @author Pavlo Degtyaryev
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

	/** The user service to repo. */
	private final UserServiceToRepo userServiceToRepo;

	/** The otp service to repo. */
	private final OtpServiceToRepo otpServiceToRepo;

	/** The password encoder. */
	private final PasswordEncoder passwordEncoder;

	/** The otp notification service. */
	private final OtpNotificationService otpNotificationService;

	/** The jwt token provider. */
	private final JwtTokenProvider jwtTokenProvider;

	/** The authentication manager. */
	private final AuthenticationManager authenticationManager;

	/** The token expiration msec. */
	@Value("${app.otp.tokenExpirationMsec}")
	private Integer tokenExpirationMsec;

	/**
	 * Load user by user name.
	 *
	 * @param username the String
	 * @return the user details
	 * @throws UsernameNotFoundException if the user name is not found
	 */
	@Override
	public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
		Optional<User> user = userServiceToRepo.loadUserByUsername(username);
		Optional<Otp> otp = otpServiceToRepo.loadOtpByUsername(username);
		if (user.isEmpty()) {
			throw new UsernameNotFoundException("User not found with email : " + username);
		}
		if (otp.isPresent()) {
			if (otp.get().getExpired().before(new Date())) {
				throw new AccessDeniedException("Acces denied. Please request One Time Password");
			} else {
				return new UserPrincipal(user.get().getEmail(), otp.get().getPassword());
			}
		}
		return new UserPrincipal(user.get().getEmail(), "");
	}

	/**
	 * Request otp that will be send to user email.
	 *
	 * @param email the String
	 */
	public void requestOtp(@NonNull String email) {

		Optional<User> userO = userServiceToRepo.loadUserByUsername(email);
		if (userO.isEmpty()) {
			return;// user is not exist
		}

		Optional<Otp> otpO = otpServiceToRepo.loadOtpByUsername(email);
		Date now = new Date();
		if (otpO.isPresent()) {
			if (otpO.get().getExpired().after(now)) {
				return; // exist "old" ome-time password
			} else {
				otpServiceToRepo.deleteByEmail(email); // request new OTP
			}
		}

		Otp otp = new Otp();
		otp.setEmail(email);
		Date expiryDate = new Date(now.getTime() + tokenExpirationMsec);
		otp.setExpired(expiryDate);
		String password = UUID.randomUUID().toString();
		otp.setPassword(passwordEncoder.encode(password));

		otpO = otpServiceToRepo.save(otp);
		if (otpO.isEmpty()) {
			log.error("requestOtp] - Unknown error. OTP was not saved");
		}
		try {
			otpNotificationService.sendNotificationToUser(userO.get().getName(), userO.get().getEmail(), password);
		} catch (MailException e) {
			OtpMailException ex = new OtpMailException();
			ex.setErrMsg(e.getMessage());
			throw ex;
		} catch (InterruptedException e) {
			log.error("requestOtp] - InterruptedException: {}", e.getMessage());
		}

	}

	/**
	 * Creates the bearer token.
	 *
	 * @param email    the email
	 * @param password the password
	 * @return the string
	 */
	public String createBearerToken(@NonNull String email, @NonNull String password) {

		log.debug("createBearerToken] - Create Token base on authentication and return it.");

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(email, password));
		SecurityContextHolder.getContext().setAuthentication(authentication);

		String token = jwtTokenProvider.createToken(authentication);

		otpServiceToRepo.deleteByEmail(email);

		return token;
	}

	/**
	 * Gets the user by id.
	 *
	 * @param userId the user id
	 * @return the user by id
	 */
	@RolesAllowed({ "ROLE_USER" })
	public Optional<User> getUserById(@NonNull Long userId) {
		log.debug("getUserById] - Get User By Id: {}", userId);
		return userServiceToRepo.getUserById(userId);
	}

	/**
	 * Delete user by id.
	 *
	 * @param userId the user id
	 */
	@RolesAllowed({ "ROLE_USER" })
	public void deleteUserById(@NonNull Long userId) {
		log.debug("deleteUserById] - Delete User By Id: {}", userId);
		userServiceToRepo.deleteUserById(userId);
	}

	/**
	 * Update user by id.
	 *
	 * @param userId            the user id
	 * @param userUpdateRequest the user update request
	 * @return the optional
	 */
	@RolesAllowed({ "ROLE_USER" })
	public Optional<User> updateUserById(@NonNull Long userId, @NonNull UserUpdateRequest userUpdateRequest) {

		log.debug("updateUserById] - Update User By Id: {} with information: {}", userId, userUpdateRequest);

		trimSpacesUpdeteRequest(userUpdateRequest);

		AuditResponse response = checkUserUpdateRequest(userUpdateRequest);

		if (response.isInvalid()) {
			log.info("IN updateFacility - User update is fail. Inform to the updater");

			UserUpdateRequestAuditException ex = new UserUpdateRequestAuditException(response);
			ex.setErrMsg(
					"Bad request for update User. Please check your request for consistent of documentation.");
			throw ex;
		}

		Optional<User> userO = userServiceToRepo.getUserById(userId);

		if (userO.isEmpty()) {
			UserNotFoundException ex = new UserNotFoundException();
			ex.setErrMsg("User wit Id:" + userId + " not found");
			throw ex;
		}

		User u = userO.get();

		fillUserUpdatedProperties(userUpdateRequest, u);

		return userServiceToRepo.saveUser(u);
	}

	/**
	 * Fill user updated properties.
	 *
	 * @param userUpdateRequest the user update request
	 * @param user              the user
	 */
	private void fillUserUpdatedProperties(@NonNull UserUpdateRequest userUpdateRequest, User user) {
		if (userUpdateRequest.getName() != null) {
			user.setName(userUpdateRequest.getName());
		}
		if (userUpdateRequest.getLastName() != null) {
			user.setLastName(userUpdateRequest.getLastName());
		}
		if (userUpdateRequest.getMaritalStatus() != null) {
			user.setMaritalStatus(MaritalStatus.valueOf(userUpdateRequest.getMaritalStatus()));
		}
		if (userUpdateRequest.getBirthDay() != null) {
			user.setBirthDay(LocalDate.parse(userUpdateRequest.getBirthDay()));
		}
	}

	/**
	 * Trim spaces in update request.
	 *
	 * @param userUpdateRequest the user update request
	 */
	private void trimSpacesUpdeteRequest(UserUpdateRequest userUpdateRequest) {
		userUpdateRequest.setBirthDay(StringUtils.trimToNull(userUpdateRequest.getBirthDay()));
		userUpdateRequest.setLastName(StringUtils.trimToNull(userUpdateRequest.getLastName()));
		userUpdateRequest.setMaritalStatus(StringUtils.trimToNull(userUpdateRequest.getMaritalStatus()));
		userUpdateRequest.setName(StringUtils.trimToNull(userUpdateRequest.getName()));
	}

	/**
	 * Audit user update request.
	 *
	 * @param userUpdateRequest the user update request
	 * @return the audit response
	 */
	private AuditResponse checkUserUpdateRequest(@NonNull UserUpdateRequest userUpdateRequest) {
		AuditResponse response = new AuditResponse();

		response.setValid(true);

		if (userUpdateRequest.getName() != null && userUpdateRequest.getName().isBlank()) {
			response.setValid(false);
			response.addMessage("name", "Field Name is too short.");
		}

		if (StringUtils.length(userUpdateRequest.getName()) > 75) {
			response.setValid(false);
			response.addMessage("name", "Field Name is too long.");
		}

		if (userUpdateRequest.getLastName() != null && userUpdateRequest.getLastName().isBlank()) {
			response.setValid(false);
			response.addMessage("lastName", "Field LastName is too short.");
		}

		if (StringUtils.length(userUpdateRequest.getLastName()) > 75) {
			response.setValid(false);
			response.addMessage("lastName", "Field LastName is too long.");
		}

		if (userUpdateRequest.getMaritalStatus() != null) {
			try {
				MaritalStatus s = MaritalStatus.valueOf(userUpdateRequest.getMaritalStatus());
			} catch (Exception e) {
				response.setValid(false);
				response.addMessage("maritalStatus", "This status is wrong.");
			}
		}
		if (userUpdateRequest.getBirthDay() != null) {
			if (StringUtils.length(userUpdateRequest.getBirthDay()) < 8) {
				response.setValid(false);
				response.addMessage("birthDay", "Field BirthDay is wrong");
			} else {
				try {
					LocalDate ld = LocalDate.parse(userUpdateRequest.getBirthDay());
					if (ld.isAfter(LocalDate.now())) {
						response.setValid(false);
						response.addMessage("birthDay", "BirthDay date is in future");
					}
				} catch (Exception e) {
					response.setValid(false);
					response.addMessage("birthDay", "Format field of BirthDay is wrong. Use format 'yyyy-MM-dd'");
				}
			}
		}

		return response;
	}

	/**
	 * Register new user.
	 *
	 * @param newUser the new user
	 * @return the optional
	 */
	@RolesAllowed({ "ROLE_USER" })
	public Optional<User> registerNewUser(@NonNull UserCreateRequest newUser) {
		log.debug("registerNewUser] - Creqte User with information: {}", newUser);

		trimSpacesCreatedUser(newUser);

		AuditResponse response = checkUserCreateRequest(newUser);

		if (response.isInvalid()) {
			log.info("registerNewUser] - User create is fail. Inform to the updater");

			UserCreateRequestAuditException ex = new UserCreateRequestAuditException(response);
			ex.setErrMsg(
					"Bad request for create User. Please check your request for consistent of documentation.");
			throw ex;
		}

		User user = new User();

		fillUserCreatedProperties(newUser, user);

		return userServiceToRepo.saveUser(user);
	}

	/**
	 * Fill user created properties.
	 *
	 * @param newUser the new user
	 * @param user    the user
	 */
	private void fillUserCreatedProperties(@NonNull UserCreateRequest newUser, @NonNull User user) {
		user.setName(newUser.getName());
		user.setLastName(newUser.getLastName());
		user.setEmail(newUser.getEmail());
		user.setBirthDay(LocalDate.parse(newUser.getBirthDay()));
		user.setMaritalStatus(MaritalStatus.valueOf(newUser.getMaritalStatus()));
	}

	/**
	 * Audit user create request.
	 *
	 * @param newUser the new user
	 * @return the audit response
	 */
	private AuditResponse checkUserCreateRequest(@NonNull UserCreateRequest newUser) {
		AuditResponse response = new AuditResponse();

		response.setValid(true);

		if (StringUtils.length(newUser.getName()) < 1) {
			response.setValid(false);
			response.addMessage("name", "Field Name is too short.");
		}

		if (StringUtils.length(newUser.getName()) > 75) {
			response.setValid(false);
			response.addMessage("name", "Field Name is too long.");
		}

		if (StringUtils.length(newUser.getLastName()) < 1) {
			response.setValid(false);
			response.addMessage("lastName", "Field LastName is too short.");
		}

		if (StringUtils.length(newUser.getLastName()) > 75) {
			response.setValid(false);
			response.addMessage("lastName", "Field LastName is too long.");
		}

		if (newUser.getMaritalStatus() != null) {
			try {
				MaritalStatus s = MaritalStatus.valueOf(newUser.getMaritalStatus());
			} catch (Exception e) {
				response.setValid(false);
				response.addMessage("maritalStatus", "This status is wrong.");
			}
		}
		if (StringUtils.length(newUser.getBirthDay()) < 8) {
			response.setValid(false);
			response.addMessage("birthDay", "Field BirthDay is wrong");
		} else {
			try {
				LocalDate ld = LocalDate.parse(newUser.getBirthDay());
				if (ld.isAfter(LocalDate.now())) {
					response.setValid(false);
					response.addMessage("birthDay", "BirthDay date is in future");
				}
			} catch (Exception e) {
				response.setValid(false);
				response.addMessage("birthDay", "Format field of BirthDay is wrong. Use format 'yyyy-MM-dd'.");
			}
		}
		if (StringUtils.length(newUser.getLastName()) < 5
				|| !EmailValidator.getInstance().isValid(newUser.getEmail())) {
			response.setValid(false);
			response.addMessage("email", "It's not like an email");
		}

		return response;
	}

	/**
	 * Trim spaces created user.
	 *
	 * @param newUser the new user
	 */
	private void trimSpacesCreatedUser(@NonNull UserCreateRequest newUser) {
		newUser.setBirthDay(StringUtils.trimToNull(newUser.getBirthDay()));
		newUser.setLastName(StringUtils.trimToNull(newUser.getLastName()));
		newUser.setMaritalStatus(StringUtils.trimToNull(newUser.getMaritalStatus()));
		newUser.setName(StringUtils.trimToNull(newUser.getName()));
		newUser.setEmail(StringUtils.trimToNull(newUser.getEmail()));
	}

	/**
	 * Gets the all users.
	 *
	 * @return the all users
	 */
	@RolesAllowed({ "ROLE_USER" })
	public List<User> getAllUsers() {
		log.debug("getAllUsers] - Get all Users");
		return userServiceToRepo.getAllUsers();
	}
}
