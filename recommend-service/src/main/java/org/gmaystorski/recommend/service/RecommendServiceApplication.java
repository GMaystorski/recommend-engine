package org.gmaystorski.recommend.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "org.gmaystorski.recommend")
public class RecommendServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RecommendServiceApplication.class, args);
	}

}
