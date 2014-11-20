/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.utilities.reporting.extractor;

import com.topcoder.utilities.reporting.extractor.generator.ModelContentGenerator;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * <p>
 * The abstract class acts as the base for all the TopCoder Big Query Extractors.
 * </p>
 *
 * <p>
 * Version 1.1 (TopCoder Big Query Data Extractor - Jira Issue Data)
 * <ul>
 *     <li>Updated ResultSetMapper class to support blob data type</li>
 * </ul>
 * </p>
 *
 * <p>
 * Version 1.2 (Module Assembly - reporting tool refactor and load user json data to Postgres)
 * <ul>
 *     <li>Added the {@link ModelContentGenerator #generator}</li>
 *     <li>Added constructor to take the model generator as argument</li>
 *     <li>Refactor 3 extract data methods into the base class</li>
 * </ul>
 * </p>
 *
 * @author GreatKevin, Veve, TCSASSEMBLER
 * @version 1.2
 */
public abstract class BaseTopCoderReportingDataExtractor implements TopCoderReportingDataExtractor {

    /**
     * The logger.
     */
    private static final Logger LOG = Logger.getLogger(BaseTopCoderReportingDataExtractor.class.getName());

    /**
     * The properties file name.
     */
    private static final String BIG_QUERY_DATA_EXTRACTOR_PROPERTIES = "DataExtractor.properties";

    /**
     * The property key for reading the topcoder_dw database connection string for the configuration file.
     */
    public static final String TOPCODER_DW_CONNECTION_KEY = "topcoder_dw_connection_str";

    /**
     * The property key for reading the tcs_dw database connection string for the configuration file.
     */
    public static final String TCS_DW_CONNECTION_KEY = "tcs_dw_connection_str";

    /**
     * The error message buffer.
     */
    private static StringBuffer sErrorMsg = new StringBuffer(128);

    /**
     * The JDBC driver name of informix database.
     */
    private static String sDriverName = "com.informix.jdbc.IfxDriver";

    /**
     * The variable to store the topcoder_dw connection string configuration.
     */
    private static String TOPCODER_DW_JDBC_URL = "";

    /**
     * The variable to store the tcs_dw connection string configuration.
     */
    private static String TCS_DW_JDBC_URL = "";

    /**
     * The directory name to export the extracted data to.
     */
    private static final String EXPORT_DIR = "data";

    /**
     * The JDBC connection to database topcoder_dw.
     */
    protected Connection topcoderDWConnection;

    /**
     * The JDBC connection to database tcs_dw.
     */
    protected Connection tcsDWConnection;

    /**
     * The ModelContentGenerator to use.
     *
     * @since 1.2
     */
    protected ModelContentGenerator generator;


    /**
     * Creates the intance with the specified ModelContentGenerator
     *
     * @param contentGenerator the model content generator instance.
     * @since 1.2
     */
    public BaseTopCoderReportingDataExtractor(ModelContentGenerator contentGenerator) {

        if (contentGenerator == null) {
            throw new IllegalArgumentException("The argument contentGenerator should not be null.");
        }

        this.generator = contentGenerator;
    }

    /**
     * Extracts the user data from topcoder database into json file accepted by Google Big Query.
     *
     * @param extractDataFileName the extracted file name.
     * @param maxRowNumber        the max row number the query reads each time. The load is done by query multiple times.
     * @param compressed          whether the extracted json file should be compressed.
     * @throws Exception if any error occurs.
     */
    @Override
    public int extractData(String extractDataFileName, int maxRowNumber, boolean compressed) throws Exception {

        LOG.info("Enter method extractData(String extractDataFileName, int maxRowNumber, boolean compressed)");
        LOG.info(String.format("extractDataFileName:%s , maxRowNumber: %d, compressed:%s", extractDataFileName,
                maxRowNumber, compressed));

        if (extractDataFileName == null || extractDataFileName.trim().length() == 0) {
            throw new IllegalArgumentException("The extractDataFileName should not be empty");
        }

        File writtenFile = new File("." + File.separator + EXPORT_DIR + File.separator + extractDataFileName);

        int count = extractData(writtenFile, maxRowNumber, compressed);

        LOG.info("Exist method extractData(String extractDataFileName, int maxRowNumber, boolean compressed)");

        return count;
    }

    /**
     * Extracts the user data from topcoder database into json file accepted by Google Big Query.
     * The default max row number is used.
     *
     * @param extractDateFileName the extracted file name.
     * @param compressed          whether the extracted json file should be compressed.
     * @throws Exception if any error occurs.
     */
    @Override
    public int extractData(String extractDateFileName, boolean compressed) throws Exception {
        return this.extractData(extractDateFileName, getDefaultMaxRowNumber(), compressed);
    }

    /**
     * Extracts the user data from topcoder database into json file accepted by Google Big Query.
     * The default max row number is used.
     *
     * @param writtenFile the file to written to
     * @param compressed  whether to compress to gzip
     * @throws Exception if any error
     */
    @Override
    public int extractData(File writtenFile, boolean compressed) throws Exception {
        return this.extractData(writtenFile, getDefaultMaxRowNumber(), compressed);
    }


    /**
     * Gets the database connection to the topcoder_dw database.
     *
     * @return database connection.
     * @throws Exception if there is any error.
     */
    protected Connection getTopcoderDWConnection() throws Exception {
        if (topcoderDWConnection == null || topcoderDWConnection.isClosed()) {
            // need to create a new connection
            checkDriver();

            try {
                Class.forName(sDriverName);
            } catch (ClassNotFoundException e) {
                sErrorMsg.setLength(0);
                sErrorMsg.append("DB driver load failed. ");
                sErrorMsg.append("Cannot continue.\n");
                sErrorMsg.append(e.getMessage());
                LOG.error(sErrorMsg.toString(), e);
                throw e;
            }

            if (TOPCODER_DW_JDBC_URL.trim().length() == 0) {
                // URL is not loaded, load URL
                Properties config = new Properties();
                config.load(this.getClass().getClassLoader().getResourceAsStream(BIG_QUERY_DATA_EXTRACTOR_PROPERTIES));
                TOPCODER_DW_JDBC_URL = config.getProperty(TOPCODER_DW_CONNECTION_KEY);
            }

            topcoderDWConnection = DriverManager.getConnection(TOPCODER_DW_JDBC_URL);
        }

        return topcoderDWConnection;
    }

    /**
     * Gets the database connection to the tcs_dw database.
     *
     * @return database connection.
     * @throws Exception if there is any error.
     */
    protected Connection getTcsDWConnection() throws Exception {
        if (tcsDWConnection == null || tcsDWConnection.isClosed()) {
            // need to create a new connection
            checkDriver();

            try {
                Class.forName(sDriverName);
            } catch (ClassNotFoundException e) {
                sErrorMsg.setLength(0);
                sErrorMsg.append("DB driver load failed. ");
                sErrorMsg.append("Cannot continue.\n");
                sErrorMsg.append(e.getMessage());
                LOG.error(sErrorMsg.toString(), e);
                throw e;
            }

            if (TCS_DW_JDBC_URL.trim().length() == 0) {
                // URL is not loaded, load URL
                Properties config = new Properties();
                config.load(this.getClass().getClassLoader().getResourceAsStream(BIG_QUERY_DATA_EXTRACTOR_PROPERTIES));
                TCS_DW_JDBC_URL = config.getProperty(TCS_DW_CONNECTION_KEY);
            }

            tcsDWConnection = DriverManager.getConnection(TCS_DW_JDBC_URL);
        }

        return tcsDWConnection;
    }


    /**
     * Checks whether the JDBC driver class can be loaded.
     */
    private static void checkDriver() {
        try {
            Class.forName(sDriverName);
        } catch (Exception ex) {
            sErrorMsg.setLength(0);
            sErrorMsg.append("Unable to load driver ");
            sErrorMsg.append(sDriverName);
            sErrorMsg.append(". Cannot continue.");
            fatal_error();
        }
    }

    /**
     * Prints out the fatal error mesage directly to the running console.
     */
    private static void fatal_error() {
        System.out.println("*******************************************");
        System.out.println("FAILURE: " + sErrorMsg.toString());
        System.out.println("*******************************************");
        System.exit(-1);
    }


    /**
     * The inner static class ResultSetMapper which maps the result set to the specified class.
     *
     * @param <T> the type param
     */
    public static class ResultSetMapper<T> {

        /**
         * Maps the result set to to a list of instances of specified class
         *
         * @param rs          the result set.
         * @param outputClass the class type.
         * @return a list of instances of the specified class, null is no result
         */
        @SuppressWarnings("unchecked")
        public List<T> mapRersultSetToObject(ResultSet rs, Class outputClass) throws Exception {
            List<T> outputList = null;
            try {
                // make sure resultset is not null
                if (rs != null) {
                    // get the resultset metadata
                    ResultSetMetaData rsmd = rs.getMetaData();

                    // get all the attributes of outputClass
                    Field[] fields = outputClass.getDeclaredFields();
                    while (rs.next()) {
                        T bean = (T) outputClass.newInstance();
                        for (int _iterator = 0; _iterator < rsmd
                                .getColumnCount(); _iterator++) {
                            // getting the SQL column name
                            String columnName = rsmd
                                    .getColumnName(_iterator + 1);

                            String columnTypeName =  rsmd
                                    .getColumnTypeName(_iterator + 1);

                            // reading the value of the SQL column
                            Object columnValue = rs.getObject(_iterator + 1);

                            if (columnTypeName.equalsIgnoreCase("text") && columnValue != null) {
                                columnValue = new String((byte[]) columnValue);
                            }

                            // iterating over outputClass attributes matching 'name' value
                            for (Field field : fields) {

                                String filedName = field.getName();

                                if (filedName.equalsIgnoreCase(
                                        columnName)
                                        && columnValue != null) {
                                    BeanUtils.setProperty(bean, filedName, columnValue);
                                    break;
                                }

                            }
                        }
                        if (outputList == null) {
                            outputList = new ArrayList<T>();
                        }
                        outputList.add(bean);
                    }


                } else {
                    return null;
                }
            } catch (Exception ex) {
                LOG.error("Error while mapping the result set to a list of " + outputClass.getName(), ex);
                throw ex;
            }
            return outputList;
        }
    }
}
