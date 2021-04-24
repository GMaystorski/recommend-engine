package org.gmaystorski.recommend.service.bean;

import java.time.Duration;

import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.gmaystorski.recommend.commons.pool.SessionPool;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class Neo4jObjectPoolBeanConfigurator {

	@Bean
	public Driver driver(@Value("${neo4j.url}") String url, @Value("${neo4j.username}") String username,
			@Value("${neo4j.password}") String password) {
		return GraphDatabase.driver("bolt://" + url, AuthTokens.basic(username, password));
	}

	@Bean("neo4jSession")
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public Session session(Driver driver) {
		return driver.session();
	}

	@Bean
	public SessionPool sessionPool(GenericObjectPoolConfig<Object> poolConfig, AbandonedConfig abandonedConfig) {
		SessionPool sessionPool = new SessionPool(poolConfig, abandonedConfig);
		sessionPool.createConnectionPool();
		return sessionPool;
	}

	@Bean
	public GenericObjectPoolConfig<Object> poolConfig() {
		GenericObjectPoolConfig<Object> pool = new GenericObjectPoolConfig<Object>();
		pool.setMaxTotal(10);
		pool.setMaxWaitMillis(Duration.ofMinutes(5).toMillis());
		return pool;
	}

	@Bean
	public AbandonedConfig abandonedConfig() {
		return new AbandonedConfig();
	}
}
