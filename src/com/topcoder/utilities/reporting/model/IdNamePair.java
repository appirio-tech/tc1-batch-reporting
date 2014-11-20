/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.utilities.reporting.model;

/**
 * <p>
 * The java bean to store the ID and Name
 * </p>
 *
 * @author GreatKevin
 * @version 1.0
 */
public class IdNamePair {

    /**
     * The id
     */
    private long id;

    /**
     * The name.
     */
    private String name;

    /**
     * Empty constructor.
     */
    public IdNamePair() {
        // empty
    }

    /**
     * Constructor.
     *
     * @param id   the id.
     * @param name the name.
     */
    public IdNamePair(long id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Gets the id.
     *
     * @return the id.
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the id.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets the name.
     *
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the name.
     */
    public void setName(String name) {
        this.name = name;
    }
}
