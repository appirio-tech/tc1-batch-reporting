/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.utilities.reporting.postgres;

import com.google.gson.Gson;
import com.topcoder.utilities.reporting.util.Utils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * <p>
 * This class provides the loader for loading data from extracted json data to postgres database.
 * </p>
 *
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class TableLoader {

    /**
     * The logger.
     */
    private static final Logger LOG = Logger.getLogger(TableLoader.class.getName());

    /**
     * The variable to store the postgres JDBC connection string configuration.
     */
    private static String JDBC_URL = "";

    /**
     * The error message buffer.
     */
    private static StringBuffer ERROR_MESSAGE = new StringBuffer(128);

    /**
     * The postgres driver name.
     */
    private static final String DRIVER_NAME = "org.postgresql.Driver";

    /**
     * The default loader property file name.
     */
    private static final String PROPERTIES_FILE = "postgres/TableLoader.properties";

    /**
     * The key to read jdbc property value from the properties file.
     */
    private static final String JDBC_URL_PROPERTY_KEY = "jdbc_connection_str";

    /**
     * The batch size to insert the records on batch.
     */
    private static final int BATCH_SIZE = 2000;

    /**
     * The database connection.
     */
    protected Connection connection;

    /**
     * The property file name.
     */
    private final String propertyFile;

    /**
     * Default constructor. Set to the default property file name.
     */
    public TableLoader() {
        this.propertyFile = PROPERTIES_FILE;
    }

    /**
     * Constructs with the specified property file name.
     *
     * @param propertyFile the property file name.
     */
    public TableLoader(String propertyFile) {
        Utils.checkNullEmptyString(propertyFile, "propertyFile");
        this.propertyFile = propertyFile;
    }

    /**
     * Gets the database connection to the postgres database.
     *
     * @return database connection.
     * @throws Exception if there is any error.
     */
    protected Connection getDBConnection() throws Exception {
        if (connection == null || connection.isClosed()) {
            // need to create a new connection
            checkDriver();

            try {
                Class.forName(DRIVER_NAME);
            } catch (ClassNotFoundException e) {
                ERROR_MESSAGE.setLength(0);
                ERROR_MESSAGE.append("DB driver load failed. ");
                ERROR_MESSAGE.append("Cannot continue.\n");
                ERROR_MESSAGE.append(e.getMessage());
                LOG.error(ERROR_MESSAGE.toString(), e);
                throw e;
            }

            if (JDBC_URL.trim().length() == 0) {
                // URL is not loaded, load URL
                Properties config = new Properties();
                config.load(this.getClass().getClassLoader().getResourceAsStream(propertyFile));
                JDBC_URL = config.getProperty(JDBC_URL_PROPERTY_KEY);
            }

            connection = DriverManager.getConnection(JDBC_URL);
        }

        return connection;
    }

    /**
     * Loads the data from extracted file into the postgres table according to the specified table loader config.
     *
     * @param config   the table loader config
     * @param dataFile the data file to load
     * @throws Exception if any error.
     */
    public void loadTable(TableLoaderConfig config, String dataFile) throws Exception {

        Connection con = getDBConnection();

        try {
            LOG.info("Enter method loadTable(TableLoaderConfig config, String dataFile)");
            LOG.info(String.format(
                    "config.setupScript:%s , \nconfig.clearScript:%s, \nconfig.insertScript:%s, \ndataFile:%s",
                    config.getSetupScript(), config.getClearScript(), config.getInsertScript(), dataFile));
            LOG.info("Start to load data for table:" + config.getTableName());
            long time = System.currentTimeMillis();

            // setup the table
            LOG.info("Setup the table:" + config.getTableName());
            setupTable(config.getSetupScript());
            LOG.info("Finished set up the table:" + config.getTableName());

            LOG.info("Clear the existing data in the table:" + config.getTableName());
            // clear all the data in the table
            clearTable(config.getClearScript());
            LOG.info("Finished clearing the data in the table:" + config.getTableName());

            // insert data into the table
            insertTable(config, dataFile);


            LOG.info(String.format("Load data for table %s took %d seconds", config.getTableName(),
                    (System.currentTimeMillis() - time) / 1000));
            LOG.info("Exist method loadTable(TableLoaderConfig config, String dataFile)");
        } finally {
            Utils.close(con);
        }
    }

    /**
     * Creates the table if the table does not exsit
     *
     * @param setupScript the script file to setup the table.
     * @throws Exception if any error.
     */
    private void setupTable(String setupScript) throws Exception {
        simpleSQLExecution(setupScript);
    }

    /**
     * Clears the table data before loading the new data.
     *
     * @param clearScript the script file to clear the table contents.
     * @throws Exception if any error.
     */
    private void clearTable(String clearScript) throws Exception {
        simpleSQLExecution(clearScript);
    }

    /**
     * Inserts the data into table.
     *
     * @param config the table load config object
     * @param dataFile     the data file
     * @throws Exception if any error
     */
    private void insertTable(TableLoaderConfig config, String dataFile) throws Exception {

        LOG.info(String.format("Start loading data for the table %s, total records number %n", config.getTableName(), config.getTotalRecordsNumber()));

        Connection c = getDBConnection();

        Gson gson = new Gson();
        TableSchema tableSchema = gson.fromJson(
                new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(config.getSchema())),
                TableSchema.class);

        c.setAutoCommit(false);
        PreparedStatement s = null;
        BufferedReader reader = null;

        try {
            s = c.prepareStatement(Utils.readFileContentAsString(config.getInsertScript()));

            reader = new BufferedReader(
                    new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(dataFile)));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String line;
            int batchCount = 0;
            Map<String, Object> userData = new HashMap<String, Object>();

            int loadedRecordsCount = 0;

            while ((line = reader.readLine()) != null) {

                if (line.trim().length() == 0) {
                    // ignore empty line
                    continue;
                }

                userData = (Map<String, Object>) gson.fromJson(line, userData.getClass());

                for (int i = 0, len = tableSchema.getSchema().size(); i < len; ++i) {
                    TableSchema.Column column = tableSchema.getSchema().get(i);

                    Utils.checkNullEmptyString(column.getName(), "name in schema");
                    Utils.checkNullEmptyString(column.getType(), "type in schema");

                    Object data = userData.get(column.getName());

                    if (!userData.containsKey(column.getName()) || data == null) {
                        s.setObject(i + 1, null);
                    } else if (column.getType().equalsIgnoreCase("integer")) {
                        s.setInt(i + 1, (int) Double.parseDouble(data.toString()));
                    } else if (column.getType().equalsIgnoreCase("string")) {
                        s.setString(i + 1, data.toString());
                    } else if (column.getType().equalsIgnoreCase("float")) {
                        s.setDouble(i + 1, Double.parseDouble(data.toString()));
                    } else if (column.getType().equalsIgnoreCase("timestamp")) {
                        s.setTimestamp(i + 1, new Timestamp(dateFormat.parse(data.toString()).getTime()));
                    }
                }

                s.addBatch();
                batchCount++;

                if (batchCount >= BATCH_SIZE) {
                    loadedRecordsCount += batchCount;
                    s.executeBatch();
                    c.commit();
                    batchCount = 0;

                    LOG.info(String.format("Loading in progress, finished percentage : %.2f%%\n", (loadedRecordsCount) * 100.0 / config.getTotalRecordsNumber()));

                }
            }

            loadedRecordsCount += batchCount;
            s.executeBatch();
            c.commit();

            LOG.info("Loading data finished for table:" + loadedRecordsCount);

        } catch (SQLException sqle) {
            sqle.getNextException().printStackTrace(System.err);
            throw sqle;
        } finally {
            Utils.close(reader);
            Utils.close(s);
            // set auto commit back
            c.setAutoCommit(true);
        }

    }

    /**
     * Helper method to execute a sql.
     *
     * @param sqlScript the sql script file to load the sql from
     * @throws Exception if any error.
     */
    private void simpleSQLExecution(String sqlScript) throws Exception {
        Connection c = getDBConnection();

        Statement s = null;

        try {
            s = c.createStatement();

            s.execute(Utils.readFileContentAsString(sqlScript));

        } finally {
            Utils.close(s);
        }
    }

    /**
     * Checks whether the JDBC driver class can be loaded.
     */
    private static void checkDriver() {
        try {
            Class.forName(DRIVER_NAME);
        } catch (Exception ex) {
            ERROR_MESSAGE.setLength(0);
            ERROR_MESSAGE.append("Unable to load driver ");
            ERROR_MESSAGE.append(DRIVER_NAME);
            ERROR_MESSAGE.append(". Cannot continue.");
            fatal_error();
        }
    }

    /**
     * Prints out the fatal error mesage directly to the running console.
     */
    private static void fatal_error() {
        System.out.println("*******************************************");
        System.out.println("FAILURE: " + ERROR_MESSAGE.toString());
        System.out.println("*******************************************");
        System.exit(-1);
    }

}
