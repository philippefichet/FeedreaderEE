FROM ubuntu:16.04
MAINTAINER Philippe FICHET <philippe.fichet@laposte.net>

# Install wget
RUN apt-get -y update
RUN apt-get -y upgrade
RUN apt-get -y install curl wget gzip zip

# Prepare Java 8
RUN apt-get -y install openjdk-8-jdk

# Add the WildFly distribution to /opt, and make wildfly the owner of the extracted tar content
# Make sure the distribution is available from a well-known place
# Set the WILDFLY_VERSION env variable
ENV WILDFLY_VERSION 10.1.0.Final
ENV WILDFLY_HOME /opt/wildfly/wildfly-$WILDFLY_VERSION
ENV WILDFLY_ADMIN_USER admin
ENV WILDFLY_ADMIN_PASSWORD megapassword
RUN mkdir -p /opt/wildfly/
RUN cd /opt/wildfly/ && \
    wget http://download.jboss.org/wildfly/$WILDFLY_VERSION/wildfly-$WILDFLY_VERSION.tar.gz -O - | \
    gunzip | \
    tar xvf -

# Configure admin
RUN $WILDFLY_HOME/bin/add-user.sh --silent -u $WILDFLY_ADMIN_USER -p $WILDFLY_ADMIN_PASSWORD -e
RUN $WILDFLY_HOME/bin/add-user.sh --silent -a -u $WILDFLY_ADMIN_USER -p $WILDFLY_ADMIN_PASSWORD -e

#Install HSQLDB to wildfly
ENV HSQLDB_VERSION 2.3.4
RUN wget -c "https://repo1.maven.org/maven2/org/hsqldb/hsqldb/$HSQLDB_VERSION/hsqldb-$HSQLDB_VERSION.jar" -O /tmp/hsqldb-$HSQLDB_VERSION.jar
RUN moduleXmlFile=$( echo "$WILDFLY_HOME/modules/system/layers/base/org/hsqldb/main/module.xml" ) && \
    mkdir -p $WILDFLY_HOME/modules/system/layers/base/org/hsqldb/main && \
    cp /tmp/hsqldb-$HSQLDB_VERSION.jar $WILDFLY_HOME/modules/system/layers/base/org/hsqldb/main/hsqldb-$HSQLDB_VERSION.jar && \
    echo '<?xml version="1.0" encoding="UTF-8"?>' > $moduleXmlFile && \
    echo '<module xmlns="urn:jboss:module:1.3" name="org.hsqldb">' >> $moduleXmlFile && \
    echo '    <resources>' >> $moduleXmlFile && \
    echo "        <resource-root path=\"hsqldb-$HSQLDB_VERSION.jar\"/>" >> $moduleXmlFile && \
    echo '    </resources>' >> $moduleXmlFile && \
    echo '    <dependencies>' >> $moduleXmlFile && \
    echo '        <module name="javax.api"/>' >> $moduleXmlFile && \
    echo '        <module name="javax.transaction.api"/>' >> $moduleXmlFile && \
    echo '    </dependencies>' >> $moduleXmlFile && \
    echo '</module>' >> $moduleXmlFile

ENV P6SPY_VERSION 2.3.1
RUN wget -c "https://repo1.maven.org/maven2/p6spy/p6spy/$P6SPY_VERSION/p6spy-$P6SPY_VERSION.jar" -O /tmp/p6spy-$P6SPY_VERSION.jar
RUN mkdir -p $WILDFLY_HOME/modules/system/layers/base/com/p6spy/main && \
    cp /tmp/p6spy-$P6SPY_VERSION.jar $WILDFLY_HOME/modules/system/layers/base/com/p6spy/main/p6spy-$P6SPY_VERSION.jar && \
    cp /tmp/hsqldb-$HSQLDB_VERSION.jar $WILDFLY_HOME/modules/system/layers/base/com/p6spy/main/hsqldb-$HSQLDB_VERSION.jar && \
    moduleXmlFile=$( echo "$WILDFLY_HOME/modules/system/layers/base/com/p6spy/main/module.xml" ) && \
    echo '<?xml version="1.0" encoding="UTF-8"?>' > $moduleXmlFile && \
    echo '<module xmlns="urn:jboss:module:1.3" name="com.p6spy">' >> $moduleXmlFile && \
    echo '    <resources>' >> $moduleXmlFile && \
    echo "        <resource-root path=\"p6spy-$P6SPY_VERSION.jar\"/>" >> $moduleXmlFile && \
    echo "        <resource-root path=\"hsqldb-$HSQLDB_VERSION.jar\"/>" >> $moduleXmlFile && \
    echo '    </resources>' >> $moduleXmlFile && \
    echo '    <dependencies>' >> $moduleXmlFile && \
    echo '        <module name="javax.api"/>' >> $moduleXmlFile && \
    echo '        <module name="javax.transaction.api"/>' >> $moduleXmlFile && \
    echo '    </dependencies>' >> $moduleXmlFile && \
    echo '</module>' >> $moduleXmlFile

RUN $WILDFLY_HOME/bin/standalone.sh --admin-only & \
    sleep 10 \
    && $WILDFLY_HOME/bin/jboss-cli.sh -u=$WILDFLY_ADMIN_USER -p=$WILDFLY_ADMIN_PASSWORD --connect --commands='/subsystem=datasources/jdbc-driver=p6spy:add(driver-name="p6spy",driver-class-name="com.p6spy.engine.spy.P6SpyDriver",driver-module-name="com.p6spy")' \
    && $WILDFLY_HOME/bin/jboss-cli.sh -u=$WILDFLY_ADMIN_USER -p=$WILDFLY_ADMIN_PASSWORD --connect --commands='/subsystem=datasources/jdbc-driver=hsqldb:add(driver-name="hsqldb",driver-class-name="org.hsqldb.jdbc.JDBCDriver",driver-module-name="org.hsqldb")' \
    && $WILDFLY_HOME/bin/jboss-cli.sh -u=$WILDFLY_ADMIN_USER -p=$WILDFLY_ADMIN_PASSWORD --connect --commands='/subsystem=datasources/data-source=feedreader:add(driver-name="p6spy",connection-url="jdbc:p6spy:hsqldb:file:~/.feedreader/hsqldb",user-name="feedreader",password="a5tY6d4u7",jndi-name="java:jboss/datasources/feedreader")'

RUN rm -rfv $WILDFLY_HOME/standalone/configuration/standalone_xml_history/*

# Expose the ports we're interested in
EXPOSE 8080 9990

ENV JAVA_OPTS="-server -Xms512m -Xmx1024m -XX:MetaspaceSize=128M -XX:MaxMetaspaceSize=256m -Djava.net.preferIPv4Stack=true -Djboss.modules.system.pkgs=org.jboss.byteman -Djava.awt.headless=true -XX:+UseG1GC -Djavamelody.datasources=java:jboss/datasources/feedreader"

# Set the default command to run on boot
# This will boot WildFly in the standalone mode and bind to all interface
CMD ["/opt/wildfly/wildfly-10.1.0.Final/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0", "-Dp6spy.config.driverlist=org.hsqldb.jdbc.JDBCDriver", "-Dp6spy.config.appender=com.p6spy.engine.spy.appender.StdoutLogger"]
