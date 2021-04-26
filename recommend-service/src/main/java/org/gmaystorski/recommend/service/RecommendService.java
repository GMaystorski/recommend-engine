package org.gmaystorski.recommend.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.gmaystorski.recommend.commons.cypher.query.CypherQueryBuilder;
import org.gmaystorski.recommend.commons.cypher.query.CypherQueryBuilder.EdgeDirection;
import org.gmaystorski.recommend.commons.cypher.query.CypherQueryBuilder.OrderDirection;
import org.gmaystorski.recommend.commons.pool.ConnectionAcquisitionException;
import org.gmaystorski.recommend.commons.pool.ConnectionReleaseException;
import org.gmaystorski.recommend.commons.pool.SessionPool;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RecommendService {

    @Autowired
    private SessionPool sessionPool;

    public Map<Production, Double> processInput(List<String> actorNames, List<String> movieTitles,
            List<String> categories) {
        Map<Production, Double> recommendations = new HashMap<>();
        processActorNames(actorNames, movieTitles, recommendations);
        processMovieTitles(movieTitles, recommendations);
        List<String> normalizedCategories = categories.stream()
                                                      .map(category -> Arrays.asList(category, "TV " + category))
                                                      .flatMap(list -> list.stream())
                                                      .collect(Collectors.toList());
        System.out.println(normalizedCategories);
        processCategories(normalizedCategories, recommendations);
        return recommendations;
    }

    private void processCategories(List<String> categories, Map<Production, Double> recommendations) {
        Map<Production, Integer> multipliers = recommendations.keySet()
                                                              .stream()
                                                              .collect(Collectors.toMap(production -> production,
                                                                      production -> CollectionUtils.intersection(
                                                                              production.getCategories(),
                                                                              categories).size()));
        multipliers.entrySet()
                   .stream()
                   .forEach(entry -> recommendations.compute(entry.getKey(),
                           (k, v) -> v * (1 + (entry.getValue() * 0.2))));
    }

    private void processActorNames(List<String> actorNames, List<String> movieTitles,
            Map<Production, Double> recommendations) {
        String titles = getCommaSeparatedStrings(movieTitles);
        String actors = getCommaSeparatedStrings(actorNames);
        String actorMoviesQuery = buildActorMoviesQuery(actors, titles);
        try {
            Session session = sessionPool.getTarget();
            executeQuery(actorMoviesQuery, record -> processGenericMovieResult(record, recommendations), session);
            sessionPool.releaseTarget(session);
        } catch (ConnectionAcquisitionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ConnectionReleaseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void processMovieTitles(List<String> movieTitles, Map<Production, Double> recommendations) {
        String titles = getCommaSeparatedStrings(movieTitles);
        String similarityQuery = buildSimilarityQuery(titles);
        String directorMoviesQuery = buildDirectorMoviesQuery(titles);
        String decadeCategoryMoviesQuery = buildDecadeCategoryMoviesQuery(titles);
        try {
            Session session = sessionPool.getTarget();
            executeQuery(similarityQuery, record -> processSimilarityResult(record, recommendations), session);
            executeQuery(directorMoviesQuery, record -> processGenericMovieResult(record, recommendations), session);
            executeQuery(decadeCategoryMoviesQuery,
                    record -> processGenericMovieResult(record, recommendations),
                    session);
            sessionPool.releaseTarget(session);
        } catch (ConnectionAcquisitionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ConnectionReleaseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void executeQuery(String query, Consumer<Record> resultProcessor, Session session) {
        Result result = session.run(query);
        result.forEachRemaining(resultProcessor);
    }

    private String buildDecadeCategoryMoviesQuery(String titles) {
        Map<String, String> firstVars = new HashMap<>();
        firstVars.put("v", null);
        firstVars.put("v2", null);
        firstVars.put("v3", null);
        firstVars.put("v4", null);
        firstVars.put("collect(distinct v5.name)", "c1");
        Map<String, String> secondVars = new HashMap<>();
        secondVars.put("v", null);
        secondVars.put("v3", null);
        secondVars.put("((toInteger(v2.year)/10)*10)", "decadeStart");
        secondVars.put("collect(distinct v4)", "c");
        return new CypherQueryBuilder().match()
                                       .withNode("v5", "Category", Collections.emptyMap())
                                       .withEdge(EdgeDirection.DESTINATION_TO_SOURCE)
                                       .withNode("v1", "Production", Collections.emptyMap())
                                       .withEdge(EdgeDirection.SOURCE_TO_DESTINATION)
                                       .withNode("v2", "Year", Collections.emptyMap())
                                       .newLine()
                                       .match()
                                       .withNode("v3", "Year", Collections.emptyMap())
                                       .withEdge(EdgeDirection.DESTINATION_TO_SOURCE)
                                       .withNode("v", "Production", Collections.emptyMap())
                                       .withEdge(EdgeDirection.SOURCE_TO_DESTINATION)
                                       .withNode("v4", "Category", Collections.emptyMap())
                                       .newLine()
                                       .where("v1.title IN " + titles, "v.title <> v1.title")
                                       .newLine()
                                       .with(firstVars)
                                       .newLine()
                                       .where("v4.name in c1")
                                       .newLine()
                                       .with(secondVars)
                                       .newLine()
                                       .where("decadeStart <= toInteger(v3.year) <= decadeStart + 10")
                                       .newLine()
                                       .returnValues("v", "c")
                                       .build();

    }

    private String buildActorMoviesQuery(String actors, String titles) {
        Map<String, String> vars = new HashMap<>();
        vars.put("v", null);
        vars.put("collect(distinct v2.name)", "c");
        return new CypherQueryBuilder().match()
                                       .withNode("v2", "Category", Collections.emptyMap())
                                       .withEdge(EdgeDirection.DESTINATION_TO_SOURCE)
                                       .withNode("v", "Production", Collections.emptyMap())
                                       .withEdge("", "actedIn", EdgeDirection.DESTINATION_TO_SOURCE)
                                       .withNode("v1", "Person", Collections.emptyMap())
                                       .where("v1.name IN " + actors, "NOT v.title IN " + titles)
                                       .with(vars)
                                       .returnValues("v", "c")
                                       .build();

    }

    private String buildDirectorMoviesQuery(String titles) {
        Map<String, String> vars = new HashMap<>();
        vars.put("v", null);
        vars.put("v2", null);
        vars.put("collect(distinct v3.name)", "c");
        return new CypherQueryBuilder().match()
                                       .withNode("v3", "Category", Collections.emptyMap())
                                       .withEdge(EdgeDirection.DESTINATION_TO_SOURCE)
                                       .withNode("v", "Production", Collections.emptyMap())
                                       .withEdge("", "directed", EdgeDirection.DESTINATION_TO_SOURCE)
                                       .withNode("v1", "Person", Collections.emptyMap())
                                       .withEdge("", "directed", EdgeDirection.SOURCE_TO_DESTINATION)
                                       .withNode("v2", "Production", Collections.emptyMap())
                                       .newLine()
                                       .with(vars)
                                       .where("v2.title IN " + titles, "v.id <> v2.id")
                                       .returnValues("v", "c")
                                       .build();
    }

    private String buildSimilarityQuery(String titles) {
        Map<String, String> vars = new HashMap<>();
        vars.put("v", null);
        vars.put("v1", null);
        vars.put("collect(distinct v2.name)", "c");
        vars.put("apoc.text.levenshteinSimilarity(v.description, v1.description)", "similarity");
        return new CypherQueryBuilder().match()
                                       .withNode("v1", "Production", Collections.emptyMap())
                                       .newLine()
                                       .match()
                                       .withNode("v", "Production", Collections.emptyMap())
                                       .withEdge(EdgeDirection.SOURCE_TO_DESTINATION)
                                       .withNode("v2", "Category", Collections.emptyMap())
                                       .newLine()
                                       .where("v1.title IN " + titles, "v.title <> v1.title")
                                       .newLine()
                                       .with(vars)
                                       .newLine()
                                       .where("similarity >= 0.8", "similarity < 1.0")
                                       .newLine()
                                       .returnValues("v", "c", "similarity")
                                       .orderBy("similarity", OrderDirection.DESC)
                                       .build();
    }

    private void processSimilarityResult(Record record, Map<Production, Double> recommendations) {
        Production similar = mapProductionFromRecord(record);
        double similarity = record.get("similarity").asDouble();
        updateRecommendation(similar, recommendations, similarity * 50.0);
    }

    private void processGenericMovieResult(Record record, Map<Production, Double> recommendations) {
        Production movie = mapProductionFromRecord(record);
        updateRecommendation(movie, recommendations, 1.0);
    }

    private Production mapProductionFromRecord(Record record) {
        Map<String, Object> productionProps = record.get("v").asMap();
        List<String> categories = record.get("c").asList().stream().map(Objects::toString).collect(Collectors.toList());

        return new Production(productionProps.get("id").toString(),
                productionProps.get("description").toString(),
                productionProps.get("title").toString(),
                categories);
    }

    private String getCommaSeparatedStrings(List<String> list) {
        String joined = list.stream().map(string -> '"' + string + '"').collect(Collectors.joining(","));
        return "[" + joined + "]";
    }

    private void updateRecommendation(Production production, Map<Production, Double> recommendations, Double score) {
        if (!recommendations.containsKey(production)) {
            recommendations.put(production, score);
        } else {
            recommendations.compute(production, (k, v) -> v + score);
        }
    }

}
