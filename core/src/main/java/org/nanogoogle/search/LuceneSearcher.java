package org.nanogoogle.search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import rx.Observable;

import java.io.IOException;
import java.util.Arrays;

import static java.util.stream.Collectors.toList;

public class LuceneSearcher implements Searcher<Document> {

    Logger logger = LoggerFactory.getLogger(LuceneSearcher.class);
    private final Analyzer analyzer;
    private final Directory directory;

    @Autowired
    public LuceneSearcher(Directory directory, Analyzer analyzer) throws IOException {
        this.directory = directory;
        this.analyzer = analyzer;
    }

    public Observable<Document> search(String searchQuery, int offset, int count) throws IOException {
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        QueryParser parser = new QueryParser("Content", analyzer);
        Query query;
        try {
            query = parser.parse(searchQuery);
        } catch (ParseException e) {
            logger.error(e.getLocalizedMessage(), e);
            return Observable.empty();
        }
        return Observable.from(Arrays.stream(searcher.search(query, count).scoreDocs).map(storeDoc -> {
            try {
                return reader.document(storeDoc.doc);
            } catch (IOException e) {
                logger.error(e.getLocalizedMessage(), e);
                return null;
            }
        }).collect(toList()));
    }
}
