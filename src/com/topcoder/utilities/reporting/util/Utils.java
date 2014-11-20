/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.utilities.reporting.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.zip.GZIPOutputStream;

/**
 * <p>
 * The utility to provide utility methods.
 * </p>
 *
 * @author TCSASSEMBLER
 * @version 1.0 (Module Assembly - reporting tool refactor and load user json data to Postgres)
 */
public class Utils {

    /**
     * The logger.
     */
    private static Log log = LogFactory.getLog(Utils.class);

    /**
     * The gson instance.
     */
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Prevents from initialization.
     */
    private Utils() {

    }

    /**
     * Utility method to check if every row of the json data file to upload to BigQuery is valid json object.
     *
     * @param fileName the upload file to check.
     * @return true if the json data file is valid, otherwise false
     * @throws Exception if any error occurs.
     */
    public static boolean isJsonDataFileValid(String fileName) throws Exception {

        log.info(String.format("Validates the json data file %s", fileName));

        InputStreamReader inputStreamReader = new InputStreamReader(
                Utils.class.getClassLoader().getResourceAsStream(fileName));

        BufferedReader reader = new BufferedReader(inputStreamReader);

        Map<Integer, String> errors = new TreeMap<Integer, String>();
        int lineNumber = 0;
        String line;
        boolean isJsonDataValid = true;

        // each line should be a valid json object
        while ((line = reader.readLine()) != null) {
            lineNumber++;
            try {
                GSON.fromJson(line, Object.class);
            } catch (Exception jsonEx) {
                isJsonDataValid = false;
                errors.put(lineNumber, jsonEx.getMessage());
            }
        }

        if (!isJsonDataValid) {
            log.error("Errors detected in the json data file:" + fileName);
        }

        // print out errors
        for (Integer i : errors.keySet()) {
            String errorMsg = String.format("Line %d Error Message: %s ", i, errors.get(i));

            // output to console
            System.err.println(errorMsg);
            // log with logger
            log.error(errorMsg);
        }

        return isJsonDataValid;
    }

    /**
     * Helper method to compress the gzip file
     *
     * @param inputFile      the input file to check.
     * @param compressedFile the file to compress.
     */
    public static void compressGzipFile(File inputFile, File compressedFile) {
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
            log.error(String.format("Compress file %s into %s failed,",
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
     * Reads the sql query from the specified file name in the classpath.
     *
     * @param fileName the file name to read.
     * @return the SQL contents in the file as as string
     * @throws Exception if any error happens while reading the query contents.
     */
    public static String readFileContentAsString(String fileName) throws Exception {
        checkNullEmptyString(fileName, "fileName");

        BufferedReader reader = null;
        try {

            reader = new BufferedReader(
                    new InputStreamReader(Utils.class.getClassLoader().getResourceAsStream(fileName)));

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

    public static void checkNullEmptyString(String strToCheck, String argumentName) {
        if (strToCheck == null || strToCheck.trim().length() == 0) {
            throw new IllegalArgumentException("The argument : [" + argumentName + "] should not be null or empty");
        }
    }

    /**
     * Helper method to override the log4j properties after initial loading of log4j properties file.
     *
     * @param log4jPropertyPath the path of the log4j properties file.
     * @param logFilePath       the logFilePath to override
     */
    public static void modifyLog4jProperties(String log4jPropertyPath, String logFilePath) {
        Properties props = new Properties();
        InputStream configStream = null;
        try {
            configStream = Utils.class.getClassLoader().getResourceAsStream(
                    log4jPropertyPath);
            props.load(configStream);
        } catch (IOException e) {
            System.err.println("log4j configuration file not found");
        } finally {
            IOUtils.closeQuietly(configStream);
        }
        props.setProperty("log4j.appender.file.file", logFilePath);
        LogManager.resetConfiguration();
        PropertyConfigurator.configure(props);
    }

    /**
     * Closes the jdbc statement
     *
     * @param statement the statement
     */
    public static void close(Statement statement) {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            // ignore
        }
    }

    /**
     * Closes the jdbc connection
     *
     * @param connection the connection
     */
    public static void close(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            // ignore
        }
    }

    /**
     * Closes the jdbc resultset
     *
     * @param resultset the resultset
     */
    public static void close(ResultSet resultset) {
        try {
            if (resultset != null) {
                resultset.close();
            }
        } catch (SQLException e) {
            // ignore
        }
    }

    /**
     * Closes the reader
     *
     * @param reader the reader
     */
    public static void close(Reader reader) {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException e) {
            // ignore
        }
    }
}
