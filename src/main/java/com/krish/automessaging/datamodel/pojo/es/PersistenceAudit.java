package com.krish.automessaging.datamodel.pojo.es;

import java.util.Objects;

import com.krish.automessaging.utils.Utils;

/**
 * The type Persistence audit.
 *
 * @param <T>
 *            the type parameter
 */

public class PersistenceAudit<T extends PersistenceAudit<T>> implements Comparable<T>, BaseElasticObject {
    private final String id = Utils.generateUUID();
    private String createdBy;
    private String lastUpdatedBy;
    private Long createdTime = System.currentTimeMillis();
    private Long lastUpdatedTime = System.currentTimeMillis();

    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getCreatedTime() {
        return createdTime;
    }

    @Override
    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String getCreatedBy() {
        return createdBy;
    }

    @Override
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    @Override
    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    @Override
    public Long getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    @Override
    public void setLastUpdatedTime(Long lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    @Override
    public int compareTo(T o) {
        return createdTime.compareTo(o.getCreatedTime());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        PersistenceAudit<?> that = (PersistenceAudit<?>) o;
        return Objects.equals(id, that.id) && Objects.equals(createdBy, that.createdBy)
                && Objects.equals(lastUpdatedBy, that.lastUpdatedBy) && Objects.equals(createdTime, that.createdTime)
                && Objects.equals(lastUpdatedTime, that.lastUpdatedTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdBy, lastUpdatedBy, createdTime, lastUpdatedTime);
    }
}
