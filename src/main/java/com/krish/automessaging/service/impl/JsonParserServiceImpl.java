package com.krish.automessaging.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.krish.automessaging.service.JsonParserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class JsonParserServiceImpl implements JsonParserService {
    private final ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
    private final ObjectMapper objectMapper;

    public JsonParserServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Map<String, List<String>> parsePrivilegesJson() throws IOException {
        InputStream inputStream = contextLoader.getResourceAsStream("json/Privileges.json");
        return objectMapper.readValue(inputStream, new TypeReference<>() {
        });
    }
}
