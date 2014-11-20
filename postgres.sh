#!/bin/sh

source /home/coder/.postgres_env

POSTGRES_DIR=/home/coder/postgres

nohup $ANT_HOME/bin/ant -buildfile ${POSTGRES_DIR}/build.xml loadPostgres  >> ${POSTGRES_DIR}/postgres_load.log 2>&1 &