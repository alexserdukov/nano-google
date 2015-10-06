package org.nanogoogle.service;

import java.io.IOException;
import java.net.URI;

public interface IndexService {

    int index (URI uri, int level) throws IOException;

}
