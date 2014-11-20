/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.utilities.reporting.job.postgres;

import com.topcoder.utilities.reporting.job.AbstractRunConfig;

import java.util.List;

/**
 * <p>The runner configuration for the postgres job runner.</p>
 *
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class PostgresRunConfig extends AbstractRunConfig {

    /**
     * The list of postgres job
     */
    private List<PostgresJobConfig> jobs;

    /**
     * Gets the list of postgres job.
     *
     * @return the list of postgres job.
     */
    public List<PostgresJobConfig> getJobs() {
        return jobs;
    }

    /**
     * Sets the list of postgres job.
     *
     * @param jobs the list of postgres job.
     */
    public void setJobs(List<PostgresJobConfig> jobs) {
        this.jobs = jobs;
    }
}
