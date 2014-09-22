/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.utilities.bigqueryload.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

/**
 * <p>
 * The small tool to insert description (blob text) data for jira_issue test data.
 * </p>
 *
 * @author Veve
 * @version 1. (TopCoder Big Query Data Extractor - Jira Issue Data)
 */
public class InsertDescriptionForJiraIssue {

    private static Connection tcsDWConnection;


    /**
     * The JDBC driver name of informix database.
     */
    private static String sDriverName = "com.informix.jdbc.IfxDriver";

    /**
     * The variable to store the tcs_dw connection string configuration.
     */
    private static String TCS_DW_JDBC_URL = "";

    /**
     * The properties file name.
     */
    private static final String BIG_QUERY_DATA_EXTRACTOR_PROPERTIES = "BigQueryDataExtractor.properties";

    /**
     * The property key for reading the tcs_dw database connection string for the configuration file.
     */
    public static final String TCS_DW_CONNECTION_KEY = "tcs_dw_connection_str";

    private static String DESCRIPTION_DATA =
            "This bug is to update 2 items in the REMIX: Flat Panel Selection experience.\n\nYou will need to " +
                    "update the provided HTML/CSS files.\n\n----------------------\nRequirements\n----------------------\n\n- This fix is only for the " +
                    "graphic changes listed. The experience, functionality and basic code structure must remain intact.\n\n-----" +
                    "-----------------\nHTML/CSS Requirements\n----------------------\n- Your HTML code must be XHTML " +
                    "1.0 Strict compliant\n- Validate your code - please comment your reason for any validation errors.\n" +
                    "- Use CSS to space out objects, not clear/transparent images (GIFs or PNGs) and use proper structural CSS " +
                    "to lay out your page. Only use table tags for tables of data/information and not for page layout.\n-" +
                    " No inline CSS styles - all styles must be placed in an external stylesheet.\n- Use semantically correct " +
                    "tags - use H tags for headers, etc. Use strong and em tags instead of bold and italic tags.\n- Element and Attribute" +
                    " names should be in lowercase and use a \"-\" or camel naming to separate multiple-word classes (i.e.. \"main-content\", " +
                    "or \"mainContent\n- Label all CSS, Javascript, or HTML hacks with explanations so others will understand.\n\n\n---" +
                    "-------------------\nBrowsers\n----------------------\nYour code must render properly in all browsers" +
                    " listed:\n- IE6 (primary)\n- Firefox for both Mac and PC (primary)\n- IE7\n- Safari\n- Chrome" +
                    "\n\n----------------------\nRequired Changes\n----------------------\n1. The \"close\" button" +
                    " throughout needs to be replaced with the standard red X graphic used on bestbuy.com\n\n2. The main yellow" +
                    " header bars must be changed from their current yellow. You must show two (2) versions of this. One in gray" +
                    " and one in Best Buy Blue. Both should remain gradients. Please see attached style guides for specific " +
                    "color information. \n\n\n----------------------\nDeliverables\n----------------------\n\n1. Provide " +
                    "JPG or PNG screens that show the color options to this bug race.\n\n2. If you are chosen as the winner, " +
                    "you must provide the HTML/CSS files completed as well as and PSD files you used for your fix.\n\n\n\n*************\nLet me" +
                    " know if you have any questions.\n\nIf you choose to submit an encrypted zip files, please email your " +
                    "password to chamlin@topcoder.com\n\nPlease name your submission with your handle and version number" +
                    " (ex: silvasurvas-v1.zip)\n\nThanks and good luck!\n\n******** ";

    /**
     * Gets the database connection to the tcs_dw database.
     *
     * @return database connection.
     * @throws Exception if there is any error.
     */
    protected static Connection getTcsDWConnection() throws Exception {
        if (tcsDWConnection == null || tcsDWConnection.isClosed()) {
            // need to create a new connection
            Class.forName(sDriverName);


            if (TCS_DW_JDBC_URL.trim().length() == 0) {
                // URL is not loaded, load URL
                Properties config = new Properties();
                config.load(
                        InsertDescriptionForJiraIssue.class.getClassLoader().getResourceAsStream(
                                BIG_QUERY_DATA_EXTRACTOR_PROPERTIES));
                TCS_DW_JDBC_URL = config.getProperty(TCS_DW_CONNECTION_KEY);
            }

            tcsDWConnection = DriverManager.getConnection(TCS_DW_JDBC_URL);
        }

        return tcsDWConnection;
    }

    /**
     * Insert blob text data for description via JDBC.
     *
     * @param args the arguments.
     * @throws Exception if there is any error.
     */
    public static void main(String[] args) throws Exception {
        // insert 'description'
        String updateQuery = "UPDATE jira_issue SET description = ? WHERE jira_issue_id = ?";
        String selectQuery = "SELECT jira_issue_id FROM jira_issue WHERE description IS NULL";

        Connection con = getTcsDWConnection();
        PreparedStatement selectStatement = null;
        PreparedStatement updateStatement = null;
        ResultSet selectRS = null;
        ResultSet updateRS = null;

        try {
            selectStatement = con.prepareStatement(selectQuery);
            updateStatement = con.prepareStatement(updateQuery);

            selectRS = selectStatement.executeQuery();

            while (selectRS.next()) {
                long jira_issue_id = selectRS.getLong("jira_issue_id");

                String desToInsert = "ISSUE #" + jira_issue_id + " Description->" + DESCRIPTION_DATA;

                updateStatement.setObject(1, desToInsert.getBytes());
                updateStatement.setLong(2, jira_issue_id);

                updateStatement.executeUpdate();
            }

        } finally {
            if (selectRS != null) {
                selectRS.close();
            }
            if (updateRS != null) {
                updateRS.close();
            }
            if (selectStatement != null) {
                selectStatement.close();
            }
            if (updateStatement != null) {
                updateStatement.close();
            }
            if (con != null) {
                con.close();
            }
        }

    }

}
