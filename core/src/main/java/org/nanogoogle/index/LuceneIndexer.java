package org.nanogoogle.index;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import rx.Observable;
import rx.Observer;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class LuceneIndexer implements Indexer<Document> {

    Logger logger = LoggerFactory.getLogger(LuceneIndexer.class);
    private Directory directory;

    @Autowired
    public LuceneIndexer(Directory directory) {
        this.directory = directory;
    }

    public Observable<Void> index(Observable<Document> documents, CountDownLatch latch) throws IOException {
        IndexWriter indexWriter = null;
        try (Analyzer analyzer = new StandardAnalyzer()) {
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            indexWriter = new IndexWriter(directory, config);
            final IndexWriter writer = indexWriter;
            Observable<Void> indexObservable = Observable.create(subscriber -> {
                try {
                    writer.addDocuments(documents.toBlocking().toIterable());
                } catch (IOException e) {
                    // TODO log error
                }
                subscriber.onCompleted();
            });

            indexObservable.subscribe(new Observer<Void>() {
                @Override
                public void onCompleted() {
                    try {
                        logger.debug("Successfully indexed");
                        writer.commit();
                        writer.close();
                        latch.countDown();
                    } catch (IOException e) {
                        // TODO log error
                    }
                }

                @Override
                public void onError(Throwable e) {
                    try {
                        logger.error("Failure indexed");
                        writer.close();
                    } catch (IOException e1) {
                        // TODO log error
                    }
                }

                @Override
                public void onNext(Void aVoid) {
                    // No emitted items
                }
            });
            return indexObservable;
        } finally {
            if (indexWriter != null)
                indexWriter.close();
        }
    }
}
