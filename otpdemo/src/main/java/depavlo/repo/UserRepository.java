package depavlo.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import depavlo.model.User;

/**
 * The Interface UserRepository.
 * 
 * @author Pavlo Degtyaryev
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	/**
	 * Find User by email ignore case.
	 *
	 * @param email the email
	 * @return the optional
	 */
	Optional<User> findByEmailIgnoreCase(String email);
}
