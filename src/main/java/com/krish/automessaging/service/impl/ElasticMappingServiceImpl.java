package com.krish.automessaging.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import com.krish.automessaging.service.AdminElasticService;
import com.krish.automessaging.service.ElasticMappingService;

import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class ElasticMappingServiceImpl.
 */
@Service

/** The Constant log. */
@Slf4j
public class ElasticMappingServiceImpl implements ElasticMappingService {

    /** The admin elastic service. */
    private final AdminElasticService adminElasticService;

    /** The index set. */
    private final Set<String> indexSet = new HashSet<>();

    /** The index setting map. */
    private final Map<String, IndexFile> indexSettingMap = new HashMap<>();

    /** The update setting map. */
    private final Map<String, List<IndexFile>> updateSettingMap = new HashMap<>();

    /** The update mapping map. */
    private final Map<String, List<IndexFile>> updateMappingMap = new HashMap<>();

    /**
     * Instantiates a new elastic mapping service impl.
     *
     * @param adminElasticService
     *            the admin elastic service
     */
    @Autowired
    public ElasticMappingServiceImpl(final AdminElasticService adminElasticService) {
        this.adminElasticService = adminElasticService;
    }

    /**
     * Initialize indexes.
     *
     * @throws URISyntaxException
     *             the URI syntax exception
     * @throws ElasticsearchException
     *             the elasticsearch exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    public void initializeIndexes() throws URISyntaxException, ElasticsearchException, IOException {
        scanIndexConfigurations("es_doc/");
        if (indexSet.size() > 0) {
            for (String index : indexSet) {
                if (!"sampleindex".equals(index)) { // Ignore sampleindex
                    if (!adminElasticService.isIndexExists(index)) {
                        createIndex(index);
                    }
                    if (updateSettingMap.containsKey(index)) {
                        updateIndexSettings(index);
                    }
                    if (updateMappingMap.containsKey(index)) {
                        updateIndexMappings(index);
                    }
                }
            }
        }
    }

    /**
     * Scan index configurations.
     *
     * @param path
     *            the path
     *
     * @throws URISyntaxException
     *             the URI syntax exception
     * @throws ElasticsearchException
     *             the elasticsearch exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    public void scanIndexConfigurations(String path) throws URISyntaxException, ElasticsearchException, IOException {
        URL url = this.getClass().getClassLoader().getResource(path);
        if (url != null) {
            if ("file".equals(url.getProtocol())) {
                processFilePathIndexes(url);
            } else if ("jar".equals(url.getProtocol())) {
                processJarPathIndexes(path);
            }
        }
    }

    /**
     * Process file path indexes.
     *
     * @param url
     *            the url
     *
     * @throws URISyntaxException
     *             the URI syntax exception
     * @throws ElasticsearchException
     *             the elasticsearch exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    public void processFilePathIndexes(URL url) throws URISyntaxException, ElasticsearchException, IOException {
        File rootESDir = new File(url.toURI());
        if (rootESDir.isDirectory()) {
            File[] files = rootESDir.listFiles();
            assert files != null;
            for (File indexDir : files) {
                if (indexDir.isDirectory()) {
                    File[] children = indexDir.listFiles();
                    boolean found = false;
                    assert children != null;
                    for (File indexFile : children) {
                        if ("index.json".equals(indexFile.getName())) {
                            indexSettingMap.put(indexDir.getName(),
                                    new IndexFile(indexFile.getName(), indexFile.getAbsolutePath()));
                            found = true;
                        } else if (indexFile.getName().endsWith(".setting.json")) {
                            List<IndexFile> indexFiles = updateSettingMap.computeIfAbsent(indexDir.getName(),
                                    k -> new ArrayList<>());
                            try {
                                indexFiles.add(new IndexFile(indexFile.getName(), indexFile.getAbsolutePath()));
                            } catch (NumberFormatException e) {
                                log.warn("Wrong file name, settings update wont be process for index {} - {}",
                                        indexDir.getName(), indexFile.getName());
                            }
                        } else if (indexFile.getName().endsWith(".mapping.json")) {
                            List<IndexFile> indexFiles = updateMappingMap.computeIfAbsent(indexDir.getName(),
                                    k -> new ArrayList<IndexFile>());
                            try {
                                indexFiles.add(new IndexFile(indexFile.getName(), indexFile.getAbsolutePath()));
                            } catch (NumberFormatException e) {
                                log.warn("Wrong file name, mapping update wont be process for index {} - {}",
                                        indexDir.getName(), indexFile.getName());
                            }
                        }
                    }
                    if (found) {
                        indexSet.add(indexDir.getName());
                    } else {
                        log.warn("No index.json found for {}", indexDir.getName());
                    }
                }
            }
        }
    }

    /**
     * Process jar path indexes.
     *
     * @param path
     *            the path
     *
     * @throws URISyntaxException
     *             the URI syntax exception
     * @throws ElasticsearchException
     *             the elasticsearch exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    public void processJarPathIndexes(String path) throws URISyntaxException, ElasticsearchException, IOException {
        ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
        Resource[] indexMappings = patternResolver.getResources("classpath:" + path + "*index/");
        for (Resource resource : indexMappings) {
            log.info("found Resouece {}", resource.getURL());
            String index = getIndexFromURL(resource.getURL());
            if (StringUtils.isNoneBlank(index)) {
                boolean found = false;
                Resource[] jsonMappings = patternResolver.getResources("classpath:" + path + index + "/**.json");
                for (Resource indexJson : jsonMappings) {
                    final String fileName = indexJson.getFilename();
                    if ("index.json".equals(fileName)) {
                        indexSettingMap.put(index, new IndexFile(indexJson.getFilename(), indexJson));
                        found = true;
                    } else if (StringUtils.isNotBlank(fileName) && fileName.endsWith(".setting.json")) {
                        List<IndexFile> indexFiles = updateSettingMap.computeIfAbsent(index, k -> new ArrayList<>());
                        try {
                            indexFiles.add(new IndexFile(indexJson.getFilename(), indexJson));
                        } catch (NumberFormatException e) {
                            log.warn("Wrong file name, settings update wont be process for index {} - {}", index,
                                    indexJson.getFilename());
                        }
                    } else if (StringUtils.isNotBlank(fileName) && fileName.endsWith(".mapping.json")) {
                        List<IndexFile> indexFiles = updateMappingMap.computeIfAbsent(index, k -> new ArrayList<>());
                        try {
                            indexFiles.add(new IndexFile(indexJson.getFilename(), indexJson));
                        } catch (NumberFormatException e) {
                            log.warn("Wrong file name, mapping update wont be process for index {} - {}", index,
                                    indexJson.getFilename());
                        }
                    }
                }
                if (found) {
                    indexSet.add(index);
                } else {
                    log.warn("No index.json found for {}", index);
                }
            }
        }
    }

    /**
     * Gets the index from URL.
     *
     * @param url
     *            the url
     *
     * @return the index from URL
     */
    @Override
    public String getIndexFromURL(URL url) {
        String str = url.toString();
        int index = str.lastIndexOf("/");
        if (index > -1) {
            int secondLastIndex = str.substring(0, index).lastIndexOf("/");
            if (secondLastIndex > -1) {
                return str.substring(secondLastIndex + 1, index);
            }
        }
        return null;
    }

    /**
     * Creates the index.
     *
     * @param index
     *            the index
     *
     * @throws ElasticsearchException
     *             the elasticsearch exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    public void createIndex(String index) throws ElasticsearchException, IOException {
        try (InputStream json = getIndexSettings(index)) {
            adminElasticService.createIndex(index, json);
        }
    }

    /**
     * Update index settings.
     *
     * @param index
     *            the index
     *
     * @throws ElasticsearchException
     *             the elasticsearch exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    public void updateIndexSettings(String index) throws ElasticsearchException, IOException {
        List<IndexFile> list = updateSettingMap.get(index);
        if (list != null && list.size() > 0) {
            Collections.sort(list);
            for (IndexFile indexFile : list) {
                updateIndexSettings(index, indexFile.fileName);
            }
        }
    }

    /**
     * Update index settings.
     *
     * @param index
     *            the index
     * @param name
     *            the name
     *
     * @throws ElasticsearchException
     *             the elasticsearch exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    public void updateIndexSettings(String index, String name) throws ElasticsearchException, IOException {
        try (InputStream json = getJson(updateSettingMap, index, name)) {
            adminElasticService.updateIndexSettings(index, name, json);
        }
    }

    /**
     * Update index mappings.
     *
     * @param index
     *            the index
     *
     * @throws ElasticsearchException
     *             the elasticsearch exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    public void updateIndexMappings(String index) throws ElasticsearchException, IOException {
        List<IndexFile> list = updateMappingMap.get(index);
        if (list != null && list.size() > 0) {
            Collections.sort(list);
            for (IndexFile indexFile : list) {
                updateIndexMappings(index, indexFile.fileName);
            }
        }
    }

    /**
     * Update index mappings.
     *
     * @param index
     *            the index
     * @param name
     *            the name
     *
     * @throws ElasticsearchException
     *             the elasticsearch exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    public void updateIndexMappings(String index, String name) throws ElasticsearchException, IOException {
        try (InputStream json = getJson(updateMappingMap, index, name)) {
            adminElasticService.updateIndexMappings(index, name, json);
        }
    }

    /**
     * Gets the index settings.
     *
     * @param index
     *            the index
     *
     * @return the index settings
     */
    @Override
    public InputStream getIndexSettings(String index) {
        try {
            IndexFile file = indexSettingMap.get(index);
            if (file != null) {
                if (file.resource != null) {
                    return file.resource.getInputStream();
                }
                return new FileInputStream(file.filePath);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Gets the json.
     *
     * @param map
     *            the map
     * @param index
     *            the index
     * @param name
     *            the name
     *
     * @return the json
     */
    @Override
    public InputStream getJson(Map<String, List<IndexFile>> map, String index, String name) {
        List<IndexFile> list = map.get(index);
        if (list != null) {
            IndexFile indexFile = list.stream().filter(e -> e.fileName.equals(name)).findFirst().orElse(null);
            if (indexFile != null) {
                try {
                    if (indexFile.resource != null) {
                        return indexFile.resource.getInputStream();
                    }
                    return new FileInputStream(indexFile.filePath);
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return null;
    }
}
