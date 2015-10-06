package org.nanogoogle.service;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Before;
import org.junit.Test;
import org.nanogoogle.crawler.WebCrawlerImpl;
import org.nanogoogle.index.LuceneIndexer;
import org.nanogoogle.model.SearchDocument;
import org.nanogoogle.search.LuceneSearcher;
import org.nanogoogle.service.impl.LuceneIndexService;
import org.nanogoogle.service.impl.LuceneSearchService;
import rx.Observable;

import java.io.IOException;
import java.net.URI;
import java.util.stream.StreamSupport;

import static org.junit.Assert.assertTrue;

public class LuceneIndexServiceTests {

    private Directory directory = new RAMDirectory();
    private Analyzer analyzer = new StandardAnalyzer();

    IndexService indexService;
    SearchService searchService;

    @Before
    public void setup() throws IOException {
        indexService = new LuceneIndexService(new WebCrawlerImpl(), new LuceneIndexer(directory, analyzer));
        searchService = new LuceneSearchService(new LuceneSearcher(directory, analyzer));
    }

    @Test
    public void testIndex() throws IOException {
        Integer indexed = indexService.index(URI.create("http://google.com"), 3).count().toBlocking().first();
        assertTrue(indexed > 0);
        Observable<SearchDocument> documents = searchService.search("google", 0, 10);
        assertTrue(StreamSupport.stream(documents.toBlocking().toIterable().spliterator(), true).count() == 10);
    }


}
