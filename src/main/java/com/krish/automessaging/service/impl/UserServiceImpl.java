package com.krish.automessaging.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.krish.automessaging.datamodel.pojo.User;
import com.krish.automessaging.datamodel.pojo.audit.GeneralAudit;
import com.krish.automessaging.datamodel.record.PaginatedResponseRecord;
import com.krish.automessaging.datamodel.record.UserRequestRecord;
import com.krish.automessaging.datamodel.record.UserResponseRecord;
import com.krish.automessaging.enums.IndexEnum;
import com.krish.automessaging.exception.custom.EmailExistsException;
import com.krish.automessaging.exception.custom.RecordNotFoundException;
import com.krish.automessaging.service.UserService;
import com.krish.automessaging.utils.AuditUtils;
import com.krish.automessaging.utils.AuthUtils;
import com.krish.automessaging.utils.UserUtils;
import com.krish.automessaging.utils.Utils;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.UpdateRequest;
import co.elastic.clients.elasticsearch.core.search.Hit;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class UserServiceImpl.
 */
@Service

/** The Constant log. */
@Slf4j
public class UserServiceImpl implements UserService {

    /** The client. */
    private final ElasticsearchClient client;

    /** The password encoder. */
    private final PasswordEncoder passwordEncoder;

    /** The user utils. */
    private final UserUtils userUtils;

    /** The utils. */
    private final Utils utils;

    /** The audit utils. */
    private final AuditUtils auditUtils;

    /** The auth utils. */
    private final AuthUtils authUtils;

    /** The object mapper. */
    private final ObjectMapper objectMapper;

    /**
     * Instantiates a new user service impl.
     *
     * @param client
     *            the client
     * @param passwordEncoder
     *            the password encoder
     * @param userUtils
     *            the user utils
     * @param utils
     *            the utils
     * @param auditUtils
     *            the audit utils
     * @param authUtils
     *            the auth utils
     * @param objectMapper
     *            the object mapper
     */
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

    /**
     * Creates the user.
     *
     * @param userRequestRecord
     *            the user request record
     * @param servletRequest
     *            the servlet request
     *
     * @return the string
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
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

    /**
     * Gets the user.
     *
     * @param id
     *            the id
     *
     * @return the user
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    public UserResponseRecord getUser(String id) throws IOException {
        return userUtils.getUserByUsernameOrEmailOrID(id).map(userUtils::mapToUserResponseRecord)
                .orElseThrow(() -> new NoSuchElementException("User not found for id " + id));
    }

    /**
     * Gets the all users.
     *
     * @return the all users
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
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

    /**
     * Update user.
     *
     * @param userRequestRecord
     *            the user request record
     * @param servletRequest
     *            the servlet request
     *
     * @return the string
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
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
        User oldUser = existingUser.map(user -> {
            try {
                return objectMapper.readValue(objectMapper.writeValueAsString(existingUser.get()), User.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).orElseThrow(() -> new RecordNotFoundException("User Record not found for ID " + userRequestRecord.id()));

        existingUser.map(user -> {
            user.setName(getTrimmedValue(userRequestRecord.name()));
            user.setPassword(userRequestRecord.password());
            user.setPhone(userRequestRecord.phone());

            IndexRequest<User> indexRequest = IndexRequest
                    .of(request -> request.index(utils.getFinalIndex(IndexEnum.user_index.name())).document(user));
            try {
                client.update(UpdateRequest
                        .of(updateRequest -> updateRequest.index(utils.getFinalIndex(IndexEnum.user_index.name()))
                                .doc(indexRequest).id(userRequestRecord.id()).upsert(user)),
                        User.class);
                auditUtils.addGeneralAudit(GeneralAudit.builder().id(Utils.generateUUID()).ownerObjectId(user.getId())
                        .objectClass(User.class).oldObject(objectMapper.writeValueAsString(oldUser))
                        .newObject(objectMapper.writeValueAsString(user)).action(GeneralAudit.Audit.UPDATE.toString())
                        .comments("Updating exising user from UI").loggedInUserId(authUtils.getLoggedInUserId())
                        .clientIP(authUtils.getClientIP(servletRequest)).build());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return user;
        });
        return "User updated successfully";
    }

    /**
     * Delete user.
     *
     * @param id
     *            the id
     * @param servletRequest
     *            the servlet request
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    public void deleteUser(String id, HttpServletRequest servletRequest) throws IOException {
        userUtils.getUserByUsernameOrEmailOrID(id).ifPresentOrElse(user -> {
            try {
                client.delete(
                        deleteRequest -> deleteRequest.index(utils.getFinalIndex(IndexEnum.user_index.name())).id(id));
                auditUtils.addGeneralAudit(GeneralAudit.builder().id(Utils.generateUUID()).ownerObjectId(user.getId())
                        .objectClass(User.class).oldObject(objectMapper.writeValueAsString(user)).newObject(null)
                        .action(GeneralAudit.Audit.DELETE.toString()).comments("Deleting exising user from UI")
                        .loggedInUserId(authUtils.getLoggedInUserId()).clientIP(authUtils.getClientIP(servletRequest))
                        .build());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, () -> log.error("Cannot delete user as user does not exists for ID" + id));
    }

    /**
     * Gets the trimmed value.
     *
     * @param value
     *            the value
     *
     * @return the trimmed value
     */
    @Override
    public String getTrimmedValue(String value) {
        if (StringUtils.isNotBlank(value)) {
            return StringUtils.trim(value);
        }
        return value;
    }
}
