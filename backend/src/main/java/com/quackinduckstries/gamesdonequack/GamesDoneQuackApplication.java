package com.quackinduckstries.gamesdonequack;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GamesDoneQuackApplication {

	public static void main(String[] args) {
		SpringApplication.run(GamesDoneQuackApplication.class, args);
	}
	@Bean
    CommandLineRunner testBeans(ApplicationContext ctx) {
        return args -> {
            System.out.println("=== Checking for UserMapper bean ===");
            String[] beanNames = ctx.getBeanDefinitionNames();
            for (String name : beanNames) {
                if (name.toLowerCase().contains("mapper")) {
                    System.out.println("Found bean: " + name);
                }
            }
        };
    }
}
