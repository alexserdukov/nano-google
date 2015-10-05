package org.nanogoogle.index;


import rx.Observable;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public interface Indexer<T> {

    Observable<Void> index (Observable<T> documents, CountDownLatch latch) throws IOException;
}
