package org.gmaystorski.recommend.ingest.neo4j.action;

import java.util.Map;

import org.gmaystorski.recommend.ingest.intent.ActionIntent;

public abstract class AbstractActionFactory<T extends Action> {

	public T getAction(ActionIntent intent) {
		switch (intent.getEntityType()) {
		case VERTEX:
			return createUpsertVertexAction(intent.getProperties());
		case EDGE:
			return createUpsertEdgeAction(intent.getProperties());
		default:
			throw new RuntimeException();
		}
	}

	protected abstract T createUpsertVertexAction(Map<String, Object> requiredData);

	protected abstract T createUpsertEdgeAction(Map<String, Object> requiredData);

}
