/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.utilities.bigqueryload.extractor;

import java.io.File;

/**
 * <p>
 *   The interface of TopCoder Big Query Data Extractor.
 * </p>
 *
 * @author GreatKevin
 * @version 1.0
 */
public interface TopCoderBigQueryExtractor {

    /**
     * Extracts the user data from topcoder database into json file accepted by Google Big Query.
     *
     * @param writtenFile the file to write to
     * @param maxRowNumber the max row number
     * @param compressed whether to compress to gzip
     * @throws Exception if any error
     */
    public void extractData(File writtenFile, int maxRowNumber, boolean compressed) throws Exception;

    /**
     * Extracts the data from topcoder database into json file accepted by Google Big Query.
     *
     * @param extractDataFileName the extracted file name.
     * @param maxRowNumber the max row number the query reads each time. The load is done by query multiple times.
     * @param compressed whether the extracted json file should be compressed.
     * @throws Exception if any error occurs.
     */
    public void extractData(String extractDataFileName, int maxRowNumber, boolean compressed) throws Exception;

    /**
     * Extracts the data from topcoder database into json file accepted by Google Big Query. The default max row number
     * will be used.
     *
     * @param extractDateFileName the extracted file name.
     * @param compressed whether the extracted json file should be compressed.
     * @throws Exception if any error occurs.
     */
    public void extractData(String extractDateFileName, boolean compressed) throws Exception;

    /**
     * Extracts the user data from topcoder database into json file accepted by Google Big Query.
     * The default max row number is used.
     *
     * @param writtenFile the file to written to
     * @param compressed whether to compress to gzip
     * @throws Exception if any error
     */
    public void extractData(File writtenFile, boolean compressed) throws Exception;

}
