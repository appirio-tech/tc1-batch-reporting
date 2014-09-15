/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.utilities.bigqueryload;

import com.google.api.services.bigquery.model.Dataset;
import com.google.api.services.bigquery.model.Job;
import com.google.api.services.bigquery.model.Table;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.topcoder.utilities.bigqueryload.extractor.TopCoderBigQueryExtractor;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * <p>
 *     The utility to read job configuration from JSON and run each data extraction and big query job one by one.
 * </p>
 *
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class BigQueryUtility {
    /**
     * The log4j properties file name.
     */
    private static final String LOG4J_CONFIG_FILE = "log4j.properties";

    /**
     * The date format to append date string to log file name.
     */
    private static DateFormat LOG_FILE_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd_hh-mm");

    /**
     * The date format to append date string to extract date file name.
     */
    private static DateFormat DATA_FILE_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * The Google JSON object.
     */
    private static Gson GSON = new GsonBuilder().setPrettyPrinting().create();


    /**
     * The main utility method.
     *
     * @param args the arguments
     * @throws Exception if there is argument and configuration error.
     */
    public static void main(String[] args) throws Exception {
        // load the configuration
        if (args.length == 0) {
            throw new IllegalArgumentException("The config file name " +
                    "is not specified as run arg  for the utility");
        }

        // check if the specified configuration file exists
        InputStream configInputStream = BigQueryUtility.class.getClassLoader().getResourceAsStream(args[0]);

        if (configInputStream == null) {
            throw new IllegalArgumentException(String.format("The config file %s does not exist in classpath",
                    args[0]));
        }

        Date runDate = new Date();

        LoadConfiguration loadConfiguration = GSON.fromJson(new InputStreamReader(configInputStream),
                LoadConfiguration.class);

        if (loadConfiguration.isLogFilePerRunning()) {
            modifyLog4jProperties(LOG4J_CONFIG_FILE, "log/" + LOG_FILE_DATE_FORMATTER.format(runDate) + ".log");
        }

        // start the job one by one
        if (loadConfiguration.getJobs() == null || loadConfiguration.getJobs().size() == 0) {
            System.err.println("There is no job configuration");
        }

        BigQueryLoaderUtility bigQueryLoaderUtility = new BigQueryLoaderUtility();


        for (JobConfiguration jc : loadConfiguration.getJobs()) {
            try {
                String extractorClassName = jc.getExtractorClassName();
                Class<?> clazz = Class.forName(extractorClassName);
                TopCoderBigQueryExtractor extractor = (TopCoderBigQueryExtractor)
                        clazz.getConstructor(new Class[]{}).newInstance();

                String dataFileName = jc.getDataFileName();

                if (jc.isExtractFileNameWithDate()) {
                    dataFileName =
                            FilenameUtils.getBaseName(dataFileName) + "_" + DATA_FILE_DATE_FORMATTER.format(runDate) +
                                    "." + FilenameUtils.getExtension(dataFileName);
                }

                extractor.extractData(dataFileName, jc.getRowNumberPerQuery(), false);

                Dataset dataset = bigQueryLoaderUtility.createNewDataSet(jc.getBigQueryProjectId(),
                        jc.getBigQueryDatasetId());

                // create the new table, if the table exists, it will not override
                Table table = bigQueryLoaderUtility.createNewTable(dataset.getDatasetReference().getProjectId(),
                        dataset.getDatasetReference().getDatasetId(), jc.getBigQueryTableId(), jc.getTableSchemaFile());

                // load the job
                Job job = bigQueryLoaderUtility.submitLoadJob(table.getTableReference(), table.getSchema(),
                        dataFileName);

                bigQueryLoaderUtility.traceJobStatus(job);

            } catch (Exception ex) {
                System.err.println(
                        String.format("Error when process with this job configuration: %s\n Error Details:\n",
                                GSON.toJson(jc)));
                ex.printStackTrace(System.err);
            }
        }
    }


    /**
     * Helper method to override the log4j properties after initial loading of log4j properties file.
     *
     * @param log4jPropertyPath the path of the log4j properties file.
     * @param logFilePath the logFilePath to override
     */
    private static void modifyLog4jProperties(String log4jPropertyPath, String logFilePath) {
        Properties props = new Properties();
        try {
            InputStream configStream = BigQueryLoaderUtility.class.getClassLoader().getResourceAsStream(
                    log4jPropertyPath);
            props.load(configStream);
            configStream.close();
        } catch (IOException e) {
            System.err.println("log4j configuration file not found");
        }
        props.setProperty("log4j.appender.file.file", logFilePath);
        LogManager.resetConfiguration();
        PropertyConfigurator.configure(props);
    }
}
