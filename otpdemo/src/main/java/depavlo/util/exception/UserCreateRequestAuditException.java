package depavlo.util.exception;

import depavlo.util.AuditResponse;

/**
 * The Class UserCreateRequestAuditException that thrown when check of create
 * request i fail.
 * 
 * @author Pavlo Degtyaryev
 */
public class UserCreateRequestAuditException extends AuditException {

	private static final long serialVersionUID = 7945648546699233676L;

	/**
	 * Instantiates a new user create request audit exception.
	 *
	 * @param audit the audit
	 */
	public UserCreateRequestAuditException(AuditResponse audit) {
		super(audit);
	}

}
