#!/bin/sh

source /home/coder/.bigquery_env

BIGQUERY_DIR=/home/coder/bigquery

nohup $ANT_HOME/bin/ant -buildfile ${BIGQUERY_DIR}/build.xml load  >> ${BIGQUERY_DIR}/bigquery_load.log 2>&1 & 