package org.gmaystorski.recommend.ingest.neo4j.action;

import java.util.Map;

public abstract class Action<T> implements Executable<T> {

	protected Map<String, Object> intentData;

	public Action(Map<String, Object> intentData) {
		this.intentData = intentData;
	}

	public Map<String, Object> getIntentData() {
		return intentData;
	}

}
