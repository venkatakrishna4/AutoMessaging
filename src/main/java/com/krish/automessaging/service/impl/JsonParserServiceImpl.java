package com.krish.automessaging.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.krish.automessaging.service.JsonParserService;

/**
 * The Class JsonParserServiceImpl.
 */
@Service
public class JsonParserServiceImpl implements JsonParserService {

    /** The context loader. */
    private final ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();

    /** The object mapper. */
    private final ObjectMapper objectMapper;

    /**
     * Instantiates a new json parser service impl.
     *
     * @param objectMapper
     *            the object mapper
     */
    public JsonParserServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Parses the privileges json.
     *
     * @return the map
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    public Map<String, List<String>> parsePrivilegesJson() throws IOException {
        InputStream inputStream = contextLoader.getResourceAsStream("json/Privileges.json");
        return objectMapper.readValue(inputStream, new TypeReference<>() {
        });
    }
}
