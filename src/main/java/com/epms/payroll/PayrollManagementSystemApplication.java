package com.epms.payroll;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class PayrollManagementSystemApplication {

	public static void main(String[] args) {

		SpringApplication.run(PayrollManagementSystemApplication.class, args);
		System.out.println("hello world");
	}
	@Configuration
	public static class WebConfig implements WebMvcConfigurer {

		@Override
		public void addCorsMappings(CorsRegistry registry) {
			registry.addMapping("/**")  // Apply CORS to all endpoints
					.allowedOrigins("http://localhost:4200")  // Allow requests from this origin
					.allowedMethods("GET", "POST", "PUT", "DELETE")  // Allow these HTTP methods
					.allowedHeaders("*")  // Allow all headers
					.allowCredentials(true);  // Allow cookies or authentication credentials
		}
	}

}
