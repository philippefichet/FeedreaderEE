#!/bin/bash
if [ -n "$WILDFLY_HOME" ]
then
  $WILDFLY_HOME/bin/jboss-cli.sh --connect --commands='/subsystem=datasources/data-source=feedreader:add(driver-name="hsqldb",connection-url="jdbc:hsqldb:file:~/.feedreader/hsqldb",user-name="feedreader",password="a5tY6d4u7",jndi-name="java:jboss/datasources/feedreader")'
else
  echo "WILDFLY_HOME must not empty"
  exit 1
fi



