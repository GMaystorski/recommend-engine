package org.gmaystorski.recommend.commons.pool;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.aop.target.CommonsPool2TargetSource;

public abstract class AbstractConnectionPool<T> extends CommonsPool2TargetSource {

	private GenericObjectPoolConfig<Object> genericObjectPoolConfig;
	private AbandonedConfig abandonedConfig;

	public AbstractConnectionPool(GenericObjectPoolConfig<Object> genericObjectPoolConfig,
			AbandonedConfig abandonedConfig) {
		super();
		this.genericObjectPoolConfig = genericObjectPoolConfig;
		this.abandonedConfig = abandonedConfig;
	}

	@Override
	protected ObjectPool<Object> createObjectPool() {
		return new GenericObjectPool<>(this, genericObjectPoolConfig, abandonedConfig);
	}

	public abstract void setConnectionBeanName();

	public void createConnectionPool() {
		setConnectionBeanName();
		this.createPool();
	}

	public T getTarget() throws ConnectionAcquisitionException {
		try {
			return (T) super.getTarget();
		} catch (Exception e) {
			throw new ConnectionAcquisitionException(e);
		}
	}

	@Override
	public void releaseTarget(Object target) throws ConnectionReleaseException {
		try {
			super.releaseTarget(target);
		} catch (Exception e) {
			throw new ConnectionReleaseException(e);
		}
	}

	@Override
	public void activateObject(PooledObject<Object> p) {
		activateTarget(getObject(p));
	}

	@Override
	public void passivateObject(PooledObject<Object> p) throws ConnectionPassivationException {
		passivateTarget(getObject(p));
	}

	@Override
	public boolean validateObject(PooledObject<Object> p) {
		return validateTarget(getObject(p));
	}

	protected abstract boolean validateTarget(T target);

	protected abstract void passivateTarget(T target) throws ConnectionPassivationException;

	protected abstract void activateTarget(T target);

	protected T getObject(PooledObject<Object> p) {
		return (T) p.getObject();
	}

}
