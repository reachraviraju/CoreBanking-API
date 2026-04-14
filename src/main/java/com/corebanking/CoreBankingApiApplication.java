package com.corebanking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.corebanking.security.JwtProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class CoreBankingApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoreBankingApiApplication.class, args);
	}
}