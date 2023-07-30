package com.krish.automessaging.configuration;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author venkat
 *
 * @version Version 1
 *
 * @implNote {@link ElasticsearchConfiguration} creates {@link Bean} of {@link ElasticsearchClient} that is used for
 *           further connection to Elasticsearch
 */
@Configuration
public class ElasticsearchConfiguration {

    @Value("${elastic.host}")
    private String elasticsearchHost;

    @Value("${elastic.username}")
    private String elasticUsername;

    @Value("${elastic.password}")
    private String elasticPassword;

    /**
     * Method getRestClient() creates {@link RestClient} using {@link BasicCredentialsProvider}
     *
     * @return {@link RestClient}
     */
    @Bean
    public RestClient getRestClient() {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(elasticUsername, elasticPassword));
        RestClientBuilder restClientBuilder = RestClient.builder(HttpHost.create(elasticsearchHost))
                .setHttpClientConfigCallback(httpAsyncClientBuilder -> httpAsyncClientBuilder
                        .setDefaultCredentialsProvider(credentialsProvider));
        return restClientBuilder.build();
    }

    /**
     * Method getTransport() returns {@link ElasticsearchTransport} that will be used to create
     * {@link ElasticsearchClient}
     *
     * @return {@link ElasticsearchClient}
     */
    @Bean
    public ElasticsearchTransport getTransport() {
        return new RestClientTransport(getRestClient(), jacksonJsonpMapper());
    }

    @Bean
    public JacksonJsonpMapper jacksonJsonpMapper() {
        return new JacksonJsonpMapper();
    }

    /**
     * Method elasticsearchClient() returns {@link ElasticsearchClient} that is used for DB interactions
     *
     * @return {@link ElasticsearchClient}
     */
    @Bean
    public ElasticsearchClient elasticsearchClient() {
        return new ElasticsearchClient(getTransport());
    }
}
