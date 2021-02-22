package depavlo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import depavlo.model.User;
import lombok.NonNull;

/**
 * The Interface UserServiceToRepo is description of the required functions of
 * the User storage service.
 * 
 * @author Pavlo Degtyaryev
 */
@Service
public interface UserServiceToRepo {

	/**
	 * Load user by username.
	 *
	 * @param username the String
	 * @return the optional
	 */
	Optional<User> loadUserByUsername(@NonNull String username);

	/**
	 * Gets the user by id.
	 *
	 * @param userId the Long
	 * @return the user by id
	 */
	Optional<User> getUserById(@NonNull Long userId);

	/**
	 * Delete user by id.
	 *
	 * @param userId the Long
	 */
	void deleteUserById(@NonNull Long userId);

	/**
	 * Save user.
	 *
	 * @param user the User
	 * @return the optional
	 */
	Optional<User> saveUser(@NonNull User user);

	/**
	 * Gets the list of all users.
	 *
	 * @return the all users
	 */
	List<User> getAllUsers();

}
