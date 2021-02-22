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
 * Class OTPRequest represent a request to get One-time password.
 *
 * @author Pavlo Degtyaryev
 */
@ToString
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OTPRequest implements Serializable {

	private static final long serialVersionUID = -7968827603112961996L;

	/** User emai to which otp will be sent. */
	@NotBlank
	@Email
	private String email;

}
