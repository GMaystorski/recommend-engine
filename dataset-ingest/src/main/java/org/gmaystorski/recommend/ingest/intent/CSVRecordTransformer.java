package org.gmaystorski.recommend.ingest.intent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVRecord;
import org.gmaystorski.recommend.ingest.intent.ActionIntent.EntityType;
import org.springframework.stereotype.Component;

@Component
public class CSVRecordTransformer implements RecordTransformer<CSVRecord> {

	@Override
	public List<ActionIntent> getIntents(CSVRecord record) {
		List<ActionIntent> intents = new ArrayList<>();
		String productionId = record.get("show_id").trim();
		intents.add(buildProductionIntent(record));
		List<ActionIntent> inCategories = Stream.of(record.get("listed_in").split(","))
				.map(category -> buildProductionInCategory(productionId, category.trim())).collect(Collectors.toList());
		intents.addAll(inCategories);
		intents.add(buildProductionReleasedIn(productionId, record.get("release_year").trim()));
		String directors = record.get("director").trim();
		if (!directors.isEmpty()) {
			for (String director : directors.split(",")) {
				intents.addAll(buildProductionDirectorIntents(director.trim(), productionId));
			}
		}
		List<ActionIntent> actedIn = Stream.of(record.get("cast").split(","))
				.map(actor -> buildProductionActorIntents(actor.trim(), productionId)).flatMap(list -> list.stream())
				.collect(Collectors.toList());
		intents.addAll(actedIn);
		return intents;
	}

	private ActionIntent buildProductionIntent(CSVRecord record) {
		Map<String, Object> productionProperties = new HashMap<>();
		productionProperties.put("label", "Production");
		productionProperties.put("id", Map.of("id", record.get("show_id").trim()));
		Map<String, Object> vertexProps = new HashMap<>();
		vertexProps.put("id", record.get("show_id").trim());
		vertexProps.put("type", record.get("type").trim());
		vertexProps.put("title", record.get("title").trim());
		vertexProps.put("country", record.get("country").trim());
		vertexProps.put("rating", record.get("rating").trim());
		vertexProps.put("duration", record.get("duration").trim());
		vertexProps.put("description", record.get("description").trim());
		productionProperties.put("data", vertexProps);
		return new ActionIntent(EntityType.VERTEX, productionProperties);
	}

	private ActionIntent buildProductionInCategory(String productionId, String category) {
		Map<String, Object> inCategoryProps = new HashMap<>();
		inCategoryProps.put("label", "inCategory");
		inCategoryProps.put("source", Map.of("label", "Production", "id", productionId));
		inCategoryProps.put("destination", Map.of("label", "Category", "name", category));
		return new ActionIntent(EntityType.EDGE, inCategoryProps);
	}

	private ActionIntent buildProductionReleasedIn(String productionId, String year) {
		Map<String, Object> releasedInProps = new HashMap<>();
		releasedInProps.put("label", "releasedIn");
		releasedInProps.put("source", Map.of("label", "Production", "id", productionId));
		releasedInProps.put("destination", Map.of("label", "Year", "year", year));
		return new ActionIntent(EntityType.EDGE, releasedInProps);
	}

	private List<ActionIntent> buildProductionDirectorIntents(String director, String productionId) {
		Map<String, Object> personProps = new HashMap<>();
		personProps.put("label", "Person");
		personProps.put("id", Map.of("name", director));
		personProps.put("data", Map.of("name", director));
		ActionIntent personIntent = new ActionIntent(EntityType.VERTEX, personProps);
		Map<String, Object> directedProps = new HashMap<>();
		directedProps.put("label", "directed");
		directedProps.put("source", Map.of("label", "Person", "name", director));
		directedProps.put("destination", Map.of("label", "Production", "id", productionId));
		ActionIntent directedIntent = new ActionIntent(EntityType.EDGE, directedProps);
		return Arrays.asList(personIntent, directedIntent);
	}

	private List<ActionIntent> buildProductionActorIntents(String actor, String productionId) {
		Map<String, Object> personProps = new HashMap<>();
		personProps.put("label", "Person");
		personProps.put("id", Map.of("name", actor));
		personProps.put("data", Map.of("name", actor));
		ActionIntent personIntent = new ActionIntent(EntityType.VERTEX, personProps);
		Map<String, Object> actedInProps = new HashMap<>();
		actedInProps.put("label", "actedIn");
		actedInProps.put("source", Map.of("label", "Person", "name", actor));
		actedInProps.put("destination", Map.of("label", "Production", "id", productionId));
		ActionIntent actedInIntent = new ActionIntent(EntityType.EDGE, actedInProps);
		return Arrays.asList(personIntent, actedInIntent);
	}

}
