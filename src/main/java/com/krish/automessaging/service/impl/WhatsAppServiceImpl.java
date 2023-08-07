package com.krish.automessaging.service.impl;

import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.krish.automessaging.datamodel.pojo.HeaderRequestInterceptor;
import com.krish.automessaging.datamodel.record.WhatsAppOptionsRecord;
import com.krish.automessaging.service.WhatsAppService;
import com.krish.automessaging.utils.WhatsAppTemplateUtils;

@Service
public class WhatsAppServiceImpl implements WhatsAppService {

    private static final Logger log = LoggerFactory.getLogger(WhatsAppServiceImpl.class);

    @Value("${whatsapp.phoneNumberId}")
    private String phoneNumberId;

    @Value("${whatsapp.token}")
    private String token;

    private static final String URL = "https://graph.facebook.com/v17.0/{{phoneNumberId}}/messages";

    private final RestTemplate restTemplate;
    private final WhatsAppTemplateUtils whatsAppTemplateUtils;
    private final ObjectMapper objectMapper;

    @Autowired
    public WhatsAppServiceImpl(final RestTemplate restTemplate, final WhatsAppTemplateUtils whatsAppTemplateUtils,
            final ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.whatsAppTemplateUtils = whatsAppTemplateUtils;
        this.objectMapper = objectMapper;
    }

    @Override
    public void sendMessage(WhatsAppOptionsRecord whatsAppOptionsRecord) throws URISyntaxException {
        restTemplate.getInterceptors()
                .add(new HeaderRequestInterceptor("Content-Type", MediaType.APPLICATION_JSON_VALUE));
        restTemplate.getInterceptors().add(new HeaderRequestInterceptor("Authorization", "Bearer " + token));
        try {
            String response = restTemplate.postForObject(getBaseUrl(), objectMapper.writeValueAsString(
                    whatsAppTemplateUtils.getWhatsAppMessageByType(whatsAppOptionsRecord)), String.class);
            log.debug(response);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }

    }

    private String getBaseUrl() {
        return URL.replace("{{phoneNumberId}}", phoneNumberId);
    }

}
