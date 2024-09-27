package com.acoldbottle.todolist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@EnableJpaAuditing
@SpringBootApplication
public class todolistApplication {

	public static void main(String[] args) {
		SpringApplication.run(todolistApplication.class, args);
	}

}
