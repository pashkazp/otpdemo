package depavlo.util.exception;

import depavlo.util.AuditResponse;

/**
 * The Class UserUpdateRequestAuditException that thrown when check of update
 * request i fail.
 * 
 * @author Pavlo Degtyaryev
 */
public class UserUpdateRequestAuditException extends AuditException {

	private static final long serialVersionUID = -5716722215063081565L;

	/**
	 * Instantiates a new user update request audit exception.
	 *
	 * @param audit the audit Response
	 */
	public UserUpdateRequestAuditException(AuditResponse audit) {
		super(audit);
	}

}
