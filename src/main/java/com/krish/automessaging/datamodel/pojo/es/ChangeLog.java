package com.krish.automessaging.datamodel.pojo.es;

import org.springframework.stereotype.Component;

/**
 * The type Change log.
 */
@Component
public class ChangeLog extends PersistenceAudit<ChangeLog> {

    /** The id. */
    private String id;

    /** The index name. */
    private String indexName;

    /** The file name. */
    private String fileName;

    /** The processed. */
    private boolean processed = false;

    /**
     * Gets the id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id
     *            the id
     *
     * @return the id
     */
    public ChangeLog setId(String id) {
        this.id = id;
        return this;
    }

    /**
     * Gets index name.
     *
     * @return the index name
     */
    public String getIndexName() {
        return indexName;
    }

    /**
     * Sets index name.
     *
     * @param index
     *            the index
     *
     * @return the index name
     */
    public ChangeLog setIndexName(String index) {
        this.indexName = index;
        return this;
    }

    /**
     * Gets file name.
     *
     * @return the file name
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets file name.
     *
     * @param fileName
     *            the file name
     *
     * @return the file name
     */
    public ChangeLog setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    /**
     * Is processed boolean.
     *
     * @return the boolean
     */
    public boolean isProcessed() {
        return processed;
    }

    /**
     * Sets processed.
     *
     * @param processed
     *            the processed
     *
     * @return the processed
     */
    public ChangeLog setProcessed(boolean processed) {
        this.processed = processed;
        return this;
    }

    /**
     * Equals.
     *
     * @param obj
     *            the obj
     *
     * @return true, if successful
     */
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    /**
     * Hash code.
     *
     * @return the int
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}