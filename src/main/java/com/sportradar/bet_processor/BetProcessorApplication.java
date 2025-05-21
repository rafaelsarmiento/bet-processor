package com.sportradar.bet_processor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BetProcessorApplication {

	public static void main(String[] args) {
		SpringApplication.run(BetProcessorApplication.class, args);
	}

}
