package com.bsi.pusbin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PusbinApplication {

	public static void main(String[] args) {
		SpringApplication.run(PusbinApplication.class, args);
	}

}
