package org.gmaystorski.recommend.ingest.neo4j.action;

import java.util.Map;

import org.gmaystorski.recommend.ingest.intent.ActionIntent;

public abstract class AbstractActionFactory<T> {

	public Action getAction(ActionIntent intent) {
		switch (intent.getEntityType()) {
		case VERTEX:
			return createUpsertVertexAction(intent.getProperties());
		case EDGE:
			return createUpsertEdgeAction(intent.getProperties());
		default:
			throw new RuntimeException();
		}
	}

	protected abstract Action createUpsertVertexAction(Map<String, Object> requiredData);

	protected abstract Action createUpsertEdgeAction(Map<String, Object> requiredData);

}
