package com.krish.automessaging.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.krish.automessaging.datamodel.pojo.User;
import com.krish.automessaging.service.JsonParserService;
import com.krish.automessaging.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class UserAuthenticationServiceImpl implements UserDetailsService {
    private final ElasticsearchClient client;
    private final UserUtils userUtils;
    private final JsonParserService jsonParserService;

    @Autowired
    public UserAuthenticationServiceImpl(final ElasticsearchClient client, final UserUtils userUtils,
            JsonParserService jsonParserService) {
        this.client = client;
        this.userUtils = userUtils;
        this.jsonParserService = jsonParserService;
    }

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
                !user.isDisabled(), true, true, true, authorities);
    }
}
