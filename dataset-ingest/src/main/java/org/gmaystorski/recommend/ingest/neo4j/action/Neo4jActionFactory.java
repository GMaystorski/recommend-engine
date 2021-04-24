package org.gmaystorski.recommend.ingest.neo4j.action;

import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class Neo4jActionFactory extends AbstractActionFactory<Neo4jAction> {

	@Override
	protected Neo4jAction createUpsertVertexAction(Map<String, Object> requiredData) {
		return new Neo4jUpsertVertexAction(requiredData);
	}

	@Override
	protected Neo4jAction createUpsertEdgeAction(Map<String, Object> requiredData) {
		return new Neo4jUpsertEdgeAction(requiredData);
	}

}
