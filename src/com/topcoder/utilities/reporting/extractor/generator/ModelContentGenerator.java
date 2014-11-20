/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.utilities.reporting.extractor.generator;

/**
 * <p>
 * This interface defines the content generator to generate content string for a model object.
 * </p>
 *
 * @author TCSASSEMBLER
 * @version 1.0 (Module Assembly - reporting tool refactor and load user json data to Postgres)
 */
public interface ModelContentGenerator {
    /**
     * Generate content string for the specified model object.
     *
     * @param obj the model object.
     * @return the generated string.
     */
    public String generateContent(Object obj);
}
