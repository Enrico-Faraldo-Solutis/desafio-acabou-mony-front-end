package com.example.acabou_mony_transaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class AcabouMonyTransactionApplication {

	public static void main(String[] args) {
		SpringApplication.run(AcabouMonyTransactionApplication.class, args);
	}

	/*@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}*/

}

