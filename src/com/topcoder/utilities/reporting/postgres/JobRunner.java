/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.utilities.reporting.postgres;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.topcoder.utilities.reporting.extractor.TopCoderReportingDataExtractor;
import com.topcoder.utilities.reporting.extractor.generator.JsonContentGenerator;
import com.topcoder.utilities.reporting.extractor.generator.ModelContentGenerator;
import com.topcoder.utilities.reporting.job.postgres.PostgresJobConfig;
import com.topcoder.utilities.reporting.job.postgres.PostgresRunConfig;
import com.topcoder.utilities.reporting.util.Utils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>
 *     The utility to read job configuration from JSON and run each data extraction and postgres load job one by one.
 * </p>
 *
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class JobRunner {
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
        InputStream configInputStream = null;
        PostgresRunConfig loadConfiguration = null;

        Date runDate;
        try {
            configInputStream = JobRunner.class.getClassLoader().getResourceAsStream(args[0]);

            if (configInputStream == null) {
                throw new IllegalArgumentException(String.format("The config file %s does not exist in classpath",
                        args[0]));
            }

            runDate = new Date();

            loadConfiguration = GSON.fromJson(new InputStreamReader(configInputStream),
                    PostgresRunConfig.class);
        } finally {
            IOUtils.closeQuietly(configInputStream);
        }

        if (loadConfiguration.isLogFilePerRunning()) {
            Utils.modifyLog4jProperties(LOG4J_CONFIG_FILE, "log/" + LOG_FILE_DATE_FORMATTER.format(runDate) + ".log");
        }

        // start the job one by one
        if (loadConfiguration.getJobs() == null || loadConfiguration.getJobs().size() == 0) {
            System.err.println("There is no job configuration");
        }

        TableLoader tableLoader = new TableLoader();
        JsonContentGenerator jsonContentGenerator = new JsonContentGenerator();


        for (PostgresJobConfig jc : loadConfiguration.getJobs()) {
            try {
                String extractorClassName = jc.getExtractorClassName();
                Class<?> clazz = Class.forName(extractorClassName);
                TopCoderReportingDataExtractor extractor = (TopCoderReportingDataExtractor)
                        clazz.getConstructor(new Class[]{ModelContentGenerator.class}).newInstance(jsonContentGenerator);

                String dataFileName = jc.getDataFileName();

                if (jc.isExtractFileNameWithDate()) {
                    dataFileName =
                            FilenameUtils.getBaseName(dataFileName) + "_" + DATA_FILE_DATE_FORMATTER.format(runDate) +
                                    "." + FilenameUtils.getExtension(dataFileName);
                }

                int totalRecordsNumber = extractor.extractData(dataFileName, jc.getRowNumberPerQuery(), false);

                jc.getLoaderConfig().setTotalRecordsNumber(totalRecordsNumber);

                tableLoader.loadTable(jc.getLoaderConfig(), dataFileName);

            } catch (Exception ex) {
                System.err.println(
                        String.format("Error when process with this job configuration: %s\n Error Details:\n",
                                GSON.toJson(jc)));
                ex.printStackTrace(System.err);
                throw ex;
            }
        }
    }
}
