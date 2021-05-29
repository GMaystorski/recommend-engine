package org.gmaystorski.recommend.ingest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.gmaystorski.recommend.ingest.intent.ActionIntent;
import org.gmaystorski.recommend.ingest.intent.RecordTransformer;
import org.gmaystorski.recommend.ingest.neo4j.action.AbstractActionFactory;
import org.gmaystorski.recommend.ingest.neo4j.action.Action;
import org.gmaystorski.recommend.ingest.neo4j.action.Executor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class IngestionRunner implements ApplicationRunner {

	private static final String[] HEADERS = { "show_id", "type", "title", "director", "cast", "country", "date_added",
			"release_year", "rating", "duration", "listed_in", "description" };
	@Autowired
	private RecordTransformer<CSVRecord> transformer;
	@Autowired
	private AbstractActionFactory actionFactory;
	private ExecutorService executorService = Executors.newFixedThreadPool(30);
	@Autowired
	private Executor executor;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		InputStream inputStream = this.getClass().getResourceAsStream("/netflix_titles.csv");
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		CSVParser parser = CSVFormat.DEFAULT.withHeader(HEADERS).withFirstRecordAsHeader().parse(reader);
		Stream.of(parser).flatMap(this::parserToRecord).map(transformer::getIntents).map(this::transformToActions)
				.peek(this::executeActions).peek(list -> System.out.println("Entry Ingested")).count();
		executorService.shutdown();
		executorService.awaitTermination(5, TimeUnit.MINUTES);

	}

	private List<Action> transformToActions(List<ActionIntent> intents) {
		return intents.stream().map(actionFactory::getAction).collect(Collectors.toList());
	}

	private void executeActions(List<Action> actions) {
		for (Action action : actions) {
			executorService.execute(() -> executor.execute(action));
		}
	}

	private Stream<CSVRecord> parserToRecord(CSVParser parser) {
		return StreamSupport.stream(parser.spliterator(), false);
	}

}
