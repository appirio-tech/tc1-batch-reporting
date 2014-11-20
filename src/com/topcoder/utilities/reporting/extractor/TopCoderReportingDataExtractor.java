/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.utilities.reporting.extractor;

import java.io.File;

/**
 * <p>
 *   The interface of TopCoder Reporting Data Extractor.
 * </p>
 *
 * <p>
 * Version 1.1 (Module Assembly - reporting tool refactor and load user json data to Postgres)
 * <ul>
 *     <li>Added method {@link #getDefaultMaxRowNumber()}</li>
 * </ul>
 * </p>
 *
 * @author GreatKevin, TCSASSEMBLER
 * @version 1.1
 */
public interface TopCoderReportingDataExtractor {

    /**
     * Extracts the user data from topcoder dw database into json file accepted by Google Big Query.
     *
     * @param writtenFile the file to write to
     * @param maxRowNumber the max row number
     * @param compressed whether to compress to gzip
     * @throws Exception if any error
     */
    public int extractData(File writtenFile, int maxRowNumber, boolean compressed) throws Exception;

    /**
     * Extracts the data from topcoder database into json file accepted by Google Big Query.
     *
     * @param extractDataFileName the extracted file name.
     * @param maxRowNumber the max row number the query reads each time. The load is done by query multiple times.
     * @param compressed whether the extracted json file should be compressed.
     * @throws Exception if any error occurs.
     */
    public int extractData(String extractDataFileName, int maxRowNumber, boolean compressed) throws Exception;

    /**
     * Extracts the data from topcoder database into json file accepted by Google Big Query. The default max row number
     * will be used.
     *
     * @param extractDateFileName the extracted file name.
     * @param compressed whether the extracted json file should be compressed.
     * @throws Exception if any error occurs.
     */
    public int extractData(String extractDateFileName, boolean compressed) throws Exception;

    /**
     * Extracts the user data from topcoder database into json file accepted by Google Big Query.
     * The default max row number is used.
     *
     * @param writtenFile the file to written to
     * @param compressed whether to compress to gzip
     * @throws Exception if any error
     */
    public int extractData(File writtenFile, boolean compressed) throws Exception;

    /**
     * Gets the default max row number.
     *
     * @return the default max row number.
     * @since 1.1
     */
    public int getDefaultMaxRowNumber();
}
