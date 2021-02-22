package depavlo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import depavlo.util.SpringApplicationContext;

@SpringBootApplication
@Configuration
@EnableAutoConfiguration
@EnableWebSecurity
@EntityScan({ "depavlo.model" })
@EnableJpaRepositories({ "depavlo.repo" })
@ComponentScan({ "depavlo" })
@EnableScheduling
@EnableAsync
//@EnableConfigurationProperties(OtpDemoApplication.class)
public class OtpDemoApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(OtpDemoApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(OtpDemoApplication.class, args);
	}

	@Bean
	public SpringApplicationContext springApplicationContext() {
		return new SpringApplicationContext();
	}

}
