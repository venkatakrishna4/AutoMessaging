package com.krish.automessaging.service;

import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The interface Elastic mapping service.
 */
public interface ElasticMappingService {
    /**
     * Initialize indexes.
     *
     * @throws URISyntaxException
     *             the uri syntax exception
     * @throws ElasticsearchException
     *             the elasticsearch exception
     * @throws IOException
     *             the io exception
     */
    void initializeIndexes() throws URISyntaxException, ElasticsearchException, IOException;

    /**
     * Scan index configurations.
     *
     * @param path
     *            the path
     *
     * @throws URISyntaxException
     *             the uri syntax exception
     * @throws ElasticsearchException
     *             the elasticsearch exception
     * @throws IOException
     *             the io exception
     */
    void scanIndexConfigurations(String path) throws URISyntaxException, ElasticsearchException, IOException;

    /**
     * Process file path indexes.
     *
     * @param url
     *            the url
     *
     * @throws URISyntaxException
     *             the uri syntax exception
     * @throws ElasticsearchException
     *             the elasticsearch exception
     * @throws IOException
     *             the io exception
     */
    void processFilePathIndexes(URL url) throws URISyntaxException, ElasticsearchException, IOException;

    /**
     * Process jar path indexes.
     *
     * @param path
     *            the path
     *
     * @throws URISyntaxException
     *             the uri syntax exception
     * @throws ElasticsearchException
     *             the elasticsearch exception
     * @throws IOException
     *             the io exception
     */
    void processJarPathIndexes(String path) throws URISyntaxException, ElasticsearchException, IOException;

    /**
     * Gets index from url.
     *
     * @param url
     *            the url
     *
     * @return the index from url
     */
    String getIndexFromURL(URL url);

    /**
     * Create index.
     *
     * @param index
     *            the index
     *
     * @throws ElasticsearchException
     *             the elasticsearch exception
     * @throws IOException
     *             the io exception
     */
    void createIndex(String index) throws ElasticsearchException, IOException;

    /**
     * Update index settings.
     *
     * @param index
     *            the index
     *
     * @throws ElasticsearchException
     *             the elasticsearch exception
     * @throws IOException
     *             the io exception
     */
    void updateIndexSettings(String index) throws ElasticsearchException, IOException;

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
     *             the io exception
     */
    void updateIndexSettings(String index, String name) throws ElasticsearchException, IOException;

    /**
     * Update index mappings.
     *
     * @param index
     *            the index
     *
     * @throws ElasticsearchException
     *             the elasticsearch exception
     * @throws IOException
     *             the io exception
     */
    void updateIndexMappings(String index) throws ElasticsearchException, IOException;

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
     *             the io exception
     */
    void updateIndexMappings(String index, String name) throws ElasticsearchException, IOException;

    /**
     * Gets index settings.
     *
     * @param index
     *            the index
     *
     * @return the index settings
     */
    InputStream getIndexSettings(String index);

    /**
     * Gets json.
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
    InputStream getJson(Map<String, List<IndexFile>> map, String index, String name);

    /**
     * The type Index file.
     */
    class IndexFile implements Comparable<IndexFile> {
        /**
         * The File name.
         */
        public String fileName;
        /**
         * The File path.
         */
        public String filePath;
        /**
         * The Resource.
         */
        public Resource resource;
        /**
         * The Seq.
         */
        public int seq = 0;

        /**
         * Instantiates a new Index file.
         *
         * @param fileName
         *            the file name
         * @param filePath
         *            the file path
         *
         * @throws NumberFormatException
         *             the number format exception
         */
        public IndexFile(String fileName, String filePath) throws NumberFormatException {
            super();
            this.fileName = fileName;
            this.filePath = filePath;
            if (!"index.json".equals(fileName) && fileName.indexOf(".") > 0) {
                try {
                    Integer.parseInt(fileName.substring(0, fileName.indexOf(".")));
                } catch (NumberFormatException ignored) {
                }
            }
        }

        /**
         * Instantiates a new Index file.
         *
         * @param fileName
         *            the file name
         * @param resource
         *            the resource
         *
         * @throws NumberFormatException
         *             the number format exception
         */
        public IndexFile(String fileName, Resource resource) throws NumberFormatException {
            super();
            this.fileName = fileName;
            this.resource = resource;
            if (!"index.json".equals(fileName) && fileName.indexOf(".") > 0) {
                try {
                    Integer.parseInt(fileName.substring(0, fileName.indexOf(".")));
                } catch (NumberFormatException ignored) {
                }
            }
        }

        @Override
        public int compareTo(IndexFile o) {
            return Integer.compare(seq, o.seq);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            IndexFile indexFile = (IndexFile) o;
            return seq == indexFile.seq && Objects.equals(fileName, indexFile.fileName)
                    && Objects.equals(filePath, indexFile.filePath) && Objects.equals(resource, indexFile.resource);
        }

        @Override
        public int hashCode() {
            return Objects.hash(fileName, filePath, resource, seq);
        }
    }

}
