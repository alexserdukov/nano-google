package org.nanogoogle.index;


import rx.Observable;

public interface Indexer<T> {

    int index (Observable<T> documents);
}
