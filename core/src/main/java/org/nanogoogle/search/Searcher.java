package org.nanogoogle.search;

import rx.Observable;

import java.io.IOException;

public interface Searcher<T> {

    Observable<T> search(String searchQuery, int offset, int count) throws IOException;

}
