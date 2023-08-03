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

    /** The id. */
    private final String id = Utils.generateUUID();

    /** The created by. */
    private String createdBy;

    /** The last updated by. */
    private String lastUpdatedBy;

    /** The created time. */
    private Long createdTime = System.currentTimeMillis();

    /** The last updated time. */
    private Long lastUpdatedTime = System.currentTimeMillis();

    /**
     * Gets the id.
     *
     * @return the id
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * Gets the created time.
     *
     * @return the created time
     */
    @Override
    public long getCreatedTime() {
        return createdTime;
    }

    /**
     * Sets the created time.
     *
     * @param createdTime
     *            the new created time
     */
    @Override
    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }

    /**
     * Gets the created by.
     *
     * @return the created by
     */
    @Override
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets the created by.
     *
     * @param createdBy
     *            the new created by
     */
    @Override
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Gets the last updated by.
     *
     * @return the last updated by
     */
    @Override
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    /**
     * Sets the last updated by.
     *
     * @param lastUpdatedBy
     *            the new last updated by
     */
    @Override
    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    /**
     * Gets the last updated time.
     *
     * @return the last updated time
     */
    @Override
    public Long getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    /**
     * Sets the last updated time.
     *
     * @param lastUpdatedTime
     *            the new last updated time
     */
    @Override
    public void setLastUpdatedTime(Long lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    /**
     * Compare to.
     *
     * @param o
     *            the o
     *
     * @return the int
     */
    @Override
    public int compareTo(T o) {
        return createdTime.compareTo(o.getCreatedTime());
    }

    /**
     * Equals.
     *
     * @param o
     *            the o
     *
     * @return true, if successful
     */
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

    /**
     * Hash code.
     *
     * @return the int
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, createdBy, lastUpdatedBy, createdTime, lastUpdatedTime);
    }
}
