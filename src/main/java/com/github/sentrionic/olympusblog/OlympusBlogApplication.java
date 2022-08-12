package com.github.sentrionic.olympusblog;

import com.github.sentrionic.olympusblog.config.SwaggerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@Import(SwaggerConfiguration.class)
@EnableAsync
public class OlympusBlogApplication {

	public static void main(String[] args) {
		SpringApplication.run(OlympusBlogApplication.class, args);
	}

}
