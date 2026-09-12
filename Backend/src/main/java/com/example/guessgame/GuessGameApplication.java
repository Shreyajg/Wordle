package com.example.guessgame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GuessGameApplication {

	public static void main(String[] args) {
		System.out.println(
			"SPRING MONGO = " +
			System.getProperty("spring.data.mongodb.uri")
		);
		System.out.println("MONGO URI = " + System.getenv("MONGODB_URI"));
		SpringApplication.run(GuessGameApplication.class, args);
	}

}
