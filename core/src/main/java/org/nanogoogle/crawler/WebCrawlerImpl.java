package org.nanogoogle.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.nanogoogle.model.SearchDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static java.util.stream.Collectors.toSet;
import static rx.Observable.from;
import static rx.Observable.merge;

public class WebCrawlerImpl implements WebCrawler {

    Logger logger = LoggerFactory.getLogger(WebCrawlerImpl.class);

    @Override
    public Observable<SearchDocument> recursiveParse(URI topUri, final int level){
        return recursiveParse(parseByUri(topUri), level - 1);
    }

    private Observable<SearchDocument> recursiveParse(Observable<SearchDocument> parentDocuments, final int level){
        if (level < 1) return parentDocuments;
        return merge(parentDocuments, recursiveParse(parentDocuments.flatMap(this::listWebDocuments), level - 1));

    }

    private Observable<SearchDocument> listWebDocuments(SearchDocument parentDocument) {
        return from(parentDocument.getFoundUrls()).flatMap(uri -> {
            if (!uri.toString().startsWith("http"))
                return parseByUri(URI.create(parentDocument.getUri() + uri));
            else return parseByUri(uri);
        });
    }

    private Observable<SearchDocument> parseByUri(URI uri) {
        return Observable.<SearchDocument>create(subscriber -> {
            try {
                logger.debug(Thread.currentThread().getName() + ": parsing by URL " + uri);
                Document document = null;
                try {
                    document = Jsoup.connect(uri.toString()).get();
                } catch (IOException e) {
                    logger.error("Exception {} occured when connecting", e.getMessage());
                }
                if (document != null)
                    subscriber.onNext(new SearchDocument(
                        document.title(),
                        uri.toString(),
                        Jsoup.parse(document.body().html()).text(),
                        Jsoup.parse(document.body().html()).getElementsByTag("a").stream()
                            .map(element -> {
                                try {
                                    return new URI(element.attr("href"));
                                } catch (URISyntaxException e) {
                                    logger.debug("Error parse of URL " + uri);
                                    return null;
                                }
                            }).collect(toSet())));
                subscriber.onCompleted();
            } catch (Exception exc) {
                logger.error("Exception {} occured", exc.getMessage());
                logger.debug("Uri {} is not parsed", uri);
            }
        }).subscribeOn(Schedulers.computation());
    }

}
