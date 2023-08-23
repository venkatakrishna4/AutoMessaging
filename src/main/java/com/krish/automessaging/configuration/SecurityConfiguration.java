package com.krish.automessaging.configuration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * The type Security configuration.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SecurityConfiguration {

    /** The username. */
    @Value("${login.username}")
    private String username;

    /** The password. */
    @Value("${login.password}")
    private String password;

    /** The user details service. */
    private final UserDetailsService userDetailsService;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final OidcUserService oidcUserService;

    private List<String> ignoreNonce = new ArrayList<>();

    /**
     * Instantiates a new security configuration.
     *
     * @param userDetailsService
     *            the user details service
     */
    @Autowired
    public SecurityConfiguration(final UserDetailsService userDetailsService,
            final ClientRegistrationRepository clientRegistrationRepository, final OidcUserService oidcUserService) {
        this.userDetailsService = userDetailsService;
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.oidcUserService = oidcUserService;
    }

    /**
     * Filter chain security filter chain.
     *
     * @param httpSecurity
     *            the http security
     *
     * @return the security filter chain
     *
     * @throws Exception
     *             the exception
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.cors(AbstractHttpConfigurer::disable).csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request.requestMatchers("/login").permitAll()
                        .requestMatchers("/user/v1/verification/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/user/public").permitAll().anyRequest()
                        .authenticated())
                .authenticationProvider(this.inMemoryAuthenticationProvider())
                .authenticationProvider(this.userDetailsAuthenticationProvider()).httpBasic(Customizer.withDefaults())
                .formLogin(Customizer.withDefaults()).logout(Customizer.withDefaults())
                .oauth2Login(oauth -> oauth.clientRegistrationRepository(clientRegistrationRepository)
                        .defaultSuccessUrl("/api/v1/user/krishna")
                        .userInfoEndpoint(user -> user.oidcUserService(oidcUserService)).authorizationEndpoint(
                                authz -> authz.authorizationRequestResolver(this.oidcAuthorizationRequestResolver())))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .invalidSessionUrl("/login"))
                .build();
    }

    /**
     * Dao's authentication provider dao authentication provider.
     *
     * @return the dao authentication provider
     */
    @Bean
    @Order(1)
    public DaoAuthenticationProvider inMemoryAuthenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsManager());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    /**
     * User details authentication provider.
     *
     * @return the dao authentication provider
     */
    @Bean
    @Order(2)
    public DaoAuthenticationProvider userDetailsAuthenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    /**
     * User details manager user details manager.
     *
     * @return the user details manager
     */
    @Bean
    public UserDetailsManager userDetailsManager() {
        return new InMemoryUserDetailsManager(
                User.builder().username(username).password(password).roles("ADMIN").build());
    }

    /**
     * Password encoder password encoder.
     *
     * @return the password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Oidc authorization request resolver.
     *
     * @return the authorization request resolver
     */
    @Bean
    AuthorizationRequestResolver oidcAuthorizationRequestResolver() {
        return new AuthorizationRequestResolver(clientRegistrationRepository, "/oauth2/authorization", ignoreNonce);
    }

    /**
     * Authorized client manager.
     *
     * @param clientRegistrationRepository
     *            the client registration repository
     * @param authorizedClientRepository
     *            the authorized client repository
     *
     * @return the o auth 2 authorized client manager
     */
    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientRepository authorizedClientRepository) {

        OAuth2AuthorizedClientProvider authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
                .authorizationCode().refreshToken().build();

        DefaultOAuth2AuthorizedClientManager authorizedClientManager = new DefaultOAuth2AuthorizedClientManager(
                clientRegistrationRepository, authorizedClientRepository);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }

}
