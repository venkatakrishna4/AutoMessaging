package com.krish.automessaging.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.krish.automessaging.datamodel.pojo.User;
import com.krish.automessaging.service.JsonParserService;
import com.krish.automessaging.utils.UserUtils;

/**
 * The Class UserAuthenticationServiceImpl.
 */
@Service
public class UserAuthenticationServiceImpl implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserAuthenticationServiceImpl.class);

    /** The user utils. */
    private final UserUtils userUtils;

    /** The json parser service. */
    private final JsonParserService jsonParserService;

    /**
     * Instantiates a new user authentication service impl.
     *
     * @param client
     *            the client
     * @param userUtils
     *            the user utils
     * @param jsonParserService
     *            the json parser service
     */
    @Autowired
    public UserAuthenticationServiceImpl(final UserUtils userUtils, JsonParserService jsonParserService) {
        this.userUtils = userUtils;
        this.jsonParserService = jsonParserService;
    }

    /**
     * Load user by username.
     *
     * @param username
     *            the username
     *
     * @return the user details
     *
     * @throws UsernameNotFoundException
     *             the username not found exception
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (StringUtils.isBlank(username)) {
            throw new UsernameNotFoundException("User not found " + username);
        }
        try {
            Optional<User> existingUser = userUtils.getUserByUsernameOrEmailOrID(username);
            if (existingUser.isPresent()) {
                return existingUser.map(this::createSpringUser)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found " + username));
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        throw new UsernameNotFoundException("User not found " + username);
    }

    /**
     * Creates the spring user.
     *
     * @param user
     *            the user
     *
     * @return the org.springframework.security.core.userdetails. user
     */
    private org.springframework.security.core.userdetails.User createSpringUser(User user) {
        List<String> privileges = new ArrayList<>();
        try {
            Map<String, List<String>> privilegesJson = jsonParserService.parsePrivilegesJson();
            user.getRoles().forEach(u -> privileges.addAll(privilegesJson.get(u)));
        } catch (IOException ignored) {

        }
        List<GrantedAuthority> authorities = new ArrayList<>(
                privileges.stream().map(SimpleGrantedAuthority::new).toList());
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                !user.isDisabled(), true, true, user.isActivated(), authorities);
    }
}
