package depavlo.util.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * The Class JwtAuthenticationException that thrown if JWT token is expired or
 * invalid.
 * 
 * @author Pavlo Degtyaryev
 */
public class JwtAuthenticationException extends AuthenticationException {

	private static final long serialVersionUID = -9164463982419539453L;

	/**
	 * Instantiates a new jwt authentication exception.
	 *
	 * @param msg the message
	 * @param t   the Exception
	 */
	public JwtAuthenticationException(String msg, Throwable t) {
		super(msg, t);
	}

	/**
	 * Instantiates a new jwt authentication exception.
	 *
	 * @param msg the message
	 */
	public JwtAuthenticationException(String msg) {
		super(msg);
	}
}
