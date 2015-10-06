package org.nanogoogle.service.impl;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.nanogoogle.crawler.WebCrawler;
import org.nanogoogle.index.Indexer;
import org.nanogoogle.model.SearchDocument;
import org.nanogoogle.service.IndexService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.io.IOException;
import java.net.URI;

public class LuceneIndexService implements IndexService {

    Logger logger = LoggerFactory.getLogger(LuceneIndexService.class);
    private WebCrawler webCrawler;
    private Indexer<Document> indexer;

    public LuceneIndexService() {
    }

    public LuceneIndexService(WebCrawler webCrawler, Indexer<Document> indexer) {
        this.webCrawler = webCrawler;
        this.indexer = indexer;
    }

    @Override
    public int index(URI uri, int level) throws IOException {
        Observable<SearchDocument> observableDocuments = webCrawler.recursiveParse(uri, level);
        logger.info("Before indexing");
        return indexer.index(observableDocuments.distinct().map(this::fromWebToLuceneDocument));
    }

    private Document fromWebToLuceneDocument(SearchDocument webDocument) {
        Document document = new Document();
        document.add(new TextField("Title", webDocument.getTitle(), Field.Store.YES));
        document.add(new TextField("URI", webDocument.getUri(), Field.Store.YES));
        document.add(new TextField("Content", webDocument.getContent(), Field.Store.YES));
        return document;
    }
}
