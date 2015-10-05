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

    public void setFoundUrls(Set<URI> foundUrls) {
        this.foundUrls = foundUrls;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
