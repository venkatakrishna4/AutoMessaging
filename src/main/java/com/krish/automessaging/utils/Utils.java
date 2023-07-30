package com.krish.automessaging.utils;

import com.krish.automessaging.datamodel.pojo.es.BaseElasticObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * The type Utils.
 */
@Component
public class Utils {
    /**
     * Generate uuid string.
     *
     * @return the string
     */

    @Value("${elastic.tenant}")
    private String tenant;

    public String getFinalIndex(String indexName) {
        if (StringUtils.isNotBlank(tenant) && StringUtils.isNotBlank(indexName)
                && !StringUtils.startsWith(indexName, tenant.concat("."))) {
            return tenant.concat(".").concat(indexName);
        }
        return indexName;
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public void setAuditProperties(BaseElasticObject source) {
        try {
            String user = null; // AuthUtils.getLoggedInUserId(); //TODO get logged-in user
            long time = System.currentTimeMillis();
            if (StringUtils.isBlank(user)) {
                user = "api";
            }
            if (StringUtils.isBlank(source.getCreatedBy())) {
                source.setCreatedBy(user);
            }
            if (source.getCreatedTime() <= 0) {
                source.setCreatedTime(time);
            }
            source.setLastUpdatedBy(user);
            source.setLastUpdatedTime(time);
        } catch (Exception ignored) {
        }
    }
}
