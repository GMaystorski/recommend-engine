package org.gmaystorski.recommend.service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class TestRunner implements ApplicationRunner {

	@Autowired
	private RecommendService service;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		Map<Production, Double> recommendations = service.processInput(
				Arrays.asList("Brad Pitt", "Charlie Cox", "Scarlett Johansson", "Robert Downey Jr.", "Chris Hemsworth"),
				Arrays.asList("The Matrix", "Marvel's The Defenders", "Marvel's Daredevil", "Men in Black",
						"Pulp Fiction", "American Psycho", "The Avengers"),
				Arrays.asList("Action & Adventure", "Dramas", "TV Action & Adventure"));
		recommendations.entrySet().stream().sorted(Comparator.comparingDouble(entry -> entry.getValue()))
				.forEachOrdered(entry -> System.out.println(
						"Recommended movie: " + entry.getKey().getTitle() + " with score: " + entry.getValue()));
	}

}
