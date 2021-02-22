package depavlo.service;

import java.util.Date;
import java.util.Optional;

import org.springframework.stereotype.Service;

import depavlo.model.Otp;
import lombok.NonNull;

/**
 * The Interface OtpServiceToRepo is description of the required functions of
 * the OTP storage service.
 * 
 * @author Pavlo Degtyaryev
 */
@Service
public interface OtpServiceToRepo {

	/**
	 * Load otp by username.
	 *
	 * @param username the username
	 * @return the optional
	 */
	Optional<Otp> loadOtpByUsername(@NonNull String username);

	/**
	 * Save otp.
	 *
	 * @param otp the otp
	 * @return the optional
	 */
	Optional<Otp> save(@NonNull Otp otp);

	/**
	 * Delete otp by email.
	 *
	 * @param email the email
	 */
	void deleteByEmail(@NonNull String email);

	/**
	 * Delete the old otp that are before the date
	 *
	 * @param date the date
	 */
	void deleteOtpBefore(Date date);

}
