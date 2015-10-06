package org.nanogoogle.index;

import com.google.common.collect.Lists;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nanogoogle.search.LuceneSearcher;
import org.nanogoogle.search.Searcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.observers.TestSubscriber;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.fail;

public class LuceneIndexerTest{

    Logger logger = LoggerFactory.getLogger(LuceneIndexerTest.class);

    private Directory directory = new RAMDirectory();
    private Analyzer analyzer = new StandardAnalyzer();

    Indexer<Document> indexer;
    Searcher<Document> searcher;

    @Before
    public void setupIndex() throws IOException {
        indexer = new LuceneIndexer(directory, analyzer);
        searcher = new LuceneSearcher(directory, analyzer);
        logger.debug("Indexed " + indexer.index(generateDocuments()) + " records");
    }

    @Test
    public void testIndexFound() {
        final Observable<Document> documentsObservable = observeOnDocuments("Example", 0, 1);
        List<Document> documents = getEmittedItemsAndCheckNoErrors(documentsObservable);
        Assert.assertTrue(documents.get(0).get("Title").equals("Example Field"));
    }

    @Test
    public void testIndexNotFound() {
        final Observable<Document> documentsObservable = observeOnDocuments("Exampl", 0, 1);
        List<Document> documents = getEmittedItemsAndCheckNoErrors(documentsObservable);
        Assert.assertTrue(documents.size() == 0);
    }

    private List<Document> getEmittedItemsAndCheckNoErrors(Observable<Document> documentObservable) {
        TestSubscriber<Document> testSubscriber = new TestSubscriber<>();
        documentObservable.subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        return testSubscriber.getOnNextEvents();
    }

    private Observable<Document> observeOnDocuments(String keyword, int offset, int size) {
        try {
            return searcher.search(keyword, offset, size);
        } catch (IOException e) {
            e.printStackTrace();
            fail("Exception occured when search has being processed");
        }
        return Observable.empty();
    }

    private Observable<Document> generateDocuments() {
        Document document = new Document();
        document.add(new TextField("Title", "Example Field", Field.Store.YES));
        document.add(new TextField("Content", "Example test", Field.Store.YES));
        document.add(new TextField("URI", "http://google.comm", Field.Store.YES));
        return Observable.from(Lists.<Document>newArrayList(document));
    }
}
