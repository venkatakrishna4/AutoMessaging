package com.krish.automessaging.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.krish.automessaging.datamodel.pojo.User;
import com.krish.automessaging.datamodel.pojo.audit.GeneralAudit;
import com.krish.automessaging.datamodel.record.PaginatedResponseRecord;
import com.krish.automessaging.datamodel.record.UserRequestRecord;
import com.krish.automessaging.datamodel.record.UserResponseRecord;
import com.krish.automessaging.enums.IndexEnum;
import com.krish.automessaging.exception.custom.EmailExistsException;
import com.krish.automessaging.service.UserService;
import com.krish.automessaging.utils.AuditUtils;
import com.krish.automessaging.utils.AuthUtils;
import com.krish.automessaging.utils.UserUtils;
import com.krish.automessaging.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final ElasticsearchClient client;
    private final PasswordEncoder passwordEncoder;
    private final UserUtils userUtils;
    private final Utils utils;
    private final AuditUtils auditUtils;
    private final AuthUtils authUtils;
    private final ObjectMapper objectMapper;

    @Autowired
    public UserServiceImpl(final ElasticsearchClient client, final PasswordEncoder passwordEncoder, UserUtils userUtils,
            Utils utils, AuditUtils auditUtils, AuthUtils authUtils, ObjectMapper objectMapper) {
        this.client = client;
        this.passwordEncoder = passwordEncoder;
        this.userUtils = userUtils;
        this.utils = utils;
        this.auditUtils = auditUtils;
        this.authUtils = authUtils;
        this.objectMapper = objectMapper;
    }

    @Override
    public String createUser(UserRequestRecord userRequestRecord, HttpServletRequest servletRequest)
            throws IOException {
        log.debug("Received Request to create user\n{}", userRequestRecord);
        /*
         * Validate Phone Number
         */
        if (!userUtils.isValidaPhoneNumber(userRequestRecord.phone())) {
            throw new IllegalArgumentException("Enter valid phone number");
        }
        /*
         * Email Address should be unique
         */
        if (userUtils.isEmailExists(userRequestRecord.email())) {
            throw new EmailExistsException("Email Address already in use. Please use a different one");
        }
        /*
         * Trim all the String fields
         */
        User newUser = User.builder().id(Utils.generateUUID()).name(getTrimmedValue(userRequestRecord.name()))
                .username(getTrimmedValue(userRequestRecord.username()))
                .password(passwordEncoder.encode(userRequestRecord.password()))
                .email(getTrimmedValue(StringUtils.lowerCase(userRequestRecord.email())))
                .phone(getTrimmedValue(userRequestRecord.phone())).build();
        utils.setAuditProperties(newUser);
        log.debug("Inserting user {} into Elasticsearch", newUser);
        client.index(IndexRequest.of(insertUserRequest -> insertUserRequest
                .index(utils.getFinalIndex(IndexEnum.user_index.toString())).id(newUser.getId()).document(newUser)));

        auditUtils.addGeneralAudit(GeneralAudit.builder().id(Utils.generateUUID()).ownerObjectId(newUser.getId())
                .objectClass(User.class).oldObject(null).newObject(objectMapper.writeValueAsString(newUser))
                .action(GeneralAudit.Audit.ADD.toString()).comments("New User is getting subscribed from UI")
                .loggedInUserId(authUtils.getLoggedInUserId()).clientIP(authUtils.getClientIP(servletRequest)).build());
        return "User created successfully";
    }

    @Override
    public UserResponseRecord getUser(String id) throws IOException {
        return userUtils.getUserByUsernameOrEmailOrID(id).map(userUtils::mapToUserResponseRecord)
                .orElseThrow(() -> new NoSuchElementException("User not found for id " + id));
    }

    @Override
    public PaginatedResponseRecord<List<UserResponseRecord>> getAllUsers() throws IOException {
        // TODO: 30/07/23 Implement Pagination
        SearchResponse<User> searchResponse = client.search(
                SearchRequest.of(searchRequest -> searchRequest.index(utils.getFinalIndex(IndexEnum.user_index.name()))
                        .query(QueryBuilders.matchAll().build()._toQuery())),
                User.class);
        return new PaginatedResponseRecord<>(searchResponse.hits().hits().stream().map(Hit::source)
                .map(userUtils::mapToUserResponseRecord).toList());
    }

    @Override
    public String updateUser(UserRequestRecord userRequestRecord, HttpServletRequest servletRequest)
            throws IOException {
        log.debug("Received Request to update user\n{}", userRequestRecord);
        /*
         * Validate Phone Number
         */
        if (!userUtils.isValidaPhoneNumber(userRequestRecord.phone())) {
            throw new IllegalArgumentException("Enter valid phone number");
        }
        if (!userUtils.isEmailAndUsernameAssociatedToID(userRequestRecord.id(), userRequestRecord.email(),
                userRequestRecord.username())) {
            throw new IllegalArgumentException("Email Address cannot be updated");
        }
        Optional<User> existingUser = userUtils.getUserByUsernameOrEmailOrID(userRequestRecord.id());
        existingUser.map(user -> {
            user.setName(getTrimmedValue(userRequestRecord.name()));
            user.setPassword(userRequestRecord.password());
            user.setPhone(userRequestRecord.phone());
            return user;
        }).orElseThrow(() -> new NoSuchElementException("User not found for id " + userRequestRecord.id()));
        IndexRequest<User> indexRequest = IndexRequest.of(request -> request
                .index(utils.getFinalIndex(IndexEnum.user_index.name())).document(existingUser.get()));
        client.update(UpdateRequest.of(updateRequest -> updateRequest
                .index(utils.getFinalIndex(IndexEnum.user_index.name())).id(userRequestRecord.id()).doc(indexRequest)),
                User.class);

        auditUtils.addGeneralAudit(GeneralAudit.builder().id(Utils.generateUUID())
                .ownerObjectId(existingUser.get().getId()).objectClass(User.class).oldObject(null)
                .newObject(objectMapper.writeValueAsString(existingUser.get()))
                .action(GeneralAudit.Audit.ADD.toString()).comments("New User is getting subscribed from UI")
                .loggedInUserId(authUtils.getLoggedInUserId()).clientIP(authUtils.getClientIP(servletRequest)).build());
        return "User updated successfully";
    }

    @Override
    public void deleteUser(String id, HttpServletRequest servletRequest) throws IOException {
        client.delete(DeleteRequest.of(deleteRequest -> deleteRequest.id(id)));
    }

    @Override
    public String getTrimmedValue(String value) {
        if (StringUtils.isNotBlank(value)) {
            return StringUtils.trim(value);
        }
        return value;
    }
}
