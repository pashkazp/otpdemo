package depavlo.repo.serviceimpl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import depavlo.model.User;
import depavlo.repo.UserRepository;
import depavlo.service.UserServiceToRepo;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class UserServiceToRepoImpl.
 * 
 * @author Pavlo Degtyaryev
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserServiceToRepoImpl implements UserServiceToRepo {

	/** The UserRepository dao. */
	private final UserRepository dao;

	/**
	 * Load user by username.
	 *
	 * @param username the username
	 * @return the optional
	 */
	@Override
	public Optional<User> loadUserByUsername(@NonNull String username) {
		log.debug("loadUserByUsername] - Perform to load User by Email '{}'", username);
		return dao.findByEmailIgnoreCase(username);
	}

	/**
	 * Gets the user by id.
	 *
	 * @param userId the user id
	 * @return the user by id
	 */
	@Override
	public Optional<User> getUserById(@NonNull Long userId) {
		log.debug("getUserById] - Perform to load User by Id: {}", userId);
		return dao.findById(userId);
	}

	/**
	 * Delete user by id.
	 *
	 * @param userId the user id
	 */
	@Override
	public void deleteUserById(@NonNull Long userId) {
		log.debug("deleteUserById] - Perform to delete User by Id: {}", userId);
		dao.deleteById(userId);
	}

	/**
	 * Save user.
	 *
	 * @param user the user
	 * @return the optional
	 */
	@Override
	public Optional<User> saveUser(@NonNull User user) {
		log.debug("saveUser] - Perform to save User: {}", user);
		return Optional.of(dao.save(user));
	}

	/**
	 * Gets the all users.
	 *
	 * @return the all users
	 */
	@Override
	public List<User> getAllUsers() {
		log.debug("getAllUsers] - Get all Users");
		return dao.findAll();
	}

}
