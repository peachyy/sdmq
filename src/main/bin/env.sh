#!/bin/bash

BUILD_ID=5555555555buildid
LOG_HOME=/var/logs/sdmq/
LOG_PATH=$LOG_HOME
export JAVA_HOME=/usr/local/jdk
JAVA_OPTS=' -server  -Xms256m -Xmx256m -XX:PermSize=64m -XX:MaxPermSize=128m  -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./heap -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true  -Dcom.sun.management.jmxremote -Djava.rmi.server.hostname=127.0.0.1 -Dcom.sun.management.jmxremote.port=6598 -Dcom.sun.management.jmxremote.authenticate=false
 -Dcom.sun.management.jmxremote.ssl=false'
#JAVA_OPTS=' -server  -Dcom.sun.management.jmxremote -Djava.rmi.server.hostname=219.239.88.245 -Dcom.sun.management.jmxremote.port=6598 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false'