package com.krish.automessaging.utils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.krish.automessaging.datamodel.pojo.User;
import com.krish.automessaging.datamodel.pojo.WhatsAppMessaging;
import com.krish.automessaging.datamodel.record.UserResponseRecord;
import com.krish.automessaging.datamodel.record.WhatsAppMessagingRecord;
import com.krish.automessaging.enums.IndexEnum;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SearchType;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;

/**
 * The Class UserUtils.
 */
@Component

public class UserUtils {

    private static final Logger log = LoggerFactory.getLogger(UserUtils.class);

    /** The client. */
    private final ElasticsearchClient client;

    /** The utils. */
    private final Utils utils;

    /** The object mapper. */
    private final ObjectMapper objectMapper;

    /** The phone number util. */
    private final PhoneNumberUtil phoneNumberUtil;

    /**
     * Instantiates a new user utils.
     *
     * @param client
     *            the client
     * @param utils
     *            the utils
     * @param objectMapper
     *            the object mapper
     * @param phoneNumberUtil
     *            the phone number util
     */
    @Autowired
    public UserUtils(final ElasticsearchClient client, final Utils utils, final ObjectMapper objectMapper,
            final PhoneNumberUtil phoneNumberUtil) {
        this.client = client;
        this.utils = utils;
        this.objectMapper = objectMapper;
        this.phoneNumberUtil = phoneNumberUtil;
    }

    /**
     * Gets the user by username or email or ID.
     *
     * @param value
     *            the value
     *
     * @return the user by username or email or ID
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public Optional<User> getUserByUsernameOrEmailOrID(String value) throws IOException {
        SearchResponse<User> searchResponse = client.search(SearchRequest.of(searchRequest -> searchRequest
                .index(utils.getFinalIndex(IndexEnum.user_index.toString()))
                .query(QueryBuilders.bool()
                        .should(QueryBuilders.term().field("email.keyword").value(StringUtils.lowerCase(value)).build()
                                ._toQuery())
                        .should(QueryBuilders.term().field("username.keyword").value(StringUtils.lowerCase(value))
                                .build()._toQuery())
                        .should(QueryBuilders.term().field("id").value(value).build()._toQuery()).build()._toQuery())
                .searchType(SearchType.DfsQueryThenFetch)), User.class);
        List<User> users = searchResponse.hits().hits().stream().map(Hit::source).toList();
        if (ObjectUtils.isNotEmpty(users)) {
            return Optional.of(users.get(0));
        }
        return Optional.empty();
    }

    /**
     * Checks if is email exists.
     *
     * @param value
     *            the value
     *
     * @return true, if is email exists
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public boolean isEmailExists(String value) throws IOException {
        return getUserByUsernameOrEmailOrID(value).isPresent();
    }

    /**
     * Checks if is email and username associated to ID.
     *
     * @param id
     *            the id
     * @param email
     *            the email
     * @param username
     *            the username
     *
     * @return true, if is email and username associated to ID
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public boolean isEmailAndUsernameAssociatedToID(String id, String email, String username) throws IOException {
        Optional<User> existingUser = getUserByUsernameOrEmailOrID(id);
        return existingUser.map(user -> StringUtils.equalsIgnoreCase(user.getEmail(), email)
                && StringUtils.equalsIgnoreCase(user.getUsername(), username)).orElse(false);
    }

    /**
     * Checks if is valida phone number.
     *
     * @param phone
     *            the phone
     *
     * @return true, if is valida phone number
     */
    public boolean isValidaPhoneNumber(String phone) {
        try {
            Phonenumber.PhoneNumber phoneNumber = phoneNumberUtil.parse(phone,
                    Phonenumber.PhoneNumber.CountryCodeSource.UNSPECIFIED.toString());
            return phoneNumberUtil.isValidNumber(phoneNumber);
        } catch (NumberParseException e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * Gets the whats app messaging by id.
     *
     * @param userId
     *            the user id
     * @param whatsAppId
     *            the whats app id
     *
     * @return the whats app messaging by id
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public Optional<WhatsAppMessaging> getWhatsAppMessagingById(String userId, String whatsAppId) throws IOException {
        SearchResponse<User> searchResponse = client.search(
                SearchRequest
                        .of(searchRequest -> searchRequest
                                .index(utils.getFinalIndex(IndexEnum.user_index.name())).query(
                                        QueryBuilders.bool()
                                                .must(QueryBuilders.term().field("id").value(userId).build()._toQuery())
                                                .must(QueryBuilders.term().field("whatsAppMessaging.id")
                                                        .value(whatsAppId).build()._toQuery())
                                                .build()._toQuery())),
                User.class);
        List<User> users = searchResponse.hits().hits().stream().map(Hit::source).toList();
        if (ObjectUtils.isNotEmpty(users)) {
            return Optional.ofNullable(users.get(0).getWhatsAppMessaging());
        }
        return Optional.empty();
    }

    /**
     * Gets the user by whats app messaging id.
     *
     * @param id
     *            the id
     *
     * @return the user by whats app messaging id
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public Optional<User> getUserByWhatsAppMessagingId(String id) throws IOException {
        SearchResponse<User> searchResponse = client.search(
                SearchRequest.of(searchRequest -> searchRequest.index(utils.getFinalIndex(IndexEnum.user_index.name()))
                        .query(QueryBuilders.term().field("whatsAppMessaging.id").value(id).build()._toQuery())),
                User.class);
        List<User> users = searchResponse.hits().hits().stream().map(Hit::source).toList();
        if (ObjectUtils.isNotEmpty(users)) {
            return Optional.ofNullable(users.get(0));
        }
        return Optional.empty();
    }

    public Optional<User> getUserByPasswordResetKey(String key) throws IOException {
        SearchResponse<User> searchResponse = client.search(
                SearchRequest.of(searchRequest -> searchRequest.index(utils.getFinalIndex(IndexEnum.user_index.name()))
                        .query(QueryBuilders.term().field("passwordResetKey.keyword").value(key).build()._toQuery())),
                User.class);
        List<User> users = searchResponse.hits().hits().stream().map(Hit::source).toList();
        if (ObjectUtils.isNotEmpty(users)) {
            return Optional.ofNullable(users.get(0));
        }
        return Optional.empty();
    }

    /**
     * Map to user response record.
     *
     * @param user
     *            the user
     *
     * @return the user response record
     */
    public UserResponseRecord mapToUserResponseRecord(User user) {
        try {
            return objectMapper.readValue(objectMapper.writeValueAsString(user), UserResponseRecord.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Map to user whats app messaging record.
     *
     * @param whatsAppMessaging
     *            the whats app messaging
     *
     * @return the whats app messaging record
     */
    public WhatsAppMessagingRecord mapToUserWhatsAppMessagingRecord(WhatsAppMessaging whatsAppMessaging) {
        try {
            return objectMapper.readValue(objectMapper.writeValueAsString(whatsAppMessaging),
                    WhatsAppMessagingRecord.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
}
