package depavlo.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.NonNull;

/**
 * The Class UserPrincipal.
 * 
 * @author Pavlo Degtyaryev
 */
public class UserPrincipal implements UserDetails {

	private static final long serialVersionUID = -7212256501026763458L;

	/** The user name. */
	private final String username;

	/** The user password. */
	private String password;

	/**
	 * Instantiates a new user principal.
	 *
	 * @param username the user name
	 * @param password the password
	 */
	public UserPrincipal(@NonNull String username, String password) {
		this.username = username;
		this.password = password;
	}

	/**
	 * Gets the user authorities.
	 *
	 * @return the authorities
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_USER"));
	}

	/**
	 * Gets the user password.
	 *
	 * @return the password
	 */
	@Override
	public String getPassword() {
		return this.password;
	}

	/**
	 * Gets the user name.
	 *
	 * @return the username
	 */
	@Override
	public String getUsername() {
		return this.username;
	}

	/**
	 * Checks if is account non expired.
	 *
	 * @return true, if is account non expired
	 */
	@Override
	public boolean isAccountNonExpired() {
		return isEnabled();
	}

	/**
	 * Checks if is account non locked.
	 *
	 * @return true, if is account non locked
	 */
	@Override
	public boolean isAccountNonLocked() {
		return isEnabled();
	}

	/**
	 * Checks if is credentials non expired.
	 *
	 * @return true, if is credentials non expired
	 */
	@Override
	public boolean isCredentialsNonExpired() {
		return isEnabled();
	}

	/**
	 * Checks if is enabled.
	 *
	 * @return true, if is enabled
	 */
	@Override
	public boolean isEnabled() {
		return true;
	}

}
