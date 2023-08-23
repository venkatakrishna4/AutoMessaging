package com.krish.automessaging.configuration;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;

@Configuration
public class OAuth2Configuration {

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(this.azureClientRegistration());
    }

    private ClientRegistration azureClientRegistration() {
        return ClientRegistration.withRegistrationId("azure").clientId("bb97b431-920f-41e6-b1cc-66e6a33c8fd6")
                .clientSecret("~Aj8Q~QMUqAkvcjFQ06yYNktpP-5qcD.qJLKPbXz")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .scope(List.of("openid", "profile", "email"))
                .authorizationUri(
                        "https://login.microsoftonline.com/281de859-c72a-4b58-8ef3-430d00dd5f43/oauth2/v2.0/authorize")
                .tokenUri("https://login.microsoftonline.com/281de859-c72a-4b58-8ef3-430d00dd5f43/oauth2/v2.0/token")
                .userInfoUri("https://graph.microsoft.com/oidc/userinfo").userNameAttributeName(IdTokenClaimNames.SUB)
                .jwkSetUri("https://login.microsoftonline.com/281de859-c72a-4b58-8ef3-430d00dd5f43/discovery/v2.0/keys")
                .clientName("Azure").build();
    }
}
