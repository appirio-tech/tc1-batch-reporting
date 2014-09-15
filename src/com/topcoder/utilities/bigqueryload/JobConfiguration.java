/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.utilities.bigqueryload;

/**
 * <p>
 * The configuration bean for a standalone extract and load job.
 * </p>
 *
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class JobConfiguration {

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
     * The class name of the extractor.
     */
    private String extractorClassName;

    /**
     * The file name of the extract data file.
     */
    private String dataFileName;

    /**
     * The rows to retrieve per query.
     */
    private int rowNumberPerQuery;

    /**
     * The flag on whether to append date to extract file name.
     */
    private boolean extractFileNameWithDate;

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

    /**
     * Gets the extractor class name.
     *
     * @return the extractor class name.
     */
    public String getExtractorClassName() {
        return extractorClassName;
    }

    /**
     * Sets the extractor class name.
     *
     * @param extractorClassName the extractor class name.
     */
    public void setExtractorClassName(String extractorClassName) {
        this.extractorClassName = extractorClassName;
    }

    /**
     * Gets the data file name.
     *
     * @return the data file name.
     */
    public String getDataFileName() {
        return dataFileName;
    }

    /**
     * Sets the data file name.
     *
     * @param dataFileName the data file name.
     */
    public void setDataFileName(String dataFileName) {
        this.dataFileName = dataFileName;
    }

    /**
     * Gets whether the extract file name needs the date.
     *
     * @return the flag on  whether the extract file name needs the date.
     */
    public boolean isExtractFileNameWithDate() {
        return extractFileNameWithDate;
    }

    /**
     * Sets whether the extract file name needs the date.
     *
     * @param extractFileNameWithDate the flag on  whether the extract file name needs the date.
     */
    public void setExtractFileNameWithDate(boolean extractFileNameWithDate) {
        this.extractFileNameWithDate = extractFileNameWithDate;
    }

    /**
     * Gets the row number per query.
     *
     * @return the row number per query.
     */
    public int getRowNumberPerQuery() {
        return rowNumberPerQuery;
    }

    /**
     * Sets the row number per query.
     *
     * @param rowNumberPerQuery the row number per query.
     */
    public void setRowNumberPerQuery(int rowNumberPerQuery) {
        this.rowNumberPerQuery = rowNumberPerQuery;
    }
}
