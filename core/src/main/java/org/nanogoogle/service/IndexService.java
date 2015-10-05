package org.nanogoogle.service;

import java.io.IOException;
import java.net.URI;

public interface IndexService {

    void index (URI uri, int level) throws IOException;

}
