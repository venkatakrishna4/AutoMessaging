package com.krish.automessaging.datamodel.pojo.audit;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.krish.automessaging.constant.TimeConstants;
import com.krish.automessaging.datamodel.pojo.es.PersistenceAudit;

/**
 * {@link GeneralAudit} stores all the DB altering operation information
 */

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

    public String getId() {
        return id;
    }

    public String getOwnerObjectId() {
        return ownerObjectId;
    }

    public Class<?> getObjectClass() {
        return objectClass;
    }

    public Object getOldObject() {
        return oldObject;
    }

    public Object getNewObject() {
        return newObject;
    }

    public String getAction() {
        return action;
    }

    public String getComments() {
        return comments;
    }

    public String getLoggedInUserId() {
        return loggedInUserId;
    }

    public String getClientIP() {
        return clientIP;
    }

    public boolean isTtlEnable() {
        return ttlEnable;
    }

    public long getTtlTime() {
        return ttlTime;
    }

    public GeneralAudit(Builder builder) {
        super();
        this.id = builder.id;
        this.ownerObjectId = builder.ownerObjectId;
        this.objectClass = builder.objectClass;
        this.oldObject = builder.oldObject;
        this.newObject = builder.newObject;
        this.action = builder.action;
        this.comments = builder.comments;
        this.loggedInUserId = builder.loggedInUserId;
        this.clientIP = builder.clientIP;
        this.ttlEnable = builder.ttlEnable;
        this.ttlTime = builder.ttlTime;
    }

    public static class Builder {
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

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setOwnerObjectId(String ownerObjectId) {
            this.ownerObjectId = ownerObjectId;
            return this;
        }

        public Builder setObjectClass(Class<?> objectClass) {
            this.objectClass = objectClass;
            return this;
        }

        public Builder setOldObject(Object oldObject) {
            this.oldObject = oldObject;
            return this;
        }

        public Builder setNewObject(Object newObject) {
            this.newObject = newObject;
            return this;
        }

        public Builder setAction(String action) {
            this.action = action;
            return this;
        }

        public Builder setComments(String comments) {
            this.comments = comments;
            return this;
        }

        public Builder setLoggedInUserId(String loggedInUserId) {
            this.loggedInUserId = loggedInUserId;
            return this;
        }

        public Builder setClientIP(String clientIP) {
            this.clientIP = clientIP;
            return this;
        }

        public Builder setTtlEnable(boolean ttlEnable) {
            this.ttlEnable = ttlEnable;
            return this;
        }

        public Builder setTtlTime(long ttlTime) {
            this.ttlTime = ttlTime;
            return this;
        }

        public GeneralAudit build() {
            return new GeneralAudit(this);
        }
    }

    @Override
    public long getCreatedTime() {

        return super.getCreatedTime();
    }

    @Override
    public void setCreatedTime(Long createdTime) {

        super.setCreatedTime(createdTime);
    }

    @Override
    public String getCreatedBy() {

        return super.getCreatedBy();
    }

    @Override
    public void setCreatedBy(String createdBy) {

        super.setCreatedBy(createdBy);
    }

    @Override
    public String getLastUpdatedBy() {

        return super.getLastUpdatedBy();
    }

    @Override
    public void setLastUpdatedBy(String lastUpdatedBy) {

        super.setLastUpdatedBy(lastUpdatedBy);
    }

    @Override
    public Long getLastUpdatedTime() {

        return super.getLastUpdatedTime();
    }

    @Override
    public void setLastUpdatedTime(Long lastUpdatedTime) {

        super.setLastUpdatedTime(lastUpdatedTime);
    }

    @Override
    public int compareTo(GeneralAudit o) {

        return super.compareTo(o);
    }

    @Override
    public boolean equals(Object o) {

        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
