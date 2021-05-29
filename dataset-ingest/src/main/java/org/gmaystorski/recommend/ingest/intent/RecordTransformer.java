package org.gmaystorski.recommend.ingest.intent;

import java.util.List;

public interface RecordTransformer<T> {

	List<ActionIntent> getIntents(T record);

}
