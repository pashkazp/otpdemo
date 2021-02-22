package depavlo.repo;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import depavlo.model.Otp;

/**
 * The Interface OtpRepository.
 * 
 * @author Pavlo Degtyaryev
 */
@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {

	/**
	 * Find OTP by email ignore case.
	 *
	 * @param email the email
	 * @return the optional
	 */
	Optional<Otp> findByEmailIgnoreCase(String email);

	/**
	 * Delete OTP by email ignore case.
	 *
	 * @param email the email
	 */
	void deleteOtpByEmailIgnoreCase(String email);

	/**
	 * Delete OTP by expired before.
	 *
	 * @param date the date
	 */
	void deleteByExpiredBefore(Date date);

}
