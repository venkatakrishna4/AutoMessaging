package com.krish.automessaging.service.impl;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.krish.automessaging.datamodel.pojo.User;
import com.krish.automessaging.datamodel.pojo.audit.GeneralAudit;
import com.krish.automessaging.enums.IndexEnum;
import com.krish.automessaging.exception.custom.RecordNotFoundException;
import com.krish.automessaging.service.UserVerificationService;
import com.krish.automessaging.utils.AuditUtils;
import com.krish.automessaging.utils.AuthUtils;
import com.krish.automessaging.utils.UserUtils;
import com.krish.automessaging.utils.Utils;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserVerificationServiceImpl implements UserVerificationService {

    private static final Logger log = LoggerFactory.getLogger(UserVerificationServiceImpl.class);

    private final ElasticsearchClient client;
    private final UserUtils userUtils;
    private final AuthUtils authUtils;
    private final AuditUtils auditUtils;
    private final ObjectMapper objectMapper;
    private final Utils utils;

    @Autowired
    public UserVerificationServiceImpl(ElasticsearchClient client, UserUtils userUtils, AuthUtils authUtils,
            AuditUtils auditUtils, ObjectMapper objectMapper, Utils utils) {
        this.client = client;
        this.userUtils = userUtils;
        this.auditUtils = auditUtils;
        this.authUtils = authUtils;
        this.objectMapper = objectMapper;
        this.utils = utils;
    }

    @Override
    public String verifyByEmail(String key, HttpServletRequest servletRequest) throws IOException {
        log.debug("Received request for user verification by Email using key {}", key);
        // get the user by key
        Optional<User> existingUser = userUtils.getUserByPasswordResetKey(key);
        if (existingUser.isEmpty()) {
            throw new RecordNotFoundException("No Record found for key " + key);
        }
        // update the user activation status
        existingUser.map(user -> {
            user.setActivated(true);
            user.setPasswordResetKey("");

            IndexRequest<User> indexRequest = IndexRequest
                    .of(request -> request.index(utils.getFinalIndex(IndexEnum.user_index.name())).document(user));
            try {
                client.update(updateRequest -> updateRequest.index(utils.getFinalIndex(IndexEnum.user_index.name()))
                        .id(user.getId()).doc(indexRequest).upsert(user), User.class);
                auditUtils.addGeneralAudit(
                        new GeneralAudit.Builder().setId(Utils.generateUUID()).setOwnerObjectId(user.getId())
                                .setObjectClass(User.class).setOldObject(objectMapper.writeValueAsString(user))
                                .setNewObject(null).setAction(GeneralAudit.Audit.DELETE.toString())
                                .setComments("Updating exising user from Email Verification")
                                .setLoggedInUserId(authUtils.getLoggedInUserId())
                                .setClientIP(authUtils.getClientIP(servletRequest)).build());
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
            return user;
        });

        return "User Verified successfully";
    }

}
