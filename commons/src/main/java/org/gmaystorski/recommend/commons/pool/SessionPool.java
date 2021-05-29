package org.gmaystorski.recommend.commons.pool;

import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.neo4j.driver.Session;

public class SessionPool extends AbstractConnectionPool<Session> {

	public SessionPool(GenericObjectPoolConfig<Object> genericObjectPoolConfig, AbandonedConfig abandonedConfig) {
		super(genericObjectPoolConfig, abandonedConfig);

	}

	@Override
	public void setConnectionBeanName() {
		this.setTargetBeanName("neo4jSession");
	}

	@Override
	protected boolean validateTarget(Session target) {
		return target.isOpen();
	}

	@Override
	protected void passivateTarget(Session target) throws ConnectionPassivationException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void activateTarget(Session target) {
		// TODO Auto-generated method stub

	}
}
