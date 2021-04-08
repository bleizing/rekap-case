package com.bleizing.rekapcase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.bleizing.rekapcase.property.FileStorageProperties;

@SpringBootApplication
@EnableJpaAuditing
@EnableConfigurationProperties({
    FileStorageProperties.class
})
public class RekapCaseApplication {

	public static void main(String[] args) {
		System.out.println("app started");
		SpringApplication.run(RekapCaseApplication.class, args);
	}

}
