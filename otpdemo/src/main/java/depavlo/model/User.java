package depavlo.model;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import depavlo.util.MaritalStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Class that represent User.
 * 
 * @author Pavlo Degtyaryev
 */
@EqualsAndHashCode(of = { "id" })
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users")
@Entity
public class User implements Serializable {

	private static final long serialVersionUID = -8821124537127138954L;

	/** The User id. */
	@Id
	@Column(name = "user_id", updatable = false, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** The User email. */
	@Column(name = "email", unique = true, length = 100, nullable = false)
	private String email = "";

	/** The User name. */
	@Column(name = "name", unique = false, length = 75, nullable = false)
	private String name = "";

	/** The User last name. */
	@Column(name = "last_name", unique = false, length = 75, nullable = false)
	private String lastName = "";

	/** The User birth day. */
	@Column(name = "birth_day", unique = false, nullable = false)
	private LocalDate birthDay;

	/** The User marital status. */
	@Column(name = "marital_status", nullable = false)
	@Enumerated(EnumType.STRING)
	private MaritalStatus maritalStatus;

}
