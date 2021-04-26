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
                Arrays.asList("Friends",
                        "Marvel's The Defenders",
                        "Marvel's Daredevil",
                        "The Office (U.S.)",
                        "Pulp Fiction",
                        "American Psycho"),
                Arrays.asList("Action & Adventure", "Dramas", "Comedies", "Thrillers"));
        recommendations.entrySet()
                       .stream()
                       .sorted(Comparator.comparingDouble((Map.Entry<Production, Double> entry) -> entry.getValue())
                                         .reversed())
                       .limit(10)
                       .forEachOrdered(entry -> System.out.println(
                               "Recommended movie: " + entry.getKey().getTitle() + " with score: " + entry.getValue()));
    }

}
