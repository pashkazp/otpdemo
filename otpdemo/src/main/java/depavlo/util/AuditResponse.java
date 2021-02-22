
package depavlo.util;

import java.io.Serializable;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * This class represents the response of the service performing data validation
 * 
 * @author Pavlo Degtyaryev
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class AuditResponse implements Serializable {

	private static final long serialVersionUID = -487372227707050769L;

	/** the truth means that all checks have been completed successfully */
	private boolean valid;

	/** The map of messages where field name is key */
	private Multimap<String, String> messages = TreeMultimap.create();

	/**
	 * Adds the error message.
	 *
	 * @param key     the field field name
	 * @param message the error message about field
	 */
	public void addMessage(String key, String message) {
		messages.put(key, message);
	}

	/**
	 * Checks if is empty the messages list.
	 *
	 * @return true, if is empty
	 */
	public boolean isEmpty() {
		return messages.isEmpty();
	}

	/**
	 * returns true if check fails
	 *
	 * @return true, if check is invalid
	 */
	public boolean isInvalid() {
		return !isValid();
	}
}
