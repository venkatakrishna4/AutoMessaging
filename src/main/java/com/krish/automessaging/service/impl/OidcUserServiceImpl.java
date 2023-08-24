package com.krish.automessaging.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import com.krish.automessaging.datamodel.pojo.CurrentOidcUser;
import com.krish.automessaging.datamodel.pojo.User;
import com.krish.automessaging.service.JsonParserService;
import com.krish.automessaging.utils.UserUtils;

@Service
public class OidcUserServiceImpl extends OidcUserService {

    private final UserUtils userUtils;
    private final JsonParserService jsonParserService;

    @Autowired
    public OidcUserServiceImpl(final UserUtils userUtils, JsonParserService jsonParserService) {
        this.userUtils = userUtils;
        this.jsonParserService = jsonParserService;
    }

    protected CurrentOidcUser checkUser(OAuth2UserRequest userRequest, OidcIdToken idToken, OidcUser oauthUser) {

        String email = oauthUser.getClaimAsString("email");

        Optional<User> dbUser = Optional.empty();
        try {
            dbUser = userUtils.getUserByUsernameOrEmailOrID(email);
        } catch (IOException e) {

        }

        if (dbUser.isPresent()) {
            User user = dbUser.get();
            if (user.isDisabled()) {
                OAuth2Error oauth2Error = new OAuth2Error("access_denied");
                throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
            }

            user.setName(oauthUser.getName());

            user.setEmail(oauthUser.getEmail());

            List<GrantedAuthority> authorities = getRoles(user, oauthUser);

            return new CurrentOidcUser(user.getId(), authorities, idToken, "email", userRequest.getAccessToken());
        }
        return null;
    }

    private List<GrantedAuthority> getRoles(User user, OidcUser oauthUser) throws OAuth2AuthenticationException {
        List<String> privileges = new ArrayList<>();
        try {
            Map<String, List<String>> privilegesJson = jsonParserService.parsePrivilegesJson();
            user.getRoles().forEach(u -> privileges.addAll(privilegesJson.get(u)));
        } catch (IOException ignored) {
        }
        return new ArrayList<>(privileges.stream().map(SimpleGrantedAuthority::new).toList());

    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {

        OidcIdToken token = userRequest.getIdToken();

        OidcUser user = super.loadUser(userRequest);
        return checkUser(userRequest, token, user);
    }

}
