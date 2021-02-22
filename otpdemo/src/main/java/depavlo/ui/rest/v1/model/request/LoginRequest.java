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
 * The LoginRequest class is a request for user authorization in the application
 * 
 * @author Pavlo Degtyaryev
 */

@ToString
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginRequest implements Serializable {

	private static final long serialVersionUID = -839018223186440069L;

	/** The user email. */
	@NotBlank
	@Email
	private String email;

	/** The user one-time password. */
	@NotBlank
	private String password;

}
