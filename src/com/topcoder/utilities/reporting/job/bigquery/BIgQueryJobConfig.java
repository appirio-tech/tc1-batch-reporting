/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.utilities.reporting.job.bigquery;

import com.topcoder.utilities.reporting.job.AbstractJobConfig;

/**
 * <p>The job configuration for the big query load</p>
 *
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class BIgQueryJobConfig extends AbstractJobConfig {
    /**
     * The big query project ID.
     */
    private String bigQueryProjectId;

    /**
     * The big query dataset id.
     */
    private String bigQueryDatasetId;

    /**
     * The big query table id.
     */
    private String bigQueryTableId;

    /**
     * The table schema file.
     */
    private String tableSchemaFile;

    /**
     * Gets the big query project ID.
     *
     * @return the big query project ID.
     */
    public String getBigQueryProjectId() {
        return bigQueryProjectId;
    }

    /**
     * Sets the big query project ID.
     *
     * @param bigQueryProjectId the big query project ID.
     */
    public void setBigQueryProjectId(String bigQueryProjectId) {
        this.bigQueryProjectId = bigQueryProjectId;
    }

    /**
     * Gets the big query dataset id.
     *
     * @return the big query dataset id.
     */
    public String getBigQueryDatasetId() {
        return bigQueryDatasetId;
    }

    /**
     * Sets the big query dataset id.
     *
     * @param bigQueryDatasetId the big query dataset id.
     */
    public void setBigQueryDatasetId(String bigQueryDatasetId) {
        this.bigQueryDatasetId = bigQueryDatasetId;
    }

    /**
     * Gets the big query table id.
     *
     * @return the big query table id.
     */
    public String getBigQueryTableId() {
        return bigQueryTableId;
    }

    /**
     * Sets the big query table id.
     *
     * @param bigQueryTableId the big query table id.
     */
    public void setBigQueryTableId(String bigQueryTableId) {
        this.bigQueryTableId = bigQueryTableId;
    }

    /**
     * Gets the table schema file.
     *
     * @return the table schema file.
     */
    public String getTableSchemaFile() {
        return tableSchemaFile;
    }

    /**
     * Sets the table schema file.
     *
     * @param tableSchemaFile the table schema file.
     */
    public void setTableSchemaFile(String tableSchemaFile) {
        this.tableSchemaFile = tableSchemaFile;
    }

}
