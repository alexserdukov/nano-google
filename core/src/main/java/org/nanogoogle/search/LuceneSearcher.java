package org.nanogoogle.search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.springframework.beans.factory.annotation.Autowired;
import rx.Observable;

import java.io.IOException;
import java.util.Arrays;

import static java.util.stream.Collectors.toList;

public class LuceneSearcher implements Searcher<Document> {

    private final Directory directory;

    @Autowired
    public LuceneSearcher(Directory directory) {
        this.directory = directory;
    }

    public Observable<Document> search(String searchQuery, int offset, int count) throws IOException {
        try (Analyzer analyzer = new StandardAnalyzer();
             IndexReader indexReader = DirectoryReader.open(directory)) {
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            QueryParser parser = new QueryParser("Content", analyzer);
            Query query = null;
            try {
                query = parser.parse(searchQuery);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return Observable.from(Arrays.stream(indexSearcher.search(query, count).scoreDocs).map(storeDoc -> {
                try {
                    return indexReader.document(storeDoc.doc);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }).collect(toList()));
        }
    }
}
