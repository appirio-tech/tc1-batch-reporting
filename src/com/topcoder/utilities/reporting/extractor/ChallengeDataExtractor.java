/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.utilities.reporting.extractor;

import com.topcoder.utilities.reporting.extractor.generator.ModelContentGenerator;
import com.topcoder.utilities.reporting.model.Challenge;
import com.topcoder.utilities.reporting.model.IdNamePair;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * This class is the TopCoderReportingDataExtractor implementation which extracts the challenge related data.
 * </p>
 *
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
 * @author GreatKevin, TCSASSEMBLER
 * @version 1.1
 */
public class ChallengeDataExtractor extends BaseTopCoderReportingDataExtractor {

    /**
     * The logger.
     */
    private static final Logger LOG = Logger.getLogger(ChallengeDataExtractor.class.getName());

    /**
     * The sql query to get the challenge data from the database.
     */
    private static final String CHALLENGE_DATA_SQL = "extractorSQL/challengeData.sql";

    /**
     * The default max row number the extraction query reads each time.
     */
    private static final int DEFAULT_MAX_ROW = 5000;

    /**
     * The default fetch size of the JDBC SQL statement
     */
    private static final int DEFAULT_FETCH_SIZE = 1000;

    /**
     * The Empty List of <code>IdNamePair</code>
     */
    private static final List<IdNamePair> EMPTY_IDNAME_LIST = new ArrayList<IdNamePair>();

    /**
     * The query to get the platform data of all challenges.
     */
    private static final String QUERY_ALL_PLATFORMS = "SELECT project_id AS challenge_id, \n" +
            "project_platform_id as id,\n" +
            "name\n" +
            "FROM project_platform";

    /**
     * The query to get the technology data of all challenges.
     */
    private static final String QUERY_ALL_TECHNOLOGIES = "SELECT project_id AS challenge_id, \n" +
            "project_technology_id as id,\n" +
            "name\n" +
            "FROM project_technology";

    /**
     * The map to store the retrieved technology data.
     */
    private Map<Long, List<IdNamePair>> challengeTechnologies;

    /**
     * The map to store the retrieved platform data.
     */
    private Map<Long, List<IdNamePair>> challengePlatforms;


    /**
     * Creates ChallengeDataExtractor with the specified ModelContentGenerator
     *
     * @param generator the generator.
     *
     * @since 1.1
     */
    public ChallengeDataExtractor(ModelContentGenerator generator) {
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
     * Helper method to build the map of challenge ID to a List of IdNamePair. It will be used to build the map
     * for challenge technologies and platforms.
     *
     * @param rs the result set
     * @return the built data map
     * @throws Exception if any error occurs.
     */
    private Map<Long, List<IdNamePair>> buildIdNamePairMap(ResultSet rs) throws Exception {

        Map<Long, List<IdNamePair>> resultMap = new HashMap<Long, List<IdNamePair>>();

        while (rs.next()) {
            long challengeId = rs.getLong("challenge_id");
            long id = rs.getLong("id");
            String name = rs.getString("name");

            if (!resultMap.containsKey(challengeId)) {
                resultMap.put(challengeId, new ArrayList<IdNamePair>());
            }

            resultMap.get(challengeId).add(new IdNamePair(id, name));
        }

        return resultMap;
    }

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

        if (maxRowNumber <= 0) {
            throw new IllegalArgumentException("The max row number should be positive");
        }


        long time = System.currentTimeMillis();
        long startTime = time;

        String sqlQueryTemplate = Utils.readFileContentAsString(CHALLENGE_DATA_SQL);
        Connection conn = getTcsDWConnection();
        Statement statement = conn.createStatement();
        statement.setFetchSize(DEFAULT_FETCH_SIZE);
        int skipCount = 0;
        ResultSet resultSet = null;
        ResultSet techResultSet = null;
        ResultSet platformResultSet = null;

        ChallengeDataExtractor.ResultSetMapper<Challenge> rsm = new ChallengeDataExtractor.ResultSetMapper<Challenge>();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(writtenFile), "UTF8"));
        String sql = "";

        try {

            // build the technology map
            techResultSet = statement.executeQuery(QUERY_ALL_TECHNOLOGIES);

            this.challengeTechnologies = buildIdNamePairMap(techResultSet);

            // build the platform map
            platformResultSet = statement.executeQuery(QUERY_ALL_PLATFORMS);

            this.challengePlatforms = buildIdNamePairMap(platformResultSet);


            // get all the challenge data
            while (true) {
                sql = sqlQueryTemplate.replace("@skip@", String.valueOf(skipCount));
                sql = sql.replace("@maxRow@", String.valueOf(maxRowNumber));

                resultSet = statement.executeQuery(sql);

                List<Challenge> challenges = rsm.mapRersultSetToObject(resultSet, Challenge.class);

                if (challenges == null || challenges.size() == 0) {
                    // no more result, load is done
                    LOG.info(String.format(
                            "The whole challenge data extraction is done, total records: %d, total time: %.1f seconds",
                            skipCount, (System.currentTimeMillis() - startTime) / 1000.0));
                    break;
                }

                for (Challenge c : challenges) {
                    // add technology
                    if (this.challengeTechnologies.containsKey(c.getChallenge_id())) {
                        c.setChallenge_technology_name(this.challengeTechnologies.get(c.getChallenge_id()));
                    } else {
                        c.setChallenge_technology_name(EMPTY_IDNAME_LIST);
                    }

                    // add platform
                    if (this.challengePlatforms.containsKey(c.getChallenge_id())) {
                        c.setChallenge_platform_name(this.challengePlatforms.get(c.getChallenge_id()));
                    } else {
                        c.setChallenge_platform_name(EMPTY_IDNAME_LIST);
                    }

                    writer.write(generator.generateContent(c));
                    writer.newLine();
                }

                writer.flush();

                LOG.info(String.format("SKIP : %d, LIMIT: %d, Time Taken: %.1f seconds", skipCount, maxRowNumber,
                        (System.currentTimeMillis() - time) / 1000.0));
                time = System.currentTimeMillis();

                skipCount += challenges.size();
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
            if (techResultSet != null) {
                techResultSet.close();
            }
            if (platformResultSet != null) {
                platformResultSet.close();
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
            LOG.info("Extracted File compressed into " + compressedFile.getName());
        }

        return skipCount;
    }
}
