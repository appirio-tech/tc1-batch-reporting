/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.utilities.reporting.extractor.generator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * <p>
 * The <code>ModelContentGenerator</code> implementation, it uses json format to represent the model content.
 * </p>
 *
 * @author TCSASSEMBLER
 * @version 1.0 (Module Assembly - reporting tool refactor and load user json data to Postgres)
 */
public class JsonContentGenerator implements ModelContentGenerator {

    /**
     * The gson object to serlialize the object to json.
     */
    private static Gson GSON = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    /**
     * Generates the json content for the specified object.
     *
     * @param obj the model object to generate content with
     * @return the generated content string.
     */
    @Override
    public String generateContent(Object obj) {
        return GSON.toJson(obj);
    }
}
