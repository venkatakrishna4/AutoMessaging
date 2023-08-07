package com.krish.automessaging.schedule;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.krish.automessaging.constant.TimeConstants;
import com.krish.automessaging.datamodel.pojo.User;
import com.krish.automessaging.datamodel.pojo.WhatsAppMessaging.Message;
import com.krish.automessaging.datamodel.record.WhatsAppOptionsRecord;
import com.krish.automessaging.enums.IndexEnum;
import com.krish.automessaging.service.WhatsAppService;
import com.krish.automessaging.utils.Utils;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;

@Component
public class WhatsAppManualJobSchedule implements Job {

    private static final Logger log = LoggerFactory.getLogger(WhatsAppManualJobSchedule.class);

    private final ElasticsearchClient client;
    private final Utils utils;
    private final WhatsAppService whatsAppService;

    @Autowired
    public WhatsAppManualJobSchedule(ElasticsearchClient client, Utils utils, WhatsAppService whatsAppService) {
        this.client = client;
        this.utils = utils;
        this.whatsAppService = whatsAppService;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Query query = QueryBuilders.bool()
                .must(QueryBuilders.range().field("whatsAppMessaging.messages.timestamp")
                        .gte(JsonData.of(System.currentTimeMillis() - TimeConstants.TIME_IN_1_MINUTES))
                        .lt(JsonData.of(System.currentTimeMillis() + TimeConstants.TIME_IN_1_MINUTES)).build()
                        ._toQuery())
                .build()._toQuery();

        try {
            SearchResponse<User> searchResponse = client.search(
                    searchRequest -> searchRequest.index(utils.getFinalIndex(IndexEnum.user_index.name())).query(query),
                    User.class);

            List<User> users = searchResponse.hits().hits().stream().map(Hit::source).toList();
            log.debug("Found {} users between 1 minute to send manual WhatsApp messages", users.size());

            for (User user : users) {
                if (user.isActivated() && !user.isDisabled()) {
                    for (Message message : user.getWhatsAppMessaging().getMessages()) {
                        WhatsAppOptionsRecord whatsAppOptionsRecord = new WhatsAppOptionsRecord(message.getType(),
                                message.getMessage(), message.getTo());
                        try {
                            whatsAppService.sendMessage(whatsAppOptionsRecord);
                        } catch (URISyntaxException ex) {
                            log.debug(ex.getMessage(), ex);
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
