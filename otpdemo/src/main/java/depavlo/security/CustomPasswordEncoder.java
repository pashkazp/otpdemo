package depavlo.security;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * CustomPasswordEncoder class used to not encrypt the password
 * 
 * @author Pavlo Degtyaryev
 */
public class CustomPasswordEncoder implements PasswordEncoder {

	/**
	 * "Encode" password.
	 *
	 * @param rawPassword the raw password
	 * @return the string
	 */
	@Override
	public String encode(CharSequence rawPassword) {
		return rawPassword.toString();
	}

	/**
	 * Matches not encoded passwords.
	 *
	 * @param rawPassword     the raw password
	 * @param encodedPassword the encoded password
	 * @return true, if successful
	 */
	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		return rawPassword.toString().equals(encodedPassword);
	}

}
