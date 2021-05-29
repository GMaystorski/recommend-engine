package org.gmaystorski.recommend.ingest.neo4j.action;

import org.gmaystorski.recommend.commons.pool.SessionPool;
import org.neo4j.driver.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;

@Component
public class Neo4jExecutor implements Executor<Session> {

	@Autowired
	private SessionPool sessionPool;

	@Autowired
	@Qualifier("neo4jRetryPolicy")
	private RetryPolicy<Object> actionRetryPolicy;

	@Override
	public void execute(Executable<Session> executable) {
		Failsafe.with(actionRetryPolicy).run(() -> {
			Session session = sessionPool.getTarget();
			try {
				executable.execute(() -> session);
			} finally {
				sessionPool.releaseTarget(session);
			}
		});
	}

}
