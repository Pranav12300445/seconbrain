package com.pranav.second_brain_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.pranav.second_brain_backend.config.SupabaseProperties;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(SupabaseProperties.class)
public class SecondBrainBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecondBrainBackendApplication.class, args);
	}

}
