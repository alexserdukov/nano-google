package org.nanogoogle.crawler;

import org.junit.Test;
import org.nanogoogle.model.SearchDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.observers.TestSubscriber;

import java.io.IOException;
import java.net.URI;
import java.util.List;

public class WebCrawlerTests {

    Logger logger = LoggerFactory.getLogger(WebCrawlerTests.class);

    WebCrawler crawler = new WebCrawlerImpl();

    @Test
    public void testRecursiveParseByURI() throws IOException, InterruptedException {
        Observable<SearchDocument> observableDocuments = crawler.recursiveParse(URI.create("http://google.com"), 2);
        observableDocuments.subscribe(searchDocument -> logger.debug("|"));
/*
        List<SearchDocument> documents = getEmittedItemsAndCheckNoErrors(observableDocuments);
        Assert.assertTrue(documents.size() > 0);
*/
    }

    private List<SearchDocument> getEmittedItemsAndCheckNoErrors(Observable<SearchDocument> documentObservable) throws InterruptedException {
        TestSubscriber<SearchDocument> testSubscriber = new TestSubscriber<>();
        documentObservable.subscribe(testSubscriber);
        // TODO make proper blocking for subscriber
        Thread.sleep(2000);
        testSubscriber.assertNoErrors();
        return testSubscriber.getOnNextEvents();
    }

}
