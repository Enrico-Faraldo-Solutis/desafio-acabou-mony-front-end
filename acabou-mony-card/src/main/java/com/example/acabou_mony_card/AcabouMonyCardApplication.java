package com.example.acabou_mony_card;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class AcabouMonyCardApplication {

	public static void main(String[] args) {
		SpringApplication.run(AcabouMonyCardApplication.class, args);
	}

}
