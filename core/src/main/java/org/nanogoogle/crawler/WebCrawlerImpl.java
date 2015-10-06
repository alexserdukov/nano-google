package org.nanogoogle.crawler;

import com.google.common.collect.Sets;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.nanogoogle.model.SearchDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;

import static java.util.stream.Collectors.toSet;
import static rx.Observable.merge;

public class WebCrawlerImpl implements WebCrawler {

    Logger logger = LoggerFactory.getLogger(WebCrawlerImpl.class);

    @Override
    public Observable<SearchDocument> recursiveParse(URI topUri, final int level) {
        return recursiveParse(parseByUri(topUri, uri -> uri), level - 1);
    }

    private Observable<SearchDocument> recursiveParse(Observable<SearchDocument> searchDocument,
                                                      final int level) {
        if (level < 1)
            return searchDocument;
        return merge(searchDocument, recursiveParse(searchDocument
            .flatMap(doc -> Observable.from(doc.getFoundUrls())
                .subscribeOn(Schedulers.from(new ForkJoinPool(Runtime.getRuntime().availableProcessors() * 10)))
                .flatMap(uri -> {
                    try {
                        return parseByUri(uri, uriToHandle -> handleRelativeURI(uriToHandle, doc));
                    } catch (Exception exc) {
                        return Observable.empty();
                    }
                })), level - 1));

    }

    private Observable<SearchDocument> parseByUri(URI uri, Function<URI, URI> uriHandler) {
        SearchDocument searchDocument;
        logger.info(Thread.currentThread().getName() + ": parsing by URL " + uri);
        Document document = null;
        try {
            document = Jsoup.connect(uri.toString()).get();
        } catch (IOException e) {
            logger.debug(e.getLocalizedMessage());
        }

        if (document != null && document.body() != null && document.body().html() != null) {
            Document plainDoc = Jsoup.parse(Jsoup.clean(document.body().html(), Whitelist.basic()));
            searchDocument = new SearchDocument(
                document.title(),
                uri.toString(),
                plainDoc == null ? "" : plainDoc.text(),
                plainDoc == null ? Sets.newHashSet() : plainDoc.getElementsByTag("a").stream()
                    .map(element -> {
                        try {
                            return uriHandler.apply(new URI(element.attr("href")));
                        } catch (URISyntaxException e) {
                            logger.debug(e.getLocalizedMessage());
                            return null;
                        }
                    }).filter(elem -> elem != null).collect(toSet()));
            return Observable.just(searchDocument);
        }
        return Observable.empty();
    }

    private URI handleRelativeURI(URI uri, SearchDocument doc) {
        if (!uri.isAbsolute())
            return URI.create(doc.getUri()).resolve(uri);
        else
            return uri;
    }
}
