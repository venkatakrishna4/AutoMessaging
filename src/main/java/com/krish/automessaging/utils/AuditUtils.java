package com.krish.automessaging.utils;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.krish.automessaging.datamodel.pojo.audit.GeneralAudit;
import com.krish.automessaging.enums.IndexEnum;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;

/**
 * The Class AuditUtils.
 */
@Component
public class AuditUtils {

    /** The client. */
    private final ElasticsearchClient client;

    /** The utils. */
    private final Utils utils;

    /** The object mapper. */
    private final ObjectMapper objectMapper;

    /**
     * Instantiates a new audit utils.
     *
     * @param client
     *            the client
     * @param utils
     *            the utils
     * @param objectMapper
     *            the object mapper
     */
    @Autowired
    public AuditUtils(final ElasticsearchClient client, Utils utils, ObjectMapper objectMapper) {
        this.client = client;
        this.utils = utils;
        this.objectMapper = objectMapper;
    }

    /**
     * Adds the general audit.
     *
     * @param generalAudit
     *            the general audit
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public void addGeneralAudit(GeneralAudit generalAudit) throws IOException {
        client.index(IndexRequest
                .of(indexRequest -> indexRequest.index(utils.getFinalIndex(IndexEnum.general_audit_index.toString()))
                        .id(generalAudit.getId()).document(generalAudit)));
    }
}
