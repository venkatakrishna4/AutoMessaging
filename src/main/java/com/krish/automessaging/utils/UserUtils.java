package com.krish.automessaging.utils;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SearchType;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.krish.automessaging.datamodel.pojo.User;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class UserUtils {
    private final ElasticsearchClient client;
    private final Utils utils;
    private final static String USER_INDEX_NAME = "user_index";

    @Autowired
    public UserUtils(final ElasticsearchClient client, final Utils utils) {
        this.client = client;
        this.utils = utils;
    }

    public Optional<User> getUserByUsernameOrEmail(String value) throws IOException {
        SearchResponse<User> searchResponse = client.search(SearchRequest.of(searchRequest -> searchRequest
                .index(utils.getFinalIndex(USER_INDEX_NAME))
                .query(QueryBuilders.bool()
                        .must(QueryBuilders.match().field("email.keyword").query(StringUtils.lowerCase(value)).build()
                                ._toQuery())
                        .should(QueryBuilders.match().field("username.keyword").query(StringUtils.lowerCase(value))
                                .build()._toQuery())
                        .build()._toQuery())
                .searchType(SearchType.DfsQueryThenFetch)), User.class);
        List<User> users = searchResponse.hits().hits().stream().map(Hit::source).toList();
        if (ObjectUtils.isNotEmpty(users)) {
            return Optional.of(users.get(0));
        }
        return Optional.empty();
    }

    public boolean isEmailExists(String value) throws IOException {
        return getUserByUsernameOrEmail(value).isPresent();
    }
}
