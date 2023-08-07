package com.krish.automessaging.schedule;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.krish.automessaging.datamodel.pojo.User;
import com.krish.automessaging.datamodel.pojo.WhatsAppMessaging;
import com.krish.automessaging.datamodel.pojo.WhatsAppMessaging.Frequency;
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

@Component
public class WhatsAppAutoJobSchedule implements Job {

    private static final Logger log = LoggerFactory.getLogger(WhatsAppAutoJobSchedule.class);

    private final ElasticsearchClient client;
    private final Utils utils;
    private final WhatsAppService whatsAppService;

    @Autowired
    public WhatsAppAutoJobSchedule(ElasticsearchClient client, Utils utils, WhatsAppService whatsAppService) {
        this.client = client;
        this.utils = utils;
        this.whatsAppService = whatsAppService;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Query query = null;
        Trigger trigger = context.getTrigger();

        long repeatInterval = ((SimpleTrigger) trigger).getRepeatInterval();
        query = getQueryBasedOnRepeatInterval(repeatInterval);

        final Query effectivelyFinalQuery = query;

        try {
            SearchResponse<User> searchResponse = client.search(searchRequest -> searchRequest
                    .index(utils.getFinalIndex(IndexEnum.user_index.toString())).query(effectivelyFinalQuery),
                    User.class);

            List<User> users = searchResponse.hits().hits().stream().map(Hit::source).toList();
            log.debug("Found {} users between for automatic 1 minute to send WhatsApp messages", users.size());

            for (User user : users) {
                if (user.isActivated() && !user.isDisabled()) {
                    for (Message message : user.getWhatsAppMessaging().getMessages()) {
                        if (Frequency.MINUTE.toString().equals(message.getFrequency())) {
                            try {
                                WhatsAppOptionsRecord whatsAppOptionsRecord = new WhatsAppOptionsRecord(
                                        message.getType(), message.getMessage(), message.getTo());
                                whatsAppService.sendMessage(whatsAppOptionsRecord);
                            } catch (URISyntaxException ex) {
                                log.error(ex.getMessage(), ex);
                            }
                        }
                    }
                }
            }

        } catch (IOException e) {
            log.debug(e.getMessage(), e);
        }

    }

    /**
     * Gets the query based on repeat interval.
     *
     * @param interval
     *            the interval
     *
     * @return the query based on repeat interval
     */
    private Query getQueryBasedOnRepeatInterval(long interval) {
        // TODO: Enhance it by using switch when using Java 21
        Query filter = QueryBuilders.bool()
                .should(QueryBuilders.term().field("whatsAppMessaging.messages.frequencyType")
                        .value(WhatsAppMessaging.FrequencyType.AUTO.toString()).build()._toQuery())
                .should(QueryBuilders.term().field("whatsAppMessaging.messages.frequencyType.keyword")
                        .value(WhatsAppMessaging.FrequencyType.AUTO.toString()).build()._toQuery())
                .build()._toQuery();
        final String frequencyField = "whatsAppMessaging.messages.frequency";
        final String frequencyKeywordField = "whatsAppMessaging.messages.frequency.keyword";
        if (Long.compare(WhatsAppMessaging.MINUTE_INTERVAL, interval) == 0) {
            return QueryBuilders.bool().must(filter)
                    .must(QueryBuilders.bool()
                            .should(QueryBuilders.term().field(frequencyField)
                                    .value(WhatsAppMessaging.Frequency.MINUTE.toString()).build()._toQuery())
                            .should(QueryBuilders.term().field(frequencyKeywordField)
                                    .value(WhatsAppMessaging.Frequency.MINUTE.toString()).build()._toQuery())
                            .build()._toQuery())
                    .build()._toQuery();
        } else if (Long.compare(WhatsAppMessaging.HOURLY_INTERVAL, interval) == 0) {
            return QueryBuilders.bool().must(filter)
                    .must(QueryBuilders.bool()
                            .should(QueryBuilders.term().field(frequencyField)
                                    .value(WhatsAppMessaging.Frequency.HOURLY.toString()).build()._toQuery())
                            .should(QueryBuilders.term().field(frequencyKeywordField)
                                    .value(WhatsAppMessaging.Frequency.HOURLY.toString()).build()._toQuery())
                            .build()._toQuery())
                    .build()._toQuery();
        } else if (Long.compare(WhatsAppMessaging.DAILY_INTERVAL, interval) == 0) {
            return QueryBuilders.bool().must(filter)
                    .must(QueryBuilders.bool()
                            .should(QueryBuilders.term().field(frequencyField)
                                    .value(WhatsAppMessaging.Frequency.DAILY.toString()).build()._toQuery())
                            .should(QueryBuilders.term().field(frequencyKeywordField)
                                    .value(WhatsAppMessaging.Frequency.DAILY.toString()).build()._toQuery())
                            .build()._toQuery())
                    .build()._toQuery();
        } else if (Long.compare(WhatsAppMessaging.WEEKLY_INTERVAL, interval) == 0) {
            return QueryBuilders.bool().must(filter)
                    .must(QueryBuilders.bool()
                            .should(QueryBuilders.term().field(frequencyField)
                                    .value(WhatsAppMessaging.Frequency.WEEKLY.toString()).build()._toQuery())
                            .should(QueryBuilders.term().field(frequencyKeywordField)
                                    .value(WhatsAppMessaging.Frequency.WEEKLY.toString()).build()._toQuery())
                            .build()._toQuery())
                    .build()._toQuery();
        } else if (Long.compare(WhatsAppMessaging.MONTHLY_INTERVAL, interval) == 0) {
            return QueryBuilders.bool().must(filter)
                    .must(QueryBuilders.bool()
                            .should(QueryBuilders.term().field(frequencyField)
                                    .value(WhatsAppMessaging.Frequency.MONTHLY.toString()).build()._toQuery())
                            .should(QueryBuilders.term().field(frequencyKeywordField)
                                    .value(WhatsAppMessaging.Frequency.MONTHLY.toString()).build()._toQuery())
                            .build()._toQuery())
                    .build()._toQuery();
        } else if (Long.compare(WhatsAppMessaging.YEARLY_INTERVAL, interval) == 0) {
            return QueryBuilders.bool().must(filter)
                    .must(QueryBuilders.bool()
                            .should(QueryBuilders.term().field(frequencyField)
                                    .value(WhatsAppMessaging.Frequency.YEARLY.toString()).build()._toQuery())
                            .should(QueryBuilders.term().field(frequencyKeywordField)
                                    .value(WhatsAppMessaging.Frequency.YEARLY.toString()).build()._toQuery())
                            .build()._toQuery())
                    .build()._toQuery();
        }
        return QueryBuilders.bool().build()._toQuery();
    }

}
