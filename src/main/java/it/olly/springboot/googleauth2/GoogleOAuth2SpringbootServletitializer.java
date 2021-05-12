package it.olly.springboot.googleauth2;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class GoogleOAuth2SpringbootServletitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(GoogleOAuth2SpringbootApplication.class);
	}

}
