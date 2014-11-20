/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.utilities.reporting.extractor;

import com.topcoder.utilities.reporting.extractor.generator.ModelContentGenerator;
import com.topcoder.utilities.reporting.model.Notification;
import com.topcoder.utilities.reporting.model.UserNotification;
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
import java.util.ArrayList;

/**
 * <p>
 * Extract user notification data from topcoder_dw:user_notification for Google Big Query
 * </p>
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
public class UserNotificationDataExtractor extends BaseTopCoderReportingDataExtractor {
    /**
     * The logger.
     */
    private static final Logger LOG = Logger.getLogger(UserNotificationDataExtractor.class.getName());

    /**
     * The sql query to get the user notification data from the database.
     */
    private static final String USER_NOTIFICATION_DATA_SQL = "extractorSQL/userNotificationData.sql";

    /**
     * The default fetch size of the JDBC SQL statement
     */
    private static final int DEFAULT_FETCH_SIZE = 50000;

    /**
     * Creates UserNotificationDataExtractor with the specified ModelContentGenerator
     *
     * @param generator the generator.
     *
     * @since 1.1
     */
    public UserNotificationDataExtractor(ModelContentGenerator generator) {
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
        return Integer.MAX_VALUE;
    }


    /**
     * Extracts the user notification data from topcoder database into json file accepted by Google Big Query.
     *
     * @param writtenFile  the file to write to the file to written to
     * @param maxRowNumber the max row number
     * @param compressed   whether to compress to gzip
     * @throws Exception if any error
     */
    @Override
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

        long time = System.currentTimeMillis();
        long startTime = time;

        String sqlQueryTemplate = Utils.readFileContentAsString(USER_NOTIFICATION_DATA_SQL);
        Connection conn = getTopcoderDWConnection();
        Statement statement = conn.createStatement();
        statement.setFetchSize(DEFAULT_FETCH_SIZE);
        ResultSet resultSet = null;
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(writtenFile), "UTF8"));

        // no skip for user notification query
        String sql = sqlQueryTemplate;

        int recordCount = 0;
        int userCount = 0;

        try {

            resultSet = statement.executeQuery(sql);

            long currentUserId = -1;
            UserNotification userNotification = null;

            // get all the user userNotification data
            while (resultSet.next()) {

                long userId = resultSet.getLong("user_id");
                String name = resultSet.getString("name");
                String status = resultSet.getString("status");
                long notifyTypeId = resultSet.getLong("notify_type_id");
                String notifyTypeDesc = resultSet.getString("notify_type_desc");

                Notification notification = new Notification();
                notification.setName(name);
                notification.setStatus(status);
                notification.setType(notifyTypeId);
                notification.setType_desc(notifyTypeDesc);

                recordCount++;

                if (currentUserId != userId) {

                    if (userNotification != null) {
                        // write user notification of previous user first
                        writer.write(generator.generateContent(userNotification));
                        writer.newLine();

                        userCount++;
                    }

                    currentUserId = userId;
                    userNotification = new UserNotification();
                    userNotification.setUser_id(userId);
                    userNotification.setNotifications(new ArrayList<Notification>());
                }

                userNotification.getNotifications().add(notification);
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

        // no more result, load is done
        LOG.info(String.format(
                "The whole user notification data extraction is done, total records: %d, total users: %d, total time: %.1f seconds",
                recordCount, userCount, (System.currentTimeMillis() - startTime) / 1000.0));

        if (compressed) {
            File compressedFile = new File(writtenFile.getAbsolutePath() + ".gz");
            Utils.compressGzipFile(writtenFile, compressedFile);
            LOG.info("Extracted File compressed into " + compressedFile.getName());
        }

        return recordCount;
    }
}
