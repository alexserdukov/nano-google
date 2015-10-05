package org.nanogoogle.service;

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

import java.io.IOException;
import java.net.URI;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class LuceneIndexServiceTests{

    private Directory directory = new RAMDirectory();

    IndexService indexService;
    SearchService searchService;

    @Before
    public void setup(){
        indexService = new LuceneIndexService(new WebCrawlerImpl(), new LuceneIndexer(directory));
        searchService = new LuceneSearchService(new LuceneSearcher(directory));
    }

    @Test
    public void testIndex() throws IOException {
        indexService.index(URI.create("http://google.com"), 2);
        List<SearchDocument> documents = searchService.search("google", 0, 10);

        assertTrue(documents != null);
    }


}
