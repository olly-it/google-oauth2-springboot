package it.olly.springboot.googleauth2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({ "it.olly" })
@EntityScan("it.olly")
public class GoogleOAuth2SpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(GoogleOAuth2SpringbootApplication.class, args);
    }

}
