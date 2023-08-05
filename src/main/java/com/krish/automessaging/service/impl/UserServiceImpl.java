package com.krish.automessaging.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.krish.automessaging.datamodel.pojo.User;
import com.krish.automessaging.datamodel.pojo.audit.GeneralAudit;
import com.krish.automessaging.datamodel.record.EmailOptionsRecord;
import com.krish.automessaging.datamodel.record.PaginatedResponseRecord;
import com.krish.automessaging.datamodel.record.UserRequestRecord;
import com.krish.automessaging.datamodel.record.UserResponseRecord;
import com.krish.automessaging.enums.IndexEnum;
import com.krish.automessaging.exception.custom.EmailExistsException;
import com.krish.automessaging.exception.custom.RecordNotFoundException;
import com.krish.automessaging.service.EmailService;
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
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;

/**
 * The Class UserServiceImpl.
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

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
    private final EmailService emailService;

    @Value("${server.host}")
    private String host;

    @Value("${server.port}")
    private String port;

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
            Utils utils, AuditUtils auditUtils, AuthUtils authUtils, ObjectMapper objectMapper,
            EmailService emailService) {
        this.client = client;
        this.passwordEncoder = passwordEncoder;
        this.userUtils = userUtils;
        this.utils = utils;
        this.auditUtils = auditUtils;
        this.authUtils = authUtils;
        this.objectMapper = objectMapper;
        this.emailService = emailService;
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
        User newUser = new User.Builder().setId(Utils.generateUUID()).setName(getTrimmedValue(userRequestRecord.name()))
                .setUsername(getTrimmedValue(userRequestRecord.username()))
                .setPassword(passwordEncoder.encode(userRequestRecord.password()))
                .setEmail(getTrimmedValue(StringUtils.lowerCase(userRequestRecord.email())))
                .setPasswordResetKey(Utils.generateUUID()).setPhone(getTrimmedValue(userRequestRecord.phone())).build();
        utils.setAuditProperties(newUser);
        log.debug("Inserting user {} into Elasticsearch", newUser);
        client.index(IndexRequest.of(insertUserRequest -> insertUserRequest
                .index(utils.getFinalIndex(IndexEnum.user_index.toString())).id(newUser.getId()).document(newUser)));

        /*
         * Send User for activation of the User
         */
        sendEmailConfirmation(newUser, servletRequest);

        auditUtils.addGeneralAudit(new GeneralAudit.Builder().setId(Utils.generateUUID())
                .setOwnerObjectId(newUser.getId()).setObjectClass(User.class).setOldObject(null)
                .setNewObject(objectMapper.writeValueAsString(newUser)).setAction(GeneralAudit.Audit.ADD.toString())
                .setComments("New User is getting subscribed from UI").setLoggedInUserId(authUtils.getLoggedInUserId())
                .setClientIP(authUtils.getClientIP(servletRequest)).build());
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
                .orElseThrow(() -> new RecordNotFoundException("No Record found for ID " + id));
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
            throw new IllegalArgumentException("Email Address or Username cannot be updated");
        }
        Optional<User> existingUser = userUtils.getUserByUsernameOrEmailOrID(userRequestRecord.id());
        User oldUser = existingUser.map(this::getNewObject).orElseThrow(
                () -> new RecordNotFoundException("User Record not found for ID " + userRequestRecord.id()));

        existingUser.map(user -> {
            user.setName(getTrimmedValue(userRequestRecord.name()));
            user.setPassword(passwordEncoder.encode(userRequestRecord.password()));
            user.setPhone(userRequestRecord.phone());
            return user;
        });

        IndexRequest<User> indexRequest = IndexRequest.of(request -> request
                .index(utils.getFinalIndex(IndexEnum.user_index.name())).document(existingUser.get()));
        try {
            client.update(UpdateRequest
                    .of(updateRequest -> updateRequest.index(utils.getFinalIndex(IndexEnum.user_index.name()))
                            .doc(indexRequest).id(userRequestRecord.id())),
                    User.class);
            log.debug("Updated the user\n{}", existingUser.get());
            auditUtils.addGeneralAudit(new GeneralAudit.Builder().setId(Utils.generateUUID())
                    .setOwnerObjectId(existingUser.get().getId()).setObjectClass(User.class)
                    .setOldObject(objectMapper.writeValueAsString(oldUser))
                    .setNewObject(objectMapper.writeValueAsString(existingUser.get()))
                    .setAction(GeneralAudit.Audit.UPDATE.toString()).setComments("Updating exising user from UI")
                    .setLoggedInUserId(authUtils.getLoggedInUserId()).setClientIP(authUtils.getClientIP(servletRequest))
                    .build());
        } catch (IOException e) {
            log.debug(e.getMessage(), e);
            throw new RuntimeException(e);
        }
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
        log.debug("Received request to delete the user for id {}", id);
        userUtils.getUserByUsernameOrEmailOrID(id).ifPresentOrElse(user -> {
            try {
                client.delete(
                        deleteRequest -> deleteRequest.index(utils.getFinalIndex(IndexEnum.user_index.name())).id(id));
                log.debug("Deleted the user {}", user.getEmail());
                auditUtils.addGeneralAudit(new GeneralAudit.Builder().setId(Utils.generateUUID())
                        .setOwnerObjectId(user.getId()).setObjectClass(User.class)
                        .setOldObject(objectMapper.writeValueAsString(user)).setNewObject(null)
                        .setAction(GeneralAudit.Audit.DELETE.toString()).setComments("Deleting exising user from UI")
                        .setLoggedInUserId(authUtils.getLoggedInUserId())
                        .setClientIP(authUtils.getClientIP(servletRequest)).build());
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

    private void sendEmailConfirmation(User user, HttpServletRequest servletRequest) throws IOException {

        String emailVerificationLink = null;

        if ("localhost".equals(host)) {
            emailVerificationLink = "http://" + host + ":" + port;
        } else {
            emailVerificationLink = "https://" + host + ":" + port;
        }
        emailVerificationLink += "/user/v1/verification/email/" + user.getPasswordResetKey();

        final String message = "Please click here to confirm your identity " + emailVerificationLink;
        EmailOptionsRecord emailOptionsRecord = new EmailOptionsRecord("noreply@krish.com", user.getEmail(),
                user.getName(), EmailOptionsRecord.TYPE_TEXT, "Email Verification for " + user.getName(), message, null,
                null, null);

        try {
            emailService.sendEmail(emailOptionsRecord);
        } catch (MessagingException e) {
            log.error(e.getMessage(), e);
        }
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
