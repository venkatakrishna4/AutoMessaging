package com.krish.automessaging.service.impl;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.krish.automessaging.datamodel.pojo.es.BaseElasticObject;
import com.krish.automessaging.datamodel.pojo.es.ChangeLog;
import com.krish.automessaging.service.AdminElasticService;
import com.krish.automessaging.utils.Utils;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class AdminElasticServiceImpl.
 */
@Service

/** The Constant log. */
@Slf4j
public class AdminElasticServiceImpl implements AdminElasticService {

    /** The client. */
    private ElasticsearchClient client;

    /** The tenant. */
    @Value("${elastic.tenant}")
    private String tenant;

    /**
     * Instantiates a new admin elastic service impl.
     */
    public AdminElasticServiceImpl() {
    }

    /**
     * Instantiates a new admin elastic service impl.
     *
     * @param client
     *            the client
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Autowired
    public AdminElasticServiceImpl(ElasticsearchClient client) throws IOException {
        this.client = client;
        initializeESChangeLogIndex();
    }

    /**
     * Gets the final index.
     *
     * @param indexName
     *            the index name
     *
     * @return the final index
     */
    @Override
    public String getFinalIndex(String indexName) {
        if (StringUtils.isNotBlank(tenant) && StringUtils.isNotBlank(indexName)
                && !StringUtils.startsWith(indexName, tenant.concat("."))) {
            return tenant.concat(".").concat(indexName);
        }
        return indexName;
    }

    /**
     * Checks if is index exists.
     *
     * @param index
     *            the index
     *
     * @return true, if is index exists
     *
     * @throws ElasticsearchException
     *             the elasticsearch exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    public boolean isIndexExists(String index) throws ElasticsearchException, IOException {
        return client.indices().exists(r -> r.index(getFinalIndex(index))).value();
    }

    /**
     * Creates the index.
     *
     * @param index
     *            the index
     * @param json
     *            the json
     *
     * @throws ElasticsearchException
     *             the elasticsearch exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    public void createIndex(String index, InputStream json) throws ElasticsearchException, IOException {
        final boolean isCreated = client.indices()
                .create(indexRequest -> indexRequest.index(getFinalIndex(index)).withJson(json)).acknowledged();
        if (isCreated) {
            log.info("Created new index: {}", getFinalIndex(index));
        } else {
            log.warn("Couldn't create index: {}", getFinalIndex(index));
        }
    }

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
     *             Signals that an I/O exception has occurred.
     */
    @Override
    public void updateIndexSettings(String index, String name, InputStream json)
            throws ElasticsearchException, IOException {
        if (!isChangeLogProcessed(getFinalIndex(index), name)) {
            boolean isUpdated = client.indices()
                    .putSettings(settings -> settings.index(getFinalIndex(index)).withJson(json)).acknowledged();
            if (isUpdated) {
                saveChangeLog(createChangeLog(index, name));
                log.info("Updated index settings for: {}", getFinalIndex(index));
            } else {
                log.warn("Couldn't update index settings for: {}", getFinalIndex(index));
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
     * @param json
     *            the json
     *
     * @throws ElasticsearchException
     *             the elasticsearch exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    public void updateIndexMappings(String index, String name, InputStream json)
            throws ElasticsearchException, IOException {
        if (!isChangeLogProcessed(getFinalIndex(index), name)) {
            boolean isUpdated = client.indices()
                    .putMapping(mapping -> mapping.index(getFinalIndex(index)).withJson(json)).acknowledged();
            if (isUpdated) {
                saveChangeLog(createChangeLog(index, name));
                log.info("Updated index mapping for: {}", getFinalIndex(index));
            } else {
                log.warn("Couldn't update index mapping for: {}", getFinalIndex(index));
            }
        }
    }

    /**
     * Initialize ES change log index.
     *
     * @throws ElasticsearchException
     *             the elasticsearch exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    public void initializeESChangeLogIndex() throws ElasticsearchException, IOException {
        if (!isIndexExists(getFinalIndex(INDEX_ES_CHANGE_LOG))) {
            client.indices()
                    .create(r -> r.index(getFinalIndex(INDEX_ES_CHANGE_LOG))
                            .mappings(m -> m.properties("id", p -> p.keyword(d -> d.index(true).store(true)))
                                    .properties("indexName", p -> p.keyword(d -> d.index(true).store(true)))
                                    .properties("fileName", p -> p.keyword(d -> d.index(true).store(true)))
                                    .properties("processed", p -> p.boolean_(d -> d.index(true).store(true)))
                                    .properties("createdBy", p -> p.keyword(d -> d.index(true).store(true)))
                                    .properties("updatedBy", p -> p.keyword(d -> d.index(true).store(true)))
                                    .properties("createdTime", p -> p.long_(d -> d.index(true).store(true)))
                                    .properties("lastUpdatedTime", p -> p.long_(d -> d.index(true).store(true)))));
        }
    }

    /**
     * Checks if is change log processed.
     *
     * @param index
     *            the index
     * @param fileName
     *            the file name
     *
     * @return true, if is change log processed
     *
     * @throws ElasticsearchException
     *             the elasticsearch exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    public boolean isChangeLogProcessed(String index, String fileName) throws ElasticsearchException, IOException {
        Query query = new Query.Builder()
                .bool(b -> b.must(q -> q.term(t -> t.field("indexName").value(getFinalIndex(index))))
                        .must(q -> q.term(t -> t.field("fileName").value(fileName)))
                        .must(q -> q.term(t -> t.field("processed").value(true))))
                .build();
        long count = client.count(cr -> cr.index(getFinalIndex(INDEX_ES_CHANGE_LOG)).query(query)).count();
        return count > 0;
    }

    /**
     * Creates the change log.
     *
     * @param index
     *            the index
     * @param fileName
     *            the file name
     *
     * @return the change log
     */
    @Override
    public ChangeLog createChangeLog(String index, String fileName) {
        return new ChangeLog().setId(Utils.generateUUID()).setIndexName(getFinalIndex(index)).setFileName(fileName)
                .setProcessed(true);
    }

    /**
     * Save change log.
     *
     * @param changeLog
     *            the change log
     *
     * @return true, if successful
     *
     * @throws ElasticsearchException
     *             the elasticsearch exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    public boolean saveChangeLog(ChangeLog changeLog) throws ElasticsearchException, IOException {
        setAuditProperties(changeLog);
        String id = client.index(i -> i.index(getFinalIndex(INDEX_ES_CHANGE_LOG)).id(changeLog.getId())
                .document(changeLog).refresh(Refresh.True)).id();
        return changeLog.getId().equals(id);
    }

    /**
     * Sets the audit properties.
     *
     * @param source
     *            the new audit properties
     */
    @Override
    public void setAuditProperties(BaseElasticObject source) {
        final String user = "system";
        long time = System.currentTimeMillis();
        if (StringUtils.isBlank(source.getCreatedBy())) {
            source.setCreatedBy(user);
        }
        if (source.getCreatedTime() <= 0) {
            source.setCreatedTime(time);
        }
        source.setLastUpdatedBy(user);
        source.setLastUpdatedTime(time);
    }
}
