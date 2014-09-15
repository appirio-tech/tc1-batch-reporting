/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.utilities.bigqueryload.extractor;

import java.io.File;

/**
 * <p>
 * The demo to call the UserDataExtractor to generate the data.
 * </p>
 *
 * @author GreatKevin
 * @version 1.0
 */
public class Demo {

    /**
     * The main entry of the demo.
     *
     * @param args the args
     * @throws Exception if any error happens.
     */
    public static void main(String[] args) throws Exception {

        TopCoderBigQueryExtractor extractor = new UserDataExtractor();

        extractor.extractData("user_data.json", 100, true);

        extractor.extractData(new File("user_data_2.json"), 100, true);

        extractor.extractData("user_data_3.json", false);

        extractor.extractData(new File("user_data_4.json"), true);
    }
}
