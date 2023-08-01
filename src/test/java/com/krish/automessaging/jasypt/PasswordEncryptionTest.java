package com.krish.automessaging.jasypt;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Profile("dev")
@Slf4j
public class PasswordEncryptionTest {

    @Value("${jasypt.password}")
    private String jasyptEncryptorPassword;

    @Value("${elastic.username}")
    private String elasticUsername;

    @Value("${elastic.password}")
    private String elasticPassword;

    @Test
    public void jasyptStringEncryptor() {
        PooledPBEStringEncryptor pooledPBEStringEncryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig simpleStringPBEConfig = new SimpleStringPBEConfig();
        simpleStringPBEConfig.setPassword(jasyptEncryptorPassword);
        simpleStringPBEConfig.setAlgorithm("PBEWithMD5AndDES");
        simpleStringPBEConfig.setKeyObtentionIterations("1000");
        simpleStringPBEConfig.setPoolSize("1");
        simpleStringPBEConfig.setProviderName("SunJCE");
        simpleStringPBEConfig.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        simpleStringPBEConfig.setStringOutputType("base64");
        pooledPBEStringEncryptor.setConfig(simpleStringPBEConfig);

        log.debug("Encrypted value: {}", pooledPBEStringEncryptor.encrypt(elasticUsername));
        log.debug("Encrypted value: {}", pooledPBEStringEncryptor.encrypt(elasticPassword));
        Assertions.assertTrue(true);
    }
}
