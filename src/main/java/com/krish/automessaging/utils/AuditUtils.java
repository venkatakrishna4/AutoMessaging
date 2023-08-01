package com.krish.automessaging.utils;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.krish.automessaging.datamodel.pojo.audit.GeneralAudit;
import com.krish.automessaging.enums.IndexEnum;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;

@Component
public class AuditUtils {
    private final ElasticsearchClient client;
    private final Utils utils;
    private final ObjectMapper objectMapper;

    @Autowired
    public AuditUtils(final ElasticsearchClient client, Utils utils, ObjectMapper objectMapper) {
        this.client = client;
        this.utils = utils;
        this.objectMapper = objectMapper;
    }

    public void addGeneralAudit(GeneralAudit generalAudit) throws IOException {
        client.index(IndexRequest
                .of(indexRequest -> indexRequest.index(utils.getFinalIndex(IndexEnum.general_audit_index.toString()))
                        .id(generalAudit.getId()).document(generalAudit)));
    }
}
