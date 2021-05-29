package org.gmaystorski.recommend.ingest.intent;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ActionIntent {

	private EntityType entityType;
	private Map<String, Object> properties;

	public ActionIntent(EntityType entityType, Map<String, Object> properties) {
		this.entityType = entityType;
		this.properties = properties;
	}

	public EntityType getEntityType() {
		return entityType;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return super.toString();
		}
	}

	public static enum EntityType {
		EDGE, VERTEX;
	}
}
