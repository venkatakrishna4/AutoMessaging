package com.krish.automessaging.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
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

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final ElasticsearchClient client;
    private final PasswordEncoder passwordEncoder;
    private final PhoneNumberUtil phoneNumberUtil;
    private final UserUtils userUtils;
    private final Utils utils;
    private final AuditUtils auditUtils;
    private final AuthUtils authUtils;
    private final ObjectMapper objectMapper;

    @Autowired
    public UserServiceImpl(final ElasticsearchClient client, final PasswordEncoder passwordEncoder,
            final PhoneNumberUtil phoneNumberUtil, UserUtils userUtils, Utils utils, AuditUtils auditUtils,
            AuthUtils authUtils, ObjectMapper objectMapper) {
        this.client = client;
        this.passwordEncoder = passwordEncoder;
        this.phoneNumberUtil = phoneNumberUtil;
        this.userUtils = userUtils;
        this.utils = utils;
        this.auditUtils = auditUtils;
        this.authUtils = authUtils;
        this.objectMapper = objectMapper;
    }

    @Override
    public String createUser(UserRequestRecord userRequestRecord, HttpServletRequest request) throws IOException {
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
        utils.setAuditProperties(newUser);
        log.debug("Inserting user {} into Elasticsearch", newUser);
        client.index(IndexRequest.of(insertUserRequest -> insertUserRequest
                .index(utils.getFinalIndex(IndexEnum.user_index.toString())).id(newUser.getId()).document(newUser)));

        auditUtils.addGeneralAudit(GeneralAudit.builder().id(Utils.generateUUID()).ownerObjectId(newUser.getId())
                .objectClass(User.class).oldObject(null).newObject(objectMapper.writeValueAsString(newUser))
                .action(GeneralAudit.Audit.ADD.toString()).comments("New User is getting subscribed from UI")
                .loggedInUserId(authUtils.getLoggedInUserId()).clientIP(authUtils.getClientIP(request)).build());
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
