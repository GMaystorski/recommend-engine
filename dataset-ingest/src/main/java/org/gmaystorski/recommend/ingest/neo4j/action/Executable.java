package org.gmaystorski.recommend.ingest.neo4j.action;

import java.util.function.Supplier;

public interface Executable<T> {

	void execute(Supplier<T> supplier);

}
