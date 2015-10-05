package org.nanogoogle.service;

import org.nanogoogle.model.SearchDocument;

import java.io.IOException;
import java.util.List;

public interface SearchService {

    List<SearchDocument> search (String query, int offset, int count) throws IOException;
}
