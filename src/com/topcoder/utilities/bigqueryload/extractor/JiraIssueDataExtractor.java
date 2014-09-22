/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.utilities.bigqueryload.extractor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * <p>Extracts the jira issue data</p>
 *
 * @author Veve
 * @version 1.0
 */
public class JiraIssueDataExtractor extends BaseTopCoderBigQueryExtractor {
    /**
     * The logger.
     */
    private static final Logger LOG = Logger.getLogger(JiraIssueDataExtractor.class.getName());

    /**
     * The sql query to get the jira issue data from the database.
     */
    private static final String JIRA_ISSUE_DATA_SQL = "jiraIssueData.sql";

    /**
     * The directory name to export the extracted data to.
     */
    private static final String EXPORT_DIR = "data";

    /**
     * The default max row number the extraction query reads each time.
     */
    private static final int DEFAULT_MAX_ROW = 10000;

    /**
     * The default fetch size of the JDBC SQL statement
     */
    private static final int DEFAULT_FETCH_SIZE = 2000;

    /**
     * Extracts the user data from topcoder database into json file accepted by Google Big Query.
     *
     * @param writtenFile  the file to write to
     * @param maxRowNumber the max row number
     * @param compressed   whether to compress to gzip
     * @throws Exception if any error
     */
    public void extractData(File writtenFile, int maxRowNumber, boolean compressed) throws Exception {
        LOG.info("Enter method extractData(String extractDataFileName, int maxRowNumber, boolean compressed)");
        LOG.info(String.format("writtenFile:%s , maxRowNumber: %d, compressed:%s", writtenFile.getAbsolutePath(),
                maxRowNumber, compressed));

        if (writtenFile == null) {
            throw new IllegalArgumentException("The argument writtenFile should not be null");
        }

        if (writtenFile.exists() && !writtenFile.canWrite()) {
            // if the file exists, but cannot write to, throw exception
            throw new IllegalArgumentException(
                    String.format("The file %s exists, but cannot be written to", writtenFile.getAbsolutePath()));
        }


        if (maxRowNumber <= 0) {
            throw new IllegalArgumentException("The max row number should be positive");
        }


        long time = System.currentTimeMillis();
        long startTime = time;

        String sqlQueryTemplate = readSQLQuery(JIRA_ISSUE_DATA_SQL);
        Connection conn = getTcsDWConnection();
        Statement statement = conn.createStatement();
        statement.setFetchSize(DEFAULT_FETCH_SIZE);
        int skipCount = 0;
        ResultSet resultSet = null;
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd hh:mm:ss").create();
        JiraIssueDataExtractor.ResultSetMapper<JiraIssue> rsm = new JiraIssueDataExtractor.ResultSetMapper<JiraIssue>();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(writtenFile, false), "UTF8"));
        String sql = "";

        try {
            while (true) {
                sql = sqlQueryTemplate.replace("@skip@", String.valueOf(skipCount));
                sql = sql.replace("@maxRow@", String.valueOf(maxRowNumber));

                resultSet = statement.executeQuery(sql);

                List<JiraIssue> issues = rsm.mapRersultSetToObject(resultSet, JiraIssue.class);

                if (issues == null || issues.size() == 0) {
                    // no more result, load is done
                    LOG.info(String.format(
                            "The whole issue data extraction is done, total records: %d, total time: %.1f seconds",
                            skipCount, (System.currentTimeMillis() - startTime) / 1000.0));
                    break;
                }

                for (JiraIssue issue : issues) {
                    writer.write(gson.toJson(issue));
                    writer.newLine();
                }

                writer.flush();

                LOG.info(String.format("SKIP : %d, LIMIT: %d, Time Taken: %.1f seconds", skipCount, maxRowNumber,
                        (System.currentTimeMillis() - time) / 1000.0));
                time = System.currentTimeMillis();

                skipCount += issues.size();
            }
        } catch (SQLException sqle) {
            LOG.error("Error running the SQL script:\n" + sql, sqle);
            throw sqle;
        } catch (IOException ioe) {
            LOG.error("Error while writing into the file:" + writtenFile.getName(), ioe);
            throw ioe;
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (conn != null) {
                conn.close();
            }
            if (writer != null) {
                writer.close();
            }
        }

        if (compressed) {
            File compressedFile = new File(writtenFile.getAbsolutePath() + ".gz");
            compressGzipFile(writtenFile, compressedFile);
            LOG.info("Extracted File compressed into " + compressedFile.getAbsoluteFile());
        }
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
    public void extractData(String extractDataFileName, int maxRowNumber, boolean compressed) throws Exception {

        LOG.info("Enter method extractData(String extractDataFileName, int maxRowNumber, boolean compressed)");
        LOG.info(String.format("extractDataFileName:%s , maxRowNumber: %d, compressed:%s", extractDataFileName,
                maxRowNumber, compressed));

        if (extractDataFileName == null || extractDataFileName.trim().length() == 0) {
            throw new IllegalArgumentException("The extractDataFileName should not be empty");
        }

        File writtenFile = new File("." + File.separator + EXPORT_DIR + File.separator + extractDataFileName);

        extractData(writtenFile, maxRowNumber, compressed);

        LOG.info("Exist method extractData(String extractDataFileName, int maxRowNumber, boolean compressed)");
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
    public void extractData(String extractDateFileName, boolean compressed) throws Exception {
        this.extractData(extractDateFileName, DEFAULT_MAX_ROW, compressed);
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
    public void extractData(File writtenFile, boolean compressed) throws Exception {
        this.extractData(writtenFile, DEFAULT_MAX_ROW, compressed);
    }
}
