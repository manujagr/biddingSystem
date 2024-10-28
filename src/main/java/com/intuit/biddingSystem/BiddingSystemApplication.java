package com.intuit.biddingSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BiddingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(BiddingSystemApplication.class, args);
	}
}
