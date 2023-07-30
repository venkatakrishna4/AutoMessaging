package com.krish.automessaging.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.krish.automessaging.datamodel.pojo.es.BaseElasticObject;
import com.krish.automessaging.datamodel.pojo.es.ChangeLog;
import com.krish.automessaging.service.AdminElasticService;
import com.krish.automessaging.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j
public class AdminElasticServiceImpl implements AdminElasticService {
    private ElasticsearchClient client;

    @Value("${elastic.tenant}")
    private String tenant;

    public AdminElasticServiceImpl() {
    }

    @Autowired
    public AdminElasticServiceImpl(ElasticsearchClient client) throws IOException {
        this.client = client;
        initializeESChangeLogIndex();
    }

    @Override
    public String getFinalIndex(String indexName) {
        if (StringUtils.isNotBlank(tenant) && StringUtils.isNotBlank(indexName)
                && !StringUtils.startsWith(indexName, tenant.concat("."))) {
            return tenant.concat(".").concat(indexName);
        }
        return indexName;
    }

    @Override
    public boolean isIndexExists(String index) throws ElasticsearchException, IOException {
        return client.indices().exists(r -> r.index(getFinalIndex(index))).value();
    }

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

    @Override
    public ChangeLog createChangeLog(String index, String fileName) {
        return new ChangeLog().setId(Utils.generateUUID()).setIndexName(getFinalIndex(index)).setFileName(fileName)
                .setProcessed(true);
    }

    @Override
    public boolean saveChangeLog(ChangeLog changeLog) throws ElasticsearchException, IOException {
        setAuditProperties(changeLog);
        String id = client.index(i -> i.index(getFinalIndex(INDEX_ES_CHANGE_LOG)).id(changeLog.getId())
                .document(changeLog).refresh(Refresh.True)).id();
        return changeLog.getId().equals(id);
    }

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
