package org.gmaystorski.recommend.ingest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "org.gmaystorski.recommend")
public class DatasetIngestApplication {

	public static void main(String[] args) {
		SpringApplication.run(DatasetIngestApplication.class, args);
	}

}
