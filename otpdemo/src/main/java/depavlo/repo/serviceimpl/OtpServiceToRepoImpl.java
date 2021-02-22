package depavlo.repo.serviceimpl;

import java.util.Date;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import depavlo.model.Otp;
import depavlo.repo.OtpRepository;
import depavlo.service.OtpServiceToRepo;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class OtpServiceToRepoImpl.
 * 
 * @author Pavlo Degtyaryev
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OtpServiceToRepoImpl implements OtpServiceToRepo {

	/** The OtpRepository dao. */
	private final OtpRepository dao;

	/**
	 * Load otp by username.
	 *
	 * @param username the username
	 * @return the optional
	 */
	@Override
	public Optional<Otp> loadOtpByUsername(@NonNull String username) {
		log.debug("loadOtpByUsername] - Perform to load OTP by Email '{}'", username);
		return dao.findByEmailIgnoreCase(username);
	}

	/**
	 * Save otp.
	 *
	 * @param otp the otp
	 * @return the optional
	 */
	@Override
	public Optional<Otp> save(@NonNull Otp otp) {
		log.debug("save] - Perform to save OTP");
		return Optional.ofNullable(dao.saveAndFlush(otp));
	}

	/**
	 * Delete otp by email.
	 *
	 * @param email the email
	 */
	@Override
	public void deleteByEmail(@NonNull String email) {
		log.debug("deleteByEmail] - Perform to delete OTP by email: ", email);
		dao.deleteOtpByEmailIgnoreCase(email);
	}

	/**
	 * Delete otp before date.
	 *
	 * @param date the date
	 */
	@Override
	public void deleteOtpBefore(Date date) {
		dao.deleteByExpiredBefore(date);
	}

}
