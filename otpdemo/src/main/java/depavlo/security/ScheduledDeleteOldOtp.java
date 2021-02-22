package depavlo.security;

import java.util.Date;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import depavlo.service.OtpServiceToRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class ScheduledDeleteOldOtp that periodically deletes old OPT.
 * 
 * @author Pavlo Degtyaryev
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledDeleteOldOtp {

	/** The otp service to repo. */
	private final OtpServiceToRepo otpServiceToRepo;

	/**
	 * Delete old otp by Cron.
	 */
	@Scheduled(cron = "${app.otp.deleteOldOtpCronStr}")
	public void deleteOldOtp() {
		Date date = new Date();
		log.debug("deleteOldOtp] - perform delete OTP that has expired on Date: {}", date);
		otpServiceToRepo.deleteOtpBefore(date);
	}
}
