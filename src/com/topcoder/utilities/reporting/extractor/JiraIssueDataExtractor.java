/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.utilities.reporting.extractor;

import com.topcoder.utilities.reporting.extractor.generator.ModelContentGenerator;
import com.topcoder.utilities.reporting.model.JiraIssue;
import com.topcoder.utilities.reporting.util.Utils;
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
 * <p>
 * Version 1.1 (Module Assembly - reporting tool refactor and load user json data to Postgres)
 * <ul>
 *     <li>Added constructor to take the model generator as argument</li>
 *     <li>Refactor 3 extract data methods into the base class</li>
 *     <li>Added {@link #getDefaultMaxRowNumber()}</li>
 * </ul>
 * </p>
 *
 * @author Veve, TCSASSEMBLER
 * @version 1.1
 */
public class JiraIssueDataExtractor extends BaseTopCoderReportingDataExtractor {
    /**
     * The logger.
     */
    private static final Logger LOG = Logger.getLogger(JiraIssueDataExtractor.class.getName());

    /**
     * The sql query to get the jira issue data from the database.
     */
    private static final String JIRA_ISSUE_DATA_SQL = "extractorSQL/jiraIssueData.sql";

    /**
     * The default max row number the extraction query reads each time.
     */
    private static final int DEFAULT_MAX_ROW = 10000;

    /**
     * The default fetch size of the JDBC SQL statement
     */
    private static final int DEFAULT_FETCH_SIZE = 2000;

    /**
     * Creates JiraIssueDataExtractor with the specified ModelContentGenerator
     *
     * @param generator the generator.
     *
     * @since 1.1
     */
    public JiraIssueDataExtractor(ModelContentGenerator generator) {
        super(generator);
    }

    /**
     * Gets the default max row number
     *
     * @return the default max row number
     *
     * @since 1.1
     */
    @Override
    public int getDefaultMaxRowNumber() {
        return DEFAULT_MAX_ROW;
    }

    /**
     * Extracts the user data from topcoder database into json file accepted by Google Big Query.
     *
     * @param writtenFile  the file to write to
     * @param maxRowNumber the max row number
     * @param compressed   whether to compress to gzip
     * @throws Exception if any error
     */
    public int extractData(File writtenFile, int maxRowNumber, boolean compressed) throws Exception {
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

        String sqlQueryTemplate = Utils.readFileContentAsString(JIRA_ISSUE_DATA_SQL);
        Connection conn = getTcsDWConnection();
        Statement statement = conn.createStatement();
        statement.setFetchSize(DEFAULT_FETCH_SIZE);
        int skipCount = 0;
        ResultSet resultSet = null;
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
                    writer.write(generator.generateContent(issue));
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
            Utils.compressGzipFile(writtenFile, compressedFile);
            LOG.info("Extracted File compressed into " + compressedFile.getAbsoluteFile());
        }

        return skipCount;
    }



}
