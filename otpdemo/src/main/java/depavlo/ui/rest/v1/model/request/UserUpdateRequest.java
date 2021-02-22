package depavlo.ui.rest.v1.model.request;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Class UserUpdateRequest represent a request to update user information.
 *
 * @author Pavlo Degtyaryev
 */
@ToString
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserUpdateRequest implements Serializable {

	private static final long serialVersionUID = 7004015989741474135L;

	/** The user name. */
	private String name;

	/** The user last name. */
	private String lastName;

	/** The user birth day. */
	private String birthDay;

	/** The user marital status. */
	private String maritalStatus;

}
