package com.krish.automessaging.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RootBeansConfiguration {

    @Bean
    public PhoneNumberUtil phoneNumberUtil() {
        return PhoneNumberUtil.getInstance();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
