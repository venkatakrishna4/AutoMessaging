package com.krish.automessaging.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.core.DeleteRequest;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.UpdateRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.krish.automessaging.datamodel.pojo.User;
import com.krish.automessaging.datamodel.pojo.WhatsAppMessaging;
import com.krish.automessaging.datamodel.pojo.audit.GeneralAudit;
import com.krish.automessaging.datamodel.record.WhatsAppMessagingRecord;
import com.krish.automessaging.enums.IndexEnum;
import com.krish.automessaging.exception.custom.RecordNotFoundException;
import com.krish.automessaging.service.UserWhatsAppMessagingService;
import com.krish.automessaging.utils.AuditUtils;
import com.krish.automessaging.utils.AuthUtils;
import com.krish.automessaging.utils.UserUtils;
import com.krish.automessaging.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class UserWhatsAppMessagingServiceImpl implements UserWhatsAppMessagingService {
    private final ElasticsearchClient client;
    private final UserUtils userUtils;
    private final Utils utils;
    private final AuditUtils auditUtils;
    private final ObjectMapper objectMapper;
    private final AuthUtils authUtils;

    @Autowired
    public UserWhatsAppMessagingServiceImpl(final ElasticsearchClient client, final UserUtils userUtils,
            final Utils utils, final AuditUtils auditUtils, final ObjectMapper objectMapper,
            final AuthUtils authUtils) {
        this.client = client;
        this.userUtils = userUtils;
        this.utils = utils;
        this.auditUtils = auditUtils;
        this.objectMapper = objectMapper;
        this.authUtils = authUtils;
    }

    @Override
    public String saveWhatsAppMessaging(WhatsAppMessagingRecord whatsAppMessagingRecord,
            HttpServletRequest servletRequest) throws IOException {
        log.debug("Received request for saveWhatsAppMessaging\n{}", whatsAppMessagingRecord);
        Optional<User> existingUser = userUtils.getUserByUsernameOrEmailOrID(whatsAppMessagingRecord.userId());
        existingUser.map(u -> {
            if (Objects.isNull(u.getWhatsAppMessaging().getId())) {
                u.getWhatsAppMessaging().setId(Utils.generateUUID());
            }
            u.getWhatsAppMessaging().setFrom(whatsAppMessagingRecord.from());
            u.getWhatsAppMessaging().setTo(whatsAppMessagingRecord.to());
            u.getWhatsAppMessaging().setMessages(whatsAppMessagingRecord.messages());
            IndexRequest<User> indexRequest = IndexRequest
                    .of(request -> request.index(utils.getFinalIndex(IndexEnum.user_index.name())).document(u));
            try {
                client.update(UpdateRequest
                        .of(updateRequest -> updateRequest.index(utils.getFinalIndex(IndexEnum.user_index.name()))
                                .doc(indexRequest).id(u.getId()).upsert(u)),
                        User.class);
                log.debug("Updated user with whatsAppMessage\n{}", u);
                auditUtils.addGeneralAudit(GeneralAudit.builder().id(Utils.generateUUID())
                        .ownerObjectId(existingUser.get().getId()).objectClass(WhatsAppMessaging.class).oldObject(null)
                        .newObject(objectMapper.writeValueAsString(existingUser.get()))
                        .action(GeneralAudit.Audit.UPDATE.toString())
                        .comments("Adding WhatsApp Messaging for Existing User")
                        .loggedInUserId(authUtils.getLoggedInUserId()).clientIP(authUtils.getClientIP(servletRequest))
                        .build());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return u;
        }).orElseThrow(
                () -> new RecordNotFoundException("User Record not found for ID " + whatsAppMessagingRecord.userId()));
        return "WhatsApp Messaging is saved for User " + existingUser.get().getName();
    }

    @Override
    public WhatsAppMessagingRecord getWhatsAppMessaging(String id) throws IOException {
        Optional<WhatsAppMessaging> existingWhatsAppMessaging = userUtils.getWhatsAppMessagingById(id);
        return existingWhatsAppMessaging.map(userUtils::mapToUserWhatsAppMessagingRecord)
                .orElseThrow(() -> new RecordNotFoundException("WhatsApp Messaging Record not found for ID " + id));
    }

    @Override
    public void deleteWhatsAppMessaging(String id, HttpServletRequest servletRequest) throws IOException {
        Optional<WhatsAppMessaging> existingWhatsAppMessaging = userUtils.getWhatsAppMessagingById(id);
        existingWhatsAppMessaging.ifPresentOrElse(whatsAppMessaging -> {
            try {
                Optional<User> existingUser = userUtils.getUserByWhatsAppMessagingId(id);
                existingUser.map(user -> {
                    try {
                        User oldUser = objectMapper.readValue(objectMapper.writeValueAsString(existingUser.get()),
                                User.class);
                        user.setWhatsAppMessaging(new WhatsAppMessaging());
                        IndexRequest<User> indexRequest = IndexRequest.of(request -> request
                                .index(utils.getFinalIndex(IndexEnum.user_index.name())).document(user));
                        client.update(
                                UpdateRequest.of(updateRequest -> updateRequest.doc(indexRequest)
                                        .index(utils.getFinalIndex(IndexEnum.user_index.name())).upsert(user)),
                                User.class);
                        auditUtils.addGeneralAudit(GeneralAudit.builder().id(Utils.generateUUID())
                                .ownerObjectId(existingWhatsAppMessaging.get().getId())
                                .objectClass(WhatsAppMessaging.class)
                                .oldObject(objectMapper.writeValueAsString(oldUser))
                                .newObject(objectMapper.writeValueAsString(existingWhatsAppMessaging.get()))
                                .action(GeneralAudit.Audit.DELETE.toString())
                                .comments("Deleting WhatsApp Messaging from UI")
                                .loggedInUserId(authUtils.getLoggedInUserId())
                                .clientIP(authUtils.getClientIP(servletRequest)).build());
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }

                    return user;
                }).orElseThrow(() -> new RecordNotFoundException("No Record Found for ID " + id));
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }, () -> log.error("Cannot delete Record for ID {}", id));
    }
}
