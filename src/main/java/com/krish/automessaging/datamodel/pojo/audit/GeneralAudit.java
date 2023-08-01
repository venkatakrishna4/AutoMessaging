package com.krish.automessaging.datamodel.pojo.audit;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.krish.automessaging.configuration.constant.TimeConstants;
import com.krish.automessaging.datamodel.pojo.es.PersistenceAudit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Builder
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Component
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeneralAudit extends PersistenceAudit<GeneralAudit> {

    public enum Audit {
        ADD, UPDATE, DELETE
    }

    private String id;
    private String ownerObjectId;
    private Class<?> objectClass;
    private Object oldObject;
    private Object newObject;
    private String action;
    private String comments;
    private String loggedInUserId;
    private String clientIP;
    private boolean ttlEnable = true;
    private long ttlTime = System.currentTimeMillis() + TimeConstants.TIME_IN_180_DAYS;

}
