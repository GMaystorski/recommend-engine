package org.gmaystorski.recommend.ingest.neo4j.action;

import java.util.Map;
import java.util.function.Supplier;

import org.gmaystorski.recommend.commons.cypher.query.CypherQueryBuilder;
import org.neo4j.driver.Session;

public class Neo4jUpsertVertexAction extends Neo4jAction {

	private static final String NODE_VAR = "v";

	public Neo4jUpsertVertexAction(Map<String, Object> intentData) {
		super(intentData);
	}

	@Override
	public void execute(Supplier<Session> supplier) {
		final Session session = supplier.get();
		Map<String, Object> properties = (Map<String, Object>) getIntentData().get("data");
		Map<String, Object> ids = (Map<String, Object>) getIntentData().get("id");

		String vertexLabel = (String) getIntentData().get("label");

		final String upsert = new CypherQueryBuilder().merge().withNode(NODE_VAR, vertexLabel, ids)
				.set(NODE_VAR, properties).build();
		session.run(upsert);
	}

}
