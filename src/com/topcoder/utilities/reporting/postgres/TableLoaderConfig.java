/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.utilities.reporting.postgres;

/**
 * <p>The table loader configuration for Postgres table loader</p>
 *
 * @author TCSASSEMBLER
 * @version 1.0 (Module Assembly - reporting tool refactor and load user json data to Postgres)
 */
public class TableLoaderConfig {

    /**
     * The table name.
     */
    private String tableName;

    /**
     * The setup script.
     */
    private String setupScript;

    /**
     * The clear script.
     */
    private String clearScript;

    /**
     * The insert script.
     */
    private String insertScript;

    /**
     * The schema file.
     */
    private String schema;

    /**
     * The total records number to insert.
     */
    private int totalRecordsNumber;

    /**
     * Gets the table name.
     *
     * @return
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Sets the table name.
     *
     * @param tableName the table name.
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * Gets the setup script.
     *
     * @return the setup script.
     */
    public String getSetupScript() {
        return setupScript;
    }

    /**
     * Sets the setup script.
     *
     * @param setupScript the setup script.
     */
    public void setSetupScript(String setupScript) {
        this.setupScript = setupScript;
    }

    /**
     * Gets the clear script.
     *
     * @return the clear script.
     */
    public String getClearScript() {
        return clearScript;
    }

    /**
     * Sets the clear script.
     *
     * @param clearScript the clear script.
     */
    public void setClearScript(String clearScript) {
        this.clearScript = clearScript;
    }

    /**
     * Gets the insert script.
     *
     * @return the insert script.
     */
    public String getInsertScript() {
        return insertScript;
    }

    /**
     * Sets the insert script.
     *
     * @param insertScript the insert script.
     */
    public void setInsertScript(String insertScript) {
        this.insertScript = insertScript;
    }

    /**
     * Gets the schema.
     *
     * @return the schema.
     */
    public String getSchema() {
        return schema;
    }

    /**
     * Sets the schema.
     *
     * @param schema the schema.
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }

    /**
     * Gets the total records number.
     *
     * @return the total records number.
     */
    public int getTotalRecordsNumber() {
        return totalRecordsNumber;
    }

    /**
     * Sets the total records number
     *
     * @param totalRecordsNumber the total records number.
     */
    public void setTotalRecordsNumber(int totalRecordsNumber) {
        this.totalRecordsNumber = totalRecordsNumber;
    }
}
