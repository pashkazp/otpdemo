package depavlo.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import depavlo.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Instantiates a new JWT authentication filter that handle authentication
 * information and forward it to authentication content
 *
 * @author Pavlo Degtyaryev
 * @version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	/** The user details service. */
	private final UserService userService;

	/** The jwt token provider. */
	private final JwtTokenProvider jwtTokenProvider;

	/**
	 * Handle the request authentication information and try to set authentication
	 * content
	 *
	 * @param request  the HttpServletRequest
	 * @param response the HttpServletResponse
	 * @param chain    the FilterChain
	 * @throws IOException      Signals that an I/O exception has occurred.
	 * @throws ServletException the servlet exception
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String jwtInCookie = null;
		String username = null;

		// if cookie authentication is missing try to get JWT token from HTTP header
		jwtInCookie = StringUtils.defaultIfBlank(jwtInCookie, jwtTokenProvider.resolveToken(request));

		if (jwtInCookie != null) {
			try {
				username = jwtTokenProvider.getUsernameFromToken(jwtInCookie);
			} catch (IllegalArgumentException e) {
				log.error("doFilterInternal] - an error occured during getting username from token", e);
			} catch (ExpiredJwtException e) {
				log.warn("doFilterInternal] - the token is expired and not valid anymore", e);
			} catch (SignatureException e) {
				log.error("doFilterInternal] - Authentication Failed. Username or Password not valid.");
			}
		} else {
			log.debug("doFilterInternal] - couldn't find bearer string, will ignore the header");
		}
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

			try {
				UserDetails userDetails = userService.loadUserByUsername(username);

				if (!userDetails.isEnabled())
					throw new DisabledException("Authentication for user '" + username + "' is disabled");

				if (jwtTokenProvider.validateToken(jwtInCookie, userDetails)) {
					UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());
					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					log.info("doFilterInternal] - authenticated user " + username + ", setting security context");
					SecurityContextHolder.getContext().setAuthentication(authentication);
				}
			} catch (UsernameNotFoundException e) {
				log.warn("doFilterInternal] - catched UsernameNotFoundException: " + e.getMessage());
			} catch (DisabledException e) {
				log.warn("doFilterInternal] - catched DisabledException: " + e.getMessage());
			}

		}

		chain.doFilter(request, response);
	}
}