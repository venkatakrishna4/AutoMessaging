package com.krish.automessaging.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.CreateRequest;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.krish.automessaging.datamodel.pojo.User;
import com.krish.automessaging.datamodel.record.PaginatedResponseRecord;
import com.krish.automessaging.datamodel.record.UserRequestRecord;
import com.krish.automessaging.datamodel.record.UserResponseRecord;
import com.krish.automessaging.exception.custom.EmailExistsException;
import com.krish.automessaging.service.UserService;
import com.krish.automessaging.utils.UserUtils;
import com.krish.automessaging.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final ElasticsearchClient client;
    private final PasswordEncoder passwordEncoder;
    private final PhoneNumberUtil phoneNumberUtil;
    private final UserUtils userUtils;
    private final Utils utils;

    @Autowired
    public UserServiceImpl(final ElasticsearchClient client, final PasswordEncoder passwordEncoder,
            final PhoneNumberUtil phoneNumberUtil, UserUtils userUtils, Utils utils) {
        this.client = client;
        this.passwordEncoder = passwordEncoder;
        this.phoneNumberUtil = phoneNumberUtil;
        this.userUtils = userUtils;
        this.utils = utils;
    }

    private static final String INDEX_NAME = "user_index";

    @Override
    public String createUser(UserRequestRecord userRequestRecord) throws IOException {
        log.debug("Received Request to create user\n{}", userRequestRecord);
        /*
         * Validate Phone Number
         */
        try {
            Phonenumber.PhoneNumber phoneNumber = phoneNumberUtil.parse(userRequestRecord.phone(),
                    Phonenumber.PhoneNumber.CountryCodeSource.UNSPECIFIED.toString());
            if (!phoneNumberUtil.isValidNumber(phoneNumber)) {
                throw new IllegalArgumentException("Enter valid phone number");
            }
        } catch (NumberParseException e) {
            log.error(e.getMessage(), e);
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
        log.debug("Inserting user {} into Elasticsearch", newUser);
        client.create(CreateRequest.of(insertUserRequest -> insertUserRequest.index(utils.getFinalIndex(INDEX_NAME))
                .id(newUser.getId()).document(newUser)));

        return newUser.getId();
    }

    @Override
    public UserResponseRecord getUser(String id) {
        return null;
    }

    @Override
    public PaginatedResponseRecord<List<UserResponseRecord>> getAllUsers() {
        return null;
    }

    @Override
    public String updateUser(UserRequestRecord userRequestRecord) {
        return null;
    }

    @Override
    public void deleteUser(String id) {

    }

    @Override
    public String getTrimmedValue(String value) {
        if (StringUtils.isNotBlank(value)) {
            return StringUtils.trim(value);
        }
        return value;
    }
}
