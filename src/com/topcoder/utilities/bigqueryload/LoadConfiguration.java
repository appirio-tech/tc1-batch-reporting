/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.utilities.bigqueryload;

import java.util.List;

/**
 * <p>
 * The configuration bean for the BigQueryUtility.
 * </p>
 *
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class LoadConfiguration {
    /**
     * Whether to create a separate log file each running of the utility.
     */
    private boolean logFilePerRunning;

    /**
     * The job configuration list.
     */
    private List<JobConfiguration> jobs;

    /**
     * Gets the flag of logFilePerRunning
     *
     * @return the flag of logFilePerRunning
     */
    public boolean isLogFilePerRunning() {
        return logFilePerRunning;
    }

    /**
     * Sets the the flag of logFilePerRunning
     *
     * @param logFilePerRunning the flag of logFilePerRunning
     */
    public void setLogFilePerRunning(boolean logFilePerRunning) {
        this.logFilePerRunning = logFilePerRunning;
    }

    /**
     * Gets the job configuration list.
     *
     * @return the job configuration list.
     */
    public List<JobConfiguration> getJobs() {
        return jobs;
    }

    /**
     * Sets the job configuration list.
     *
     * @param jobs the job configuration list.
     */
    public void setJobs(List<JobConfiguration> jobs) {
        this.jobs = jobs;
    }
}
