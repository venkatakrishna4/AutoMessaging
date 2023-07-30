package com.krish.automessaging.utils;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SearchType;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.krish.automessaging.datamodel.pojo.User;
import com.krish.automessaging.datamodel.record.UserResponseRecord;
import com.krish.automessaging.enums.IndexEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class UserUtils {
    private final ElasticsearchClient client;
    private final Utils utils;
    private final ObjectMapper objectMapper;
    private final PhoneNumberUtil phoneNumberUtil;

    @Autowired
    public UserUtils(final ElasticsearchClient client, final Utils utils, final ObjectMapper objectMapper,
            final PhoneNumberUtil phoneNumberUtil) {
        this.client = client;
        this.utils = utils;
        this.objectMapper = objectMapper;
        this.phoneNumberUtil = phoneNumberUtil;
    }

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

    public boolean isEmailExists(String value) throws IOException {
        return getUserByUsernameOrEmailOrID(value).isPresent();
    }

    public boolean isEmailAndUsernameAssociatedToID(String id, String email, String username) throws IOException {
        Optional<User> existingUser = getUserByUsernameOrEmailOrID(id);
        return existingUser.map(user -> StringUtils.equalsIgnoreCase(user.getEmail(), email)
                && StringUtils.equalsIgnoreCase(user.getUsername(), username)).orElse(false);
    }

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

    public UserResponseRecord mapToUserResponseRecord(User user) {
        try {
            return objectMapper.readValue(objectMapper.writeValueAsString(user), UserResponseRecord.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
}
