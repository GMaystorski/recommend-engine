package org.gmaystorski.recommend.ingest.neo4j.action;

import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.gmaystorski.recommend.commons.cypher.query.CypherQueryBuilder;
import org.gmaystorski.recommend.commons.cypher.query.CypherQueryBuilder.EdgeDirection;
import org.neo4j.driver.Session;

public class Neo4jUpsertEdgeAction extends Neo4jAction {

	private static final String EDGE_VAR = "e";
	private static final String DEST_VAR = "d";
	private static final String SOURCE_VAR = "s";

	public Neo4jUpsertEdgeAction(Map<String, Object> intentData) {
		super(intentData);
	}

	@Override
	public void execute(Supplier<Session> supplier) {
		final Session session = supplier.get();

		EdgeData edgeData = getEdgeData();

		final String upsert = new CypherQueryBuilder().merge()
				.withNode(SOURCE_VAR, edgeData.getSourceLabel(), edgeData.getSourceIds()).newLine().merge()
				.withNode(DEST_VAR, edgeData.getDestinationLabel(), edgeData.getDestinationIds()).newLine().merge()
				.withNode(SOURCE_VAR).withEdge(EDGE_VAR, edgeData.getEdgeLabel(), EdgeDirection.SOURCE_TO_DESTINATION)
				.withNode(DEST_VAR).build();

		session.run(upsert);
	}

	private EdgeData getEdgeData() {
		Map<String, Object> source = (Map<String, Object>) getIntentData().get("source");
		Map<String, Object> destination = (Map<String, Object>) getIntentData().get("destination");
		String edgeLabel = getIntentData().get("label").toString();
		String sourceLabel = source.get("label").toString();
		Map<String, Object> sourceIds = getIds(source);
		Map<String, Object> destinationIds = getIds(destination);
		String destinationLabel = destination.get("label").toString();
		return new EdgeData(sourceIds, destinationIds, sourceLabel, destinationLabel, edgeLabel);
	}

	private Map<String, Object> getIds(Map<String, Object> props) {
		return props.keySet().stream().filter(key -> !key.equals("label"))
				.collect(Collectors.toMap(key -> key, key -> props.get(key)));
	}

	private static class EdgeData {
		private Map<String, Object> sourceIds;
		private Map<String, Object> destinationIds;
		private String sourceLabel;
		private String destinationLabel;

		public EdgeData(Map<String, Object> sourceIds, Map<String, Object> destinationIds, String sourceLabel,
				String destinationLabel, String edgeLabel) {
			super();
			this.sourceIds = sourceIds;
			this.destinationIds = destinationIds;
			this.sourceLabel = sourceLabel;
			this.destinationLabel = destinationLabel;
			this.edgeLabel = edgeLabel;
		}

		private String edgeLabel;

		public Map<String, Object> getSourceIds() {
			return sourceIds;
		}

		public Map<String, Object> getDestinationIds() {
			return destinationIds;
		}

		public String getSourceLabel() {
			return sourceLabel;
		}

		public String getDestinationLabel() {
			return destinationLabel;
		}

		public String getEdgeLabel() {
			return edgeLabel;
		}

	}
}
