package depavlo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class OtpNotificationService that asynchronously send requested otp to
 * user.
 * 
 * @author Pavlo Degtyaryev
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpNotificationService {

	/** The java mail sender. */
	private final JavaMailSender javaMailSender;

	/** The sender in email field "From". */
	@Value("${app.otp.sender}")
	private String sender;

	/**
	 * Asynchronously send requested OTP to user.
	 *
	 * @param username the User Name
	 * @param email    the User email
	 * @param key      the OTP
	 * @throws MailException        the mail exception
	 * @throws InterruptedException the interrupted exception
	 */
	@Async
	public void sendNotificationToUser(@NonNull String username, @NonNull String email, @NonNull String key)
			throws MailException, InterruptedException {

		log.debug("sendNotificationToUser] - Perform send OTP key to User: {}", email);

		StringBuffer sb = new StringBuffer();

		sb.append("Hello, ").append(username).append(".\n\n");
		sb.append("Someone requested a one-time password for this email.\n");
		sb.append("If it was you, copy the password below and use it during authorization.\n");
		sb.append("If it wasn't you, just ignore this letter.\n");
		sb.append("If you believe that something is going wrong, contact our service.\n\n");
		sb.append("One-time password: ").append(key);

		SimpleMailMessage mail = new SimpleMailMessage();

		mail.setTo(email);
		mail.setFrom(sender);
		mail.setSubject("OTP requested.");
		mail.setText(sb.toString());

		javaMailSender.send(mail);

		log.info("sendNotificationToUser] - Email to User: '{}' with OPT was sent to Email: '{}'.", username, email);
	}

}
