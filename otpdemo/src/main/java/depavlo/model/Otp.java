package depavlo.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Class that represent One-time password.
 * 
 * @author Pavlo Degtyaryev
 */
@EqualsAndHashCode(of = { "id" })
@Getter
@Setter
@NoArgsConstructor
@Table(name = "otp")
@Entity
public class Otp implements Serializable {

	private static final long serialVersionUID = -1570711734749425199L;

	/** The OTP id. */
	@Id
	@Column(name = "otp_id", updatable = false, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/** The User email. */
	@Column(name = "email", unique = true, length = 100, nullable = false)
	private String email = "";

	/** The OTP password. */
	@Column(name = "otp", unique = false, length = 255, nullable = false)
	private String password = "";

	/** The OTP expired after. */
	@Column(name = "expired", unique = false, nullable = false)
	private Date expired;

}
