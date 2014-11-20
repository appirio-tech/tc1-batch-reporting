/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.utilities.reporting.job;

/**
 * <p>
 * The configuration bean for a standalone extract and load job.
 * </p>
 *
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class AbstractJobConfig {

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
