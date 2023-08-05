package com.krish.automessaging;

import java.io.IOException;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.krish.automessaging.service.ElasticMappingService;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

import jakarta.annotation.PostConstruct;

// TODO: Auto-generated Javadoc
/**
 * The Class AutoMessaging.
 */
@SpringBootApplication
@EnableWebMvc
@EnableEncryptableProperties

public class AutoMessaging {

    private static final Logger log = LoggerFactory.getLogger(AutoMessaging.class);

    /** The elastic mapping service. */
    private final ElasticMappingService elasticMappingService;

    /**
     * Instantiates a new auto messaging.
     *
     * @param elasticMappingService
     *            the elastic mapping service
     */
    @Autowired
    public AutoMessaging(final ElasticMappingService elasticMappingService) {
        this.elasticMappingService = elasticMappingService;
    }

    /**
     * Initialize.
     */
    @PostConstruct
    public void initialize() {
        try {
            elasticMappingService.initializeIndexes();
        } catch (URISyntaxException | IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * The main method.
     *
     * @param args
     *            the arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(AutoMessaging.class, args);
    }

}
