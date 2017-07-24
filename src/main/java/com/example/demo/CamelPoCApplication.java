	package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.controller"})
@ComponentScan(basePackages = {"com.example.service"})
@ComponentScan(basePackages = {"com.example.configuration"})
@EntityScan(basePackages = { "com.example.model" })
@EnableJpaRepositories(basePackages = { "com.example.repository" })

public class CamelPoCApplication {

	public static void main(String[] args) {
		
		SpringApplication.run(CamelPoCApplication.class, args);
	}
}
