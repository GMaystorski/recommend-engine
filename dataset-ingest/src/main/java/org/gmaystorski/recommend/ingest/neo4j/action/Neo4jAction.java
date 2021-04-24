package org.gmaystorski.recommend.ingest.neo4j.action;

import java.util.Map;

import org.neo4j.driver.Session;

public abstract class Neo4jAction extends Action<Session> {

	public Neo4jAction(Map<String, Object> intentData) {
		super(intentData);
	}
}
