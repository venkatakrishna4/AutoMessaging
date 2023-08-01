package com.krish.automessaging.service;

import java.io.IOException;
import java.io.InputStream;

import com.krish.automessaging.datamodel.pojo.es.BaseElasticObject;
import com.krish.automessaging.datamodel.pojo.es.ChangeLog;

import co.elastic.clients.elasticsearch._types.ElasticsearchException;

/**
 * The interface Admin elastic service.
 */
public interface AdminElasticService {
    /**
     * The constant INDEX_ES_CHANGE_LOG.
     */
    String INDEX_ES_CHANGE_LOG = "es_change_log";

    /**
     * Gets final index.
     *
     * @param indexName
     *            the index name
     *
     * @return the final index
     */
    String getFinalIndex(final String indexName);

    /**
     * Is index exists boolean.
     *
     * @param index
     *            the index
     *
     * @return the boolean
     *
     * @throws ElasticsearchException
     *             the elasticsearch exception
     * @throws IOException
     *             the io exception
     */
    boolean isIndexExists(final String index) throws ElasticsearchException, IOException;

    /**
     * Create index.
     *
     * @param index
     *            the index
     * @param json
     *            the json
     *
     * @throws ElasticsearchException
     *             the elasticsearch exception
     * @throws IOException
     *             the io exception
     */
    void createIndex(final String index, final InputStream json) throws ElasticsearchException, IOException;

    /**
     * Update index settings.
     *
     * @param index
     *            the index
     * @param name
     *            the name
     * @param json
     *            the json
     *
     * @throws ElasticsearchException
     *             the elasticsearch exception
     * @throws IOException
     *             the io exception
     */
    void updateIndexSettings(final String index, final String name, final InputStream json)
            throws ElasticsearchException, IOException;

    /**
     * Update index mappings.
     *
     * @param index
     *            the index
     * @param name
     *            the name
     * @param json
     *            the json
     *
     * @throws ElasticsearchException
     *             the elasticsearch exception
     * @throws IOException
     *             the io exception
     */
    void updateIndexMappings(final String index, final String name, final InputStream json)
            throws ElasticsearchException, IOException;

    /**
     * Initialize es change log index.
     *
     * @throws ElasticsearchException
     *             the elasticsearch exception
     * @throws IOException
     *             the io exception
     */
    void initializeESChangeLogIndex() throws ElasticsearchException, IOException;

    /**
     * Is change log processed boolean.
     *
     * @param index
     *            the index
     * @param fileName
     *            the file name
     *
     * @return the boolean
     *
     * @throws ElasticsearchException
     *             the elasticsearch exception
     * @throws IOException
     *             the io exception
     */
    boolean isChangeLogProcessed(final String index, final String fileName) throws ElasticsearchException, IOException;

    /**
     * Create change log.
     *
     * @param index
     *            the index
     * @param fileName
     *            the file name
     *
     * @return the change log
     */
    ChangeLog createChangeLog(final String index, final String fileName);

    /**
     * Save change log boolean.
     *
     * @param changeLog
     *            the change log
     *
     * @return the boolean
     *
     * @throws ElasticsearchException
     *             the elasticsearch exception
     * @throws IOException
     *             the io exception
     */
    boolean saveChangeLog(final ChangeLog changeLog) throws ElasticsearchException, IOException;

    /**
     * Sets audit properties.
     *
     * @param source
     *            the source
     */
    void setAuditProperties(BaseElasticObject source);

}
