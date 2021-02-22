package depavlo.util.exception;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

import depavlo.util.AuditResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * The Class AuditException. The AuditException class is the basis for
 * exceptions that occur when check information is fail.
 * 
 * @author Pavlo Degtyaryev
 */
@Getter
@Setter
public class AuditException extends CustomGenericException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4728309790778116393L;

	/** The audit messages. */
	private Multimap<String, String> auditMessages = TreeMultimap.create();

	/**
	 * Instantiates a new audit exception.
	 *
	 * @param audit the audit Response
	 */
	public AuditException(AuditResponse audit) {
		audit.getMessages().forEach((key, value) -> auditMessages.put(key, value));
	}

	/**
	 * Adds the audit message to the Audit messages.
	 *
	 * @param key     the key
	 * @param message the message
	 */
	public void addAuditMessage(String key, String message) {
		auditMessages.put(key, message);
	}
}
