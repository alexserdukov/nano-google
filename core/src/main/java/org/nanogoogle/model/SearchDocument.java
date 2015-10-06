package org.nanogoogle.model;

import java.net.URI;
import java.util.Set;

public class SearchDocument {

    private String title;
    private String uri;
    private String content;
    private Set<URI> foundUrls;


    public SearchDocument(String title, String uri, String content, Set<URI> foundUrls) {
        this.title = title;
        this.uri = uri;
        this.content = content;
        this.foundUrls = foundUrls;
    }

    public Set<URI> getFoundUrls() {
        return foundUrls;
    }

    public String getUri() {
        return uri;
    }

    public String getContent() {
        return content;
    }


    public String getTitle() {
        return title;
    }

    @Override
    public int hashCode() {
        return uri.hashCode();
    }

    @Override
    public boolean equals(Object that) {
        if (that == null) return false;
        if (that.getClass() != this.getClass()) return false;
        SearchDocument thatDoc = (SearchDocument) that;
        return thatDoc.getUri().equals(this.getUri());
    }
}
