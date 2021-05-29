package org.gmaystorski.recommend.ingest.neo4j.bean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.jodah.failsafe.RetryPolicy;

@Configuration
public class Neo4jRetryPolicyBeanConfigurator {

	@Bean("neo4jRetryPolicy")
	public RetryPolicy<Object> neoExecutionRetryPolicy() {
		return new RetryPolicy<>();// TODO
	}

}
