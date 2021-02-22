package depavlo.security.jwt;

import java.io.Serializable;
import java.util.Date;
import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import depavlo.util.exception.JwtAuthenticationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class JwtTokenProvider which ensures the operation of the security token.
 * 
 * @author Pavlo Degtyaryev
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider implements Serializable {

	private static final long serialVersionUID = -2564335944320546370L;

	/** The token expiration msec. */
	@Value("${app.auth.tokenExpirationMsec}")
	private Integer tokenExpirationMsec;

	/** The token issurer. */
	@Value("${app.auth.tokenIssurer}")
	private String tokenIssurer;

	/** The token secret. */
	@Value("${app.auth.tokenSecret}")
	private String tokenSecret;

	/** The token prefix. */
	@Value("${app.auth.tokenPrefix}")
	private String tokenPrefix;

	/**
	 * Creates the token.
	 *
	 * @param authentication the authentication
	 * @return the string
	 */
	public String createToken(Authentication authentication) {
		log.debug("createToken] - Get Principal Authentication '{}' and create JWT token", authentication);
		UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

		Claims claims = Jwts.claims().setSubject(userPrincipal.getUsername());
		claims.put("scopes", userPrincipal.getAuthorities());

		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + tokenExpirationMsec);
		return Jwts.builder()
				.setClaims(claims)
				.setIssuer(tokenIssurer)
				.setIssuedAt(now)
				.setExpiration(expiryDate)
				.signWith(SignatureAlgorithm.HS512, tokenSecret)
				.compact();
	}

	/**
	 * Checks if is token expired. Returns the true value if the expiration date
	 * taken from the token exceeds the current date or if more milliseconds have
	 * elapsed since the token was created than specified in the AppProperties
	 *
	 * @param token the token String
	 * @return the boolean
	 */
	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date())
				|| (new Date().getTime() - getIssuedDateFromToken(token).getTime()) > tokenExpirationMsec;
	}

	/**
	 * Gets the expiration date from token.
	 *
	 * @param token the token
	 * @return the expiration date from token
	 */
	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	/**
	 * Gets the issued date from token.
	 *
	 * @param token the token
	 * @return the issued date from token
	 */
	public Date getIssuedDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getIssuedAt);
	}

	/**
	 * Gets the username from token.
	 *
	 * @param token the token
	 * @return the username from token
	 */
	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	/**
	 * Gets the claim from token.
	 *
	 * @param <T>            the generic type
	 * @param token          the token
	 * @param claimsResolver the claims resolver
	 * @return the claim from token
	 */
	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	/**
	 * Gets the all claims from token.
	 *
	 * @param token the token
	 * @return the all claims from token
	 */
	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser()
				.setSigningKey(tokenSecret)
				.parseClaimsJws(token)
				.getBody();
	}

	/**
	 * Validate token.
	 *
	 * @param token       the token
	 * @param userDetails the user details
	 * @return the boolean
	 */
	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = getUsernameFromToken(token);
		return (username.equals(userDetails.getUsername())
				&& !isTokenExpired(token));
	}

	/**
	 * Validate token.
	 *
	 * @param token the token
	 * @return true, if successful
	 */
	public boolean validateToken(String token) {
		try {
			Jws<Claims> claims = Jwts.parser().setSigningKey(tokenSecret)
					.parseClaimsJws(token);

			if (claims.getBody().getExpiration().before(new Date())) {
				return false;
			}

			return true;
		} catch (JwtException | IllegalArgumentException e) {
			throw new JwtAuthenticationException("JWT token is expired or invalid");
		}
	}

	/**
	 * Resolve token.
	 *
	 * @param request the request
	 * @return the string
	 */
	public String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith(tokenPrefix)) {
			return bearerToken.substring(7, bearerToken.length());
		}
		return null;
	}

}
