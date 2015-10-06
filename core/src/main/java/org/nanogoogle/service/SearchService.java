package org.nanogoogle.service;

import org.nanogoogle.model.SearchDocument;
import rx.Observable;

import java.io.IOException;

public interface SearchService {

    Observable<SearchDocument> search (String query, int offset, int count) throws IOException;
}
