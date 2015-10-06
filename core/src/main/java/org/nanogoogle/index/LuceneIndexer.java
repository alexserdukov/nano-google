package org.nanogoogle.index;

import com.google.common.collect.Lists;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import rx.Observable;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class LuceneIndexer implements Indexer<Document> {

    Logger logger = LoggerFactory.getLogger(LuceneIndexer.class);
    private IndexWriter writer;

    @Autowired
    public LuceneIndexer(Directory directory, Analyzer analyzer) throws IOException {
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        this.writer = new IndexWriter(directory, iwc);
    }

    @Override
    public int index(Observable<Document> observableDocuments) {
        try {
            CountDownLatch latch = new CountDownLatch(1);
            List<Document> documents = Lists.newArrayList();
            logger.info("Waiting for documents");
            observableDocuments.subscribe(documents::add,
                error -> {
                    logger.error(error.getLocalizedMessage(), error);
                    latch.countDown();
                }, latch::countDown);
            latch.await();
            logger.info("Starting to index");
            writer.addDocuments(documents);
            writer.commit();
            logger.info("Indexed " + documents.size() + " documents" );
            return documents.size();
        } catch (IOException | InterruptedException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return 0;
    }
}
