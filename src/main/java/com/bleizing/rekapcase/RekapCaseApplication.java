package com.bleizing.rekapcase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.bleizing.rekapcase.property.FileStorageProperties;

@SpringBootApplication
@EnableConfigurationProperties({
    FileStorageProperties.class
})
public class RekapCaseApplication {

	public static void main(String[] args) {
		System.out.println("app started");
		SpringApplication.run(RekapCaseApplication.class, args);
	}

}
