package com.krish.automessaging.configuration;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link JasyptEncryptorConfiguration} has a bean {@link #jasyptStringEncryptor()} that is used to create an encryptor
 * to encode and decode sensitive information.
 */
@Configuration
public class JasyptEncryptorConfiguration {

    /** The jasypt encryptor password. */
    @Value("${jasypt.password}")
    private String jasyptEncryptorPassword;

    /**
     * Jasypt string encryptor string encryptor.
     *
     * @return the string encryptor
     */
    @Bean
    public StringEncryptor jasyptStringEncryptor() {
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
        return pooledPBEStringEncryptor;
    }
}
