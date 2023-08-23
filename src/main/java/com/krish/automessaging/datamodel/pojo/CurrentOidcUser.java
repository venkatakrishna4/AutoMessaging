package com.krish.automessaging.datamodel.pojo;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

public class CurrentOidcUser extends DefaultOidcUser {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;
    private String id;
    private String accessToken;

    public CurrentOidcUser(String id, Collection<? extends GrantedAuthority> authorities, OidcIdToken idToken,
            String nameAttributeKey, OAuth2AccessToken oAuth2AccessToken) {
        super(authorities, idToken, nameAttributeKey);
        this.id = id.toLowerCase();
        this.accessToken = oAuth2AccessToken.getTokenValue();
    }

    @Override
    public String toString() {
        return "CurrentOidcUser [id=" + id + ", other=" + super.toString() + "]";
    }

    @Override
    public String getName() {
        return super.getName().toLowerCase();
    }

    public String getId() {
        return id;
    }

    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return super.getAuthorities();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return super.getAttributes();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
