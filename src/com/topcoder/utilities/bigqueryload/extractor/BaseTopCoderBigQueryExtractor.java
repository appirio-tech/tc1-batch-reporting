/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.utilities.bigqueryload.extractor;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.zip.GZIPOutputStream;

/**
 * <p>
 * The abstract class acts as the base for all the TopCoder Big Query Extractors.
 * </p>
 *
 * @author GreatKevin
 * @version 1.0
 */
public abstract class BaseTopCoderBigQueryExtractor implements TopCoderBigQueryExtractor {

    /**
     * The logger.
     */
    private static final Logger LOG = Logger.getLogger(BaseTopCoderBigQueryExtractor.class.getName());

    /**
     * The properties file name.
     */
    private static final String BIG_QUERY_DATA_EXTRACTOR_PROPERTIES = "BigQueryDataExtractor.properties";

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
     * The JDBC connection to database topcoder_dw.
     */
    protected Connection topcoderDWConnection;

    /**
     * The JDBC connection to database tcs_dw.
     */
    protected Connection tcsDWConnection;


    /**
     * Reads the sql query from the specified file name in the classpath.
     *
     * @param fileName the file name to read.
     * @return the SQL contents in the file as as string
     * @throws Exception if any error happens while reading the query contents.
     */
    protected String readSQLQuery(String fileName) throws Exception {
        BufferedReader reader = null;
        try {

            reader = new BufferedReader(
                    new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(fileName)));

            StringBuffer sqlQueryBuffer = new StringBuffer();
            String line;

            while ((line = reader.readLine()) != null) {
                sqlQueryBuffer.append(line);
                sqlQueryBuffer.append("\n");
            }

            return sqlQueryBuffer.toString();

        } finally {
            if (reader != null) {
                reader.close();

            }
        }
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
     * Helper method to compress the gzip file
     *
     * @param inputFile      the input file to check.
     * @param compressedFile the file to compress.
     */
    protected void compressGzipFile(File inputFile, File compressedFile) {

        FileInputStream fis = null;
        FileOutputStream fos = null;
        GZIPOutputStream gzipOS = null;


        try {
            fis = new FileInputStream(inputFile);

            if (!compressedFile.exists()) {
                compressedFile.createNewFile();
            }

            fos = new FileOutputStream(compressedFile);
            gzipOS = new GZIPOutputStream(fos);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                gzipOS.write(buffer, 0, len);
            }

        } catch (Exception e) {
            LOG.error(String.format("Compress file %s into %s failed,",
                    inputFile.getAbsolutePath(), compressedFile.getAbsolutePath()), e);
        } finally {
            try {
                if (gzipOS != null) {
                    gzipOS.close();
                }
                if (fos != null) {
                    fos.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception ce) {

            }
        }
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
                            // reading the value of the SQL column
                            Object columnValue = rs.getObject(_iterator + 1);
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
