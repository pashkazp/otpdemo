package depavlo.ui.rest.v1.model.request;

import java.io.Serializable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Class UserCreateRequest represent a request to create new user information.
 *
 * @author Pavlo Degtyaryev
 */
@ToString
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserCreateRequest implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7004015989741474135L;

	/** The user email. */
	@NotBlank
	@Email
	private String email;

	/** The user name. */
	@NotBlank
	private String name;

	/** The user last name. */
	@NotBlank
	private String lastName;

	/** The user birth day. */
	@NotBlank
	private String birthDay;

	/** The user marital status. */
	@NotBlank
	private String maritalStatus;

}
