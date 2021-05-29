package org.gmaystorski.recommend.commons.cypher.query;

import static org.gmaystorski.recommend.commons.cypher.query.QueryConstants.COLON;
import static org.gmaystorski.recommend.commons.cypher.query.QueryConstants.COMMA;
import static org.gmaystorski.recommend.commons.cypher.query.QueryConstants.CURLY_L_BRACKET;
import static org.gmaystorski.recommend.commons.cypher.query.QueryConstants.CURLY_R_BRACKET;
import static org.gmaystorski.recommend.commons.cypher.query.QueryConstants.EQUALS;
import static org.gmaystorski.recommend.commons.cypher.query.QueryConstants.L_BRACKET;
import static org.gmaystorski.recommend.commons.cypher.query.QueryConstants.MATCH;
import static org.gmaystorski.recommend.commons.cypher.query.QueryConstants.MERGE;
import static org.gmaystorski.recommend.commons.cypher.query.QueryConstants.NEWLINE;
import static org.gmaystorski.recommend.commons.cypher.query.QueryConstants.ORDER_BY;
import static org.gmaystorski.recommend.commons.cypher.query.QueryConstants.QUOTE;
import static org.gmaystorski.recommend.commons.cypher.query.QueryConstants.RETURN;
import static org.gmaystorski.recommend.commons.cypher.query.QueryConstants.R_BRACKET;
import static org.gmaystorski.recommend.commons.cypher.query.QueryConstants.SET;
import static org.gmaystorski.recommend.commons.cypher.query.QueryConstants.SQUARE_L_BRACKET;
import static org.gmaystorski.recommend.commons.cypher.query.QueryConstants.SQUARE_R_BRACKET;
import static org.gmaystorski.recommend.commons.cypher.query.QueryConstants.WHERE;
import static org.gmaystorski.recommend.commons.cypher.query.QueryConstants.WHITESPACE;
import static org.gmaystorski.recommend.commons.cypher.query.QueryConstants.WITH;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class CypherQueryBuilder {

	private StringBuilder sb;

	public CypherQueryBuilder() {
		sb = new StringBuilder();
	}

	public CypherQueryBuilder merge() {
		sb.append(MERGE).append(WHITESPACE);
		return this;
	}

	public CypherQueryBuilder match() {
		sb.append(MATCH).append(WHITESPACE);
		return this;
	}

	public CypherQueryBuilder withNode(String varName, String nodeLabel, Map<String, Object> nodeProperties) {
		sb.append(L_BRACKET).append(varName).append(COLON).append(nodeLabel);
		if (!nodeProperties.isEmpty()) {
			sb.append(WHITESPACE).append(CURLY_L_BRACKET).append(getCommaSeparatedProps(nodeProperties))
					.append(CURLY_R_BRACKET);
		}
		sb.append(R_BRACKET).append(WHITESPACE);
		return this;
	}

	public CypherQueryBuilder set(String key, Object value) {
		sb.append(SET).append(WHITESPACE).append(key).append(WHITESPACE).append(EQUALS).append(WHITESPACE);
		if (value instanceof Map) {
			sb.append(CURLY_L_BRACKET).append(getCommaSeparatedProps((Map<String, Object>) value))
					.append(CURLY_R_BRACKET);
		} else
			sb.append(value);
		return this;
	}

	public CypherQueryBuilder where(String... projections) {
		sb.append(WHERE).append(WHITESPACE);
		String joinedProjections = Arrays.asList(projections).stream().collect(Collectors.joining(" and "));
		sb.append(joinedProjections).append(WHITESPACE);
		return this;
	}

	public CypherQueryBuilder returnValues(String... values) {
		sb.append(RETURN).append(WHITESPACE);
		String returnValues = Arrays.asList(values).stream().collect(Collectors.joining(","));
		sb.append(returnValues).append(WHITESPACE);
		return this;
	}

	public CypherQueryBuilder orderBy(String field, OrderDirection direction) {
		sb.append(ORDER_BY).append(WHITESPACE).append(field).append(WHITESPACE).append(direction.toString())
				.append(WHITESPACE);
		return this;
	}

	public CypherQueryBuilder with(Map<String, String> variables) {
		sb.append(WITH).append(WHITESPACE);
		String variablesStr = variables.entrySet().stream().map(this::mapVariableToString)
				.collect(Collectors.joining(","));
		sb.append(variablesStr).append(WHITESPACE);
		return this;
	}

	public CypherQueryBuilder withNode(String varName) {
		sb.append(L_BRACKET).append(varName).append(R_BRACKET);
		return this;
	}

	public CypherQueryBuilder withEdge(String varName, String edgeLabel, EdgeDirection direction) {
		sb.append(direction.LEFT_PATTERN).append(SQUARE_L_BRACKET).append(varName).append(COLON).append(edgeLabel)
				.append(SQUARE_R_BRACKET).append(direction.RIGHT_PATTERN);
		return this;
	}

	public CypherQueryBuilder withEdge(EdgeDirection direction) {
		sb.append(direction.LEFT_PATTERN).append(SQUARE_L_BRACKET).append(SQUARE_R_BRACKET)
				.append(direction.RIGHT_PATTERN);
		return this;
	}

	public CypherQueryBuilder whitespace() {
		sb.append(WHITESPACE);
		return this;
	}

	public CypherQueryBuilder newLine() {
		sb.append(NEWLINE);
		return this;
	}

	public String build() {
		return sb.toString();
	}

	private String getCommaSeparatedProps(Map<String, Object> parameters) {
		return parameters.keySet().stream().map(parameterKey -> getParamValueString(parameterKey, parameters))
				.collect(Collectors.joining(COMMA));
	}

	private String getParamValueString(String key, Map<String, Object> sourceParams) {
		Object value = sourceParams.get(key);
		String prefix = key + COLON + WHITESPACE;
		if (value instanceof String) {
			return prefix + QUOTE + value + QUOTE;
		}
		return prefix + value;
	}

	private String mapVariableToString(Entry<String, String> entry) {
		if (entry.getValue() == null) {
			return entry.getKey();
		}
		return entry.getKey() + " as " + entry.getValue();
	}

	public enum EdgeDirection {

		SOURCE_TO_DESTINATION("-", "->"), DESTINATION_TO_SOURCE("<-", "-");

		public final String LEFT_PATTERN;
		public final String RIGHT_PATTERN;

		private EdgeDirection(String leftPattern, String rightPattern) {
			this.LEFT_PATTERN = leftPattern;
			this.RIGHT_PATTERN = rightPattern;
		}
	}

	public enum OrderDirection {
		ASC, DESC;

	}

}
