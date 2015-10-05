package org.nanogoogle.service.impl;

import org.apache.lucene.document.Document;
import org.nanogoogle.model.SearchDocument;
import org.nanogoogle.search.Searcher;
import org.nanogoogle.service.SearchService;

import java.io.IOException;
import java.util.List;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

public class LuceneSearchService implements SearchService {

    private Searcher<Document> searcher;

    public LuceneSearchService() {
    }

    public LuceneSearchService(Searcher<Document> searcher) {
        this.searcher = searcher;
    }

    @Override
    public List<SearchDocument> search(String query, int offset, int count) throws IOException {
        return StreamSupport.stream(searcher.search(query, offset, count)
            .map(this::mapFromLuceneToWebDocument)
            .toBlocking()
            .toIterable()
            .spliterator(), true)
            .collect(toList());
    }

    private SearchDocument mapFromLuceneToWebDocument(Document document) {
        return new SearchDocument(document.get("Title"), document.get("URI"), document.get("Content"), null);
    }
}
