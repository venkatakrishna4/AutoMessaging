package com.krish.automessaging.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Random;
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

}
