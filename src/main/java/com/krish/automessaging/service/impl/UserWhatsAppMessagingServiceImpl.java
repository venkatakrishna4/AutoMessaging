package com.krish.automessaging.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.UpdateRequest;
import jakarta.servlet.http.HttpServletRequest;

/**
 * The Class UserWhatsAppMessagingServiceImpl.
 */
@Service
public class UserWhatsAppMessagingServiceImpl implements UserWhatsAppMessagingService {

    private static final Logger log = LoggerFactory.getLogger(UserWhatsAppMessagingServiceImpl.class);

    /** The client. */
    private final ElasticsearchClient client;

    /** The user utils. */
    private final UserUtils userUtils;

    /** The utils. */
    private final Utils utils;

    /** The audit utils. */
    private final AuditUtils auditUtils;

    /** The object mapper. */
    private final ObjectMapper objectMapper;

    /** The auth utils. */
    private final AuthUtils authUtils;

    /**
     * Instantiates a new user whats app messaging service impl.
     *
     * @param client
     *            the client
     * @param userUtils
     *            the user utils
     * @param utils
     *            the utils
     * @param auditUtils
     *            the audit utils
     * @param objectMapper
     *            the object mapper
     * @param authUtils
     *            the auth utils
     */
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

    /**
     * Save whats app messaging.
     *
     * @param whatsAppMessagingRecord
     *            the whats app messaging record
     * @param servletRequest
     *            the servlet request
     *
     * @return the string
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    public String saveWhatsAppMessaging(WhatsAppMessagingRecord whatsAppMessagingRecord,
            HttpServletRequest servletRequest) throws IOException {
        log.debug("Received request for saveWhatsAppMessaging\n{}", whatsAppMessagingRecord);
        /*
         * Get the existing user by using userId
         */
        Optional<User> existingUser = userUtils.getUserByUsernameOrEmailOrID(whatsAppMessagingRecord.userId());

        /*
         * If the existing user not found, then throw RecordNotFoundException
         */
        if (existingUser.isEmpty()) {
            throw new RecordNotFoundException("No Record found for the ID " + whatsAppMessagingRecord.userId());
        }

        /*
         * If the user exists, then update the user with required WhatsApp Messaging information
         */
        User oldUser = getNewObject(existingUser.get());
        Optional<User> user = existingUser.map(u -> {
            if (StringUtils.isBlank(u.getWhatsAppMessaging().getId())) {
                u.getWhatsAppMessaging().setId(Utils.generateUUID());
            }
            u.getWhatsAppMessaging().setFrom(getTrimmedValue(whatsAppMessagingRecord.from()));
            u.getWhatsAppMessaging().getMessages().addAll(whatsAppMessagingRecord.messages());
            IndexRequest<User> indexRequest = IndexRequest
                    .of(request -> request.index(utils.getFinalIndex(IndexEnum.user_index.name())).document(u));
            try {
                client.update(UpdateRequest
                        .of(updateRequest -> updateRequest.index(utils.getFinalIndex(IndexEnum.user_index.name()))
                                .doc(indexRequest).id(u.getId()).upsert(u)),
                        User.class);
                log.debug("Updated user with whatsAppMessage\n{}", u);
                auditUtils.addGeneralAudit(new GeneralAudit.Builder().setId(Utils.generateUUID())
                        .setOwnerObjectId(existingUser.get().getId()).setObjectClass(WhatsAppMessaging.class)
                        .setOldObject(objectMapper.writeValueAsString(oldUser))
                        .setNewObject(objectMapper.writeValueAsString(existingUser.get()))
                        .setAction(GeneralAudit.Audit.UPDATE.toString())
                        .setComments("Adding WhatsApp Messaging for Existing User")
                        .setLoggedInUserId(authUtils.getLoggedInUserId())
                        .setClientIP(authUtils.getClientIP(servletRequest)).build());
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }

            return u;
        });
        return "WhatsApp Messaging is saved for User " + user.get().getName();
    }

    /**
     * Gets the whats app messaging.
     *
     * @param userId
     *            the user id
     * @param id
     *            the id
     *
     * @return the whats app messaging
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    public WhatsAppMessagingRecord getWhatsAppMessaging(String userId, String id) throws IOException {
        Optional<WhatsAppMessaging> existingWhatsAppMessaging = userUtils.getWhatsAppMessagingById(userId, id);
        return existingWhatsAppMessaging.map(userUtils::mapToUserWhatsAppMessagingRecord)
                .orElseThrow(() -> new RecordNotFoundException("WhatsApp Messaging Record not found for ID " + id));
    }

    /**
     * Delete whats app messaging.
     *
     * @param userId
     *            the user id
     * @param id
     *            the id
     * @param servletRequest
     *            the servlet request
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    public void deleteWhatsAppMessaging(String userId, String id, HttpServletRequest servletRequest)
            throws IOException {
        Optional<WhatsAppMessaging> existingWhatsAppMessaging = userUtils.getWhatsAppMessagingById(userId, id);
        if (existingWhatsAppMessaging.isEmpty()) {
            throw new RecordNotFoundException("No Record found for ID " + id + " for User ID " + userId);
        }
        existingWhatsAppMessaging.ifPresent(whatsAppMessaging -> {
            try {
                Optional<User> existingUser = userUtils.getUserByWhatsAppMessagingId(id);
                if (existingUser.isEmpty()) {
                    throw new RecordNotFoundException("No Record found for ID " + id);
                }
                Optional<User> user = existingUser.map(u -> {
                    try {
                        User oldUser = getNewObject(u);
                        /*
                         * Resetting WhatsApp Messaging to empty
                         */
                        WhatsAppMessaging whatsApp = new WhatsAppMessaging("", "", new ArrayList<>());
                        u.setWhatsAppMessaging(whatsApp);
                        IndexRequest<User> indexRequest = IndexRequest.of(
                                request -> request.index(utils.getFinalIndex(IndexEnum.user_index.name())).document(u));
                        client.update(
                                UpdateRequest.of(updateRequest -> updateRequest.doc(indexRequest).id(u.getId())
                                        .index(utils.getFinalIndex(IndexEnum.user_index.name())).upsert(u)),
                                User.class);
                        auditUtils.addGeneralAudit(new GeneralAudit.Builder().setId(Utils.generateUUID())
                                .setOwnerObjectId(existingWhatsAppMessaging.get().getId())
                                .setObjectClass(WhatsAppMessaging.class)
                                .setOldObject(objectMapper.writeValueAsString(oldUser))
                                .setNewObject(objectMapper.writeValueAsString(existingWhatsAppMessaging.get()))
                                .setAction(GeneralAudit.Audit.DELETE.toString())
                                .setComments("Deleting WhatsApp Messaging from UI")
                                .setLoggedInUserId(authUtils.getLoggedInUserId())
                                .setClientIP(authUtils.getClientIP(servletRequest)).build());
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }

                    return u;
                });
                log.debug("Deleted WhatsApp Messaging for User\n{}", user.get());
            } catch (RecordNotFoundException e) {
                throw new RecordNotFoundException(e.getMessage());
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        });
    }

    @Override
    public String getTrimmedValue(String value) {
        if (StringUtils.isNotBlank(value)) {
            return StringUtils.trim(value);
        }
        return value;
    }

    @Override
    public User getNewObject(User data) {
        try {
            return objectMapper.readValue(objectMapper.writeValueAsString(data), User.class);
        } catch (JsonProcessingException ex) {
            log.error(ex.getMessage(), ex);
        }
        return null;
    }
}
