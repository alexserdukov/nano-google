package org.nanogoogle.crawler;

import org.nanogoogle.model.SearchDocument;
import rx.Observable;

import java.net.URI;

public interface WebCrawler {

    Observable<SearchDocument> recursiveParse (URI topUri, int level);
}
