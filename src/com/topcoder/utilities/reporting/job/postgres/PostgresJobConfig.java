/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.utilities.reporting.job.postgres;

import com.topcoder.utilities.reporting.job.AbstractJobConfig;
import com.topcoder.utilities.reporting.postgres.TableLoaderConfig;

/**
 * <p>
 * The postgres job configuration.
 * </p>
 *
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class PostgresJobConfig extends AbstractJobConfig {

    /**
     * The table loader config.
     */
    private TableLoaderConfig loaderConfig;

    /**
     * Gets the table loader config.
     *
     * @return the table loader config.
     */
    public TableLoaderConfig getLoaderConfig() {
        return loaderConfig;
    }

    /**
     * Sets the table loader config.
     *
     * @param loaderConfig the table loader config.
     */
    public void setLoaderConfig(TableLoaderConfig loaderConfig) {
        this.loaderConfig = loaderConfig;
    }
}
