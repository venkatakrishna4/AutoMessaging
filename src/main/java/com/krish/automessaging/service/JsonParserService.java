package com.krish.automessaging.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * The Interface JsonParserService.
 */
public interface JsonParserService {

    /**
     * Parses the privileges json.
     *
     * @return the map
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    Map<String, List<String>> parsePrivilegesJson() throws IOException;
}
