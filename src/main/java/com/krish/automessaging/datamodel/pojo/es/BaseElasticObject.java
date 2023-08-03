package com.krish.automessaging.datamodel.pojo.es;

/**
 * The interface Base elastic object.
 */
public interface BaseElasticObject {
    /**
     * Gets id.
     *
     * @return the id
     */
    public String getId();

    /**
     * Gets created time.
     *
     * @return the created time
     */
    public long getCreatedTime();

    /**
     * Sets created time.
     *
     * @param createdTime
     *            the new created time
     */
    public void setCreatedTime(Long createdTime);

    /**
     * Gets created by.
     *
     * @return the created by
     */
    public String getCreatedBy();

    /**
     * Sets created by.
     *
     * @param createdBy
     *            the created by
     */
    public void setCreatedBy(final String createdBy);

    /**
     * Gets last updated by.
     *
     * @return the last updated by
     */
    public String getLastUpdatedBy();

    /**
     * Sets last updated by.
     *
     * @param lastUpdatedBy
     *            the updated by
     */
    public void setLastUpdatedBy(final String lastUpdatedBy);

    /**
     * Gets last updated time.
     *
     * @return the last updated time
     */
    public Long getLastUpdatedTime();

    /**
     * Sets last updated time.
     *
     * @param lastUpdatedTime
     *            the last updated time
     */
    public void setLastUpdatedTime(final Long lastUpdatedTime);

}
