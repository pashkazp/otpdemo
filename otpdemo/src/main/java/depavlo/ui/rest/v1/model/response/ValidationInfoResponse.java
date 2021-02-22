package depavlo.ui.rest.v1.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The Class ValidationInfoResponse is a unit of error information when
 * validating a field.
 * 
 * @author Pavlo Degtyaryev
 */

@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ValidationInfoResponse extends AbstractSubInfoResponse {

	/** The name of field. */
	private String field;

	/** The error message. */
	private String message;

	/**
	 * Instantiates a new validation info response.
	 *
	 * @param field   the String
	 * @param message the String
	 */
	public ValidationInfoResponse(String field, String message) {
		this.field = field;
		this.message = message;
	}
}
