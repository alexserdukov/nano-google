package org.nanogoogle.service.impl;

import org.apache.lucene.document.Document;
import org.nanogoogle.model.SearchDocument;
import org.nanogoogle.search.Searcher;
import org.nanogoogle.service.SearchService;
import rx.Observable;

import java.io.IOException;

public class LuceneSearchService implements SearchService {

    private Searcher<Document> searcher;

    public LuceneSearchService() {
    }

    public LuceneSearchService(Searcher<Document> searcher) {
        this.searcher = searcher;
    }

    @Override
    public Observable<SearchDocument> search(String query, int offset, int count) throws IOException {
        return searcher.search(query, offset, count).map(this::mapFromLuceneToWebDocument);
    }

    private SearchDocument mapFromLuceneToWebDocument(Document document) {
        return new SearchDocument(document.get("Title"), document.get("URI"), document.get("Content"), null);
    }
}
