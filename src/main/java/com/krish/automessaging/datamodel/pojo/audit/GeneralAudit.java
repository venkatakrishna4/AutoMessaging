package com.krish.automessaging.datamodel.pojo.audit;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.krish.automessaging.constant.TimeConstants;
import com.krish.automessaging.datamodel.pojo.es.PersistenceAudit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Hash code.
 *
 * @return the int
 */
@EqualsAndHashCode(callSuper = true)

/**
 * To string.
 *
 * @return the java.lang. string
 */
@Builder

/**
 * Gets the ttl time.
 *
 * @return the ttl time
 */
@Getter

/**
 * Sets the ttl time.
 *
 * @param ttlTime
 *            the new ttl time
 */
@Setter

/**
 * To string.
 *
 * @return the java.lang. string
 */
@ToString(callSuper = true)

/**
 * Instantiates a new general audit.
 */
@NoArgsConstructor

/**
 * Instantiates a new general audit.
 *
 * @param id
 *            the id
 * @param ownerObjectId
 *            the owner object id
 * @param objectClass
 *            the object class
 * @param oldObject
 *            the old object
 * @param newObject
 *            the new object
 * @param action
 *            the action
 * @param comments
 *            the comments
 * @param loggedInUserId
 *            the logged in user id
 * @param clientIP
 *            the client IP
 * @param ttlEnable
 *            the ttl enable
 * @param ttlTime
 *            the ttl time
 */
@AllArgsConstructor
@Component
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeneralAudit extends PersistenceAudit<GeneralAudit> {

    /**
     * The Enum Audit.
     */
    public enum Audit {

        /** The add. */
        ADD,
        /** The update. */
        UPDATE,
        /** The delete. */
        DELETE
    }

    /** The id. */
    private String id;

    /** The owner object id. */
    private String ownerObjectId;

    /** The object class. */
    private Class<?> objectClass;

    /** The old object. */
    private Object oldObject;

    /** The new object. */
    private Object newObject;

    /** The action. */
    private String action;

    /** The comments. */
    private String comments;

    /** The logged in user id. */
    private String loggedInUserId;

    /** The client IP. */
    private String clientIP;

    /** The ttl enable. */
    private boolean ttlEnable = true;

    /** The ttl time. */
    private long ttlTime = System.currentTimeMillis() + TimeConstants.TIME_IN_180_DAYS;

}
