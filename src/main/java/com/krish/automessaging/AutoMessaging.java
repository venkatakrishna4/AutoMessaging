package com.krish.automessaging;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.krish.automessaging.service.ElasticMappingService;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableWebMvc
@EnableEncryptableProperties
@Slf4j
public class AutoMessaging {

    private final ElasticMappingService elasticMappingService;

    @Autowired
    public AutoMessaging(final ElasticMappingService elasticMappingService) {
        this.elasticMappingService = elasticMappingService;
    }

    @PostConstruct
    public void initialize() {
        try {
            elasticMappingService.initializeIndexes();
        } catch (URISyntaxException | IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(AutoMessaging.class, args);
    }

}
