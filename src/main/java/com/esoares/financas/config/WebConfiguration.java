package com.esoares.financas.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@Configuration
public class WebConfiguration implements WebMvcConfigurer{

   	@Override
	public void addCorsMappings(CorsRegistry registry){  // <-----
		registry
				.addMapping("/**") // urls da minha api
				//.allowedOrigins(origins) // urls de onde vem as requisições
				.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
	}
}
