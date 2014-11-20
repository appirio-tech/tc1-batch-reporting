/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.utilities.reporting.job;

/**
 * <p>
 * The configuration bean for the JobRunner.
 * </p>
 *
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class AbstractRunConfig {
    /**
     * Whether to create a separate log file each running of the utility.
     */
    private boolean logFilePerRunning;

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

}
