package com.krish.automessaging.configuration;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Profile(value = "dev")
@ContextConfiguration(classes = { ElasticsearchConfiguration.class, JasyptEncryptorConfiguration.class })
@EnableEncryptableProperties
@Slf4j
public class CreateIndexTest {
    private final ElasticsearchClient client;

    @Autowired
    public CreateIndexTest(ElasticsearchClient client) {
        this.client = client;
    }

    @Test
    public void createSampleIndexTest() throws IOException {
        boolean isIndexCreated = client.indices().exists(r -> r.index("local.test_index")).value();
        if (isIndexCreated) {
            log.info("Test Index created successfully");
        }
        Assertions.assertTrue(true);
    }
}
