package depavlo.util.exception;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The CustomGenericException class is the basis for exceptions that occur when
 * certain errors occur.
 * 
 * @author Pavlo Degtyaryev
 */
@Getter
@Setter
@NoArgsConstructor
public class CustomGenericException extends RuntimeException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4902468204270195408L;

	/** The err code. */
	private String errCode = "";

	/** The err message. */
	private String errMsg = "";

	/**
	 * Instantiates a new custom generic exception.
	 *
	 * @param errCode the error code
	 * @param errMsg  the error message
	 */
	public CustomGenericException(String errCode, String errMsg) {
		super();
		this.errCode = StringUtils.defaultString(errCode);
		this.errMsg = StringUtils.defaultString(errMsg);
	}
}
