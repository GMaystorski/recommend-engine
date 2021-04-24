package org.gmaystorski.recommend.ingest.neo4j.action;

public interface Executor<T> {

	void execute(Executable<T> executable);

}
