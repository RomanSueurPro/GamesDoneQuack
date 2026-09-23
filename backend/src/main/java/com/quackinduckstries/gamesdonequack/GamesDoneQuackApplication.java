package com.quackinduckstries.gamesdonequack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class GamesDoneQuackApplication {

	public static void main(String[] args) {
		SpringApplication.run(GamesDoneQuackApplication.class, args);
	}
	
}
