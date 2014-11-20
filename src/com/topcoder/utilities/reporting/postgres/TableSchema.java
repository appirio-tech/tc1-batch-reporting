/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.utilities.reporting.postgres;

import java.util.List;

/**
 * <p>
 * The table schema of the postgres table.
 * </p>
 *
 * @author TCSASSEMBLER
 * @version 1.0 (Module Assembly - reporting tool refactor and load user json data to Postgres)
 */
public class TableSchema {

    /**
     * The schema which is consisted of columns.
     */
    private List<Column> schema;

    /**
     * Gets the list of columns.
     *
     * @return the list of columns
     */
    public List<Column> getSchema() {
        return schema;
    }

    /**
     * Sets the list of columns
     *
     * @param schema the list of columns.
     */
    public void setSchema(List<Column> schema) {
        this.schema = schema;
    }

    /**
     * Static inner class to presents a table column
     *
     * @author TCSASSEMBLER
     * @version 1.0 (Module Assembly - reporting tool refactor and load user json data to Postgres)
     */
    public static class Column {
        /**
         * The column name.
         */
        private String name;

        /**
         * The column type.
         */
        private String type;

        /**
         * Gets the column name.
         *
         * @return the column name.
         */
        public String getName() {
            return name;
        }

        /**
         * Sets the column name.
         *
         * @param name the column name.
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Gets the column type.
         *
         * @return the column type.
         */
        public String getType() {
            return type;
        }

        /**
         * Sets the column type.
         *
         * @param type the column type.
         */
        public void setType(String type) {
            this.type = type;
        }
    }
}
