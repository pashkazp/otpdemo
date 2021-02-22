package depavlo.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import depavlo.security.jwt.JwtAuthenticationFilter;
import depavlo.service.UserService;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class SecurityConfig.
 * 
 * @author Pavlo Degtyaryev
 */
@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

	/** The user service. */
	@Autowired
	private UserService userService;

	/** The password encoder. */
	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * Authentication provider.
	 *
	 * @return the authentication provider
	 */
	@Bean
	public AuthenticationProvider authenticationProvider() {
		log.info("authenticationProvider] - Create AuthenticationProvider");
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userService);
		provider.setPasswordEncoder(passwordEncoder);
		return provider;
	}

	/**
	 * Configure global.
	 *
	 * @param auth the auth
	 * @throws Exception the exception
	 */
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userService).passwordEncoder(passwordEncoder);
	}

	/**
	 * The Class RestConfiguration.
	 */
	@Order(1)
	@Configuration
	public static class RestConfiguration extends WebSecurityConfigurerAdapter {

		/** The jwt authentication filter. */
		@Autowired
		private JwtAuthenticationFilter jwtAuthenticationFilter;

		/**
		 * Configure.
		 *
		 * @param http the http
		 * @throws Exception the exception
		 */
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http
					.antMatcher("/api/v1" + "/**")
					.cors()
					.and()
					.csrf()
					.disable() // we don't need CSRF because our token is invulnerable
					.authorizeRequests()

					.antMatchers(HttpMethod.POST, "/api/v1/auth" + "/**").permitAll()

					.anyRequest().authenticated()

					.and()
					.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

			http.addFilterAfter(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		}

	}

	/**
	 * The Class WebConfiguration.
	 */
	@Order(2)
	@Configuration
	public static class WebConfiguration extends WebSecurityConfigurerAdapter {

		/** The jwt authentication filter. */
		@Autowired
		private JwtAuthenticationFilter jwtAuthenticationFilter;

		/** The user service. */
		@Autowired
		private UserService userService;

		/** The password encoder. */
		@Autowired
		PasswordEncoder passwordEncoder;

		/**
		 * Configure AuthenticationManagerBuilder.
		 *
		 * @param authenticationManagerBuilder the authentication manager builder
		 * @throws Exception the exception
		 */
		@Override
		public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
			authenticationManagerBuilder
					.userDetailsService(userService)
					.passwordEncoder(passwordEncoder);
		}

		/**
		 * Configure WebSecurity.
		 *
		 * @param web the web
		 * @throws Exception the exception
		 */
		@Override
		public void configure(WebSecurity web) throws Exception {
			web
					.ignoring()
					.antMatchers("/resources/**")
					.antMatchers("/publics/**")
					.antMatchers("/resources/**")
					// the standard favicon URI
					.antMatchers("/favicon.ico")

					// "/images/.*",
					.antMatchers("/registration/**")
					.antMatchers("/js/**")
					.antMatchers("/css/**")
					.antMatchers("/img/**")
					.antMatchers("/webjars/**")
					.antMatchers("/assets/**")
					// the robots exclusion standard
					.antMatchers("/robots.txt")
					// web application manifest
					.antMatchers("/manifest.webmanifest")
					.antMatchers("/sw.js")
					.antMatchers("/offline-page.html");
		}

		/**
		 * Configure.
		 *
		 * @param http the http
		 * @throws Exception the exception
		 */
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			log.info("configure] - Configure HttpSecurity");
			// @formatter:off

	        http.headers().frameOptions().sameOrigin();
	        http.csrf().ignoringAntMatchers("/h2-console/**");
			http
					.cors()
					.and()
					.sessionManagement()
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
					.and()
					.csrf().disable() // CSRF handled

					.authorizeRequests()
					
					.antMatchers("/h2-console/**").permitAll()

					.antMatchers("/**").fullyAuthenticated()

					.regexMatchers(HttpMethod.POST, "/\\?v-r=.*").permitAll()

					.and()
					.logout()
					.invalidateHttpSession(true)
					.clearAuthentication(true)
					.permitAll();

			// Add our custom Token based authentication filter
			http.addFilterAfter(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
		}
	}

}
