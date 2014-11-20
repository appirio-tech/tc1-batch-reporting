/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.utilities.reporting.job.bigquery;

import com.topcoder.utilities.reporting.job.AbstractRunConfig;

import java.util.List;

/**
 * <p>The runner tool configuration for the big query runner</p>
 *
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class BigQueryRunConfig extends AbstractRunConfig {
    /**
     * The job configuration list.
     */
    private List<BIgQueryJobConfig> jobs;

    /**
     * Gets the configured jobs.
     *
     * @return the configured jobs.
     */
    public List<BIgQueryJobConfig> getJobs() {
        return jobs;
    }

    /**
     * Sets the configured jobs.
     *
     * @param jobs the configured jobs.
     */
    public void setJobs(List<BIgQueryJobConfig> jobs) {
        this.jobs = jobs;
    }
}
