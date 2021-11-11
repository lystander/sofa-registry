#!/bin/bash


set -o pipefail

if [[ -z "$REGISTRY_APP_NAME" ]]; then
    echo "Must provide REGISTRY_APP_NAME in environment" 1>&2
    exit 1
fi
BASE_DIR=`cd $(dirname $0)/../..; pwd`
APP_JAR="${BASE_DIR}/registry-${REGISTRY_APP_NAME}.jar"
if [[ -z "$SPRING_CONFIG_LOCATION" ]]; then
  SPRING_CONFIG_LOCATION=${BASE_DIR}/conf/application.properties
fi

# set user.home
JAVA_OPTS="$JAVA_OPTS -Duser.home=${BASE_DIR} -Dspring.config.location=${SPRING_CONFIG_LOCATION}"

# springboot conf
SPRINGBOOT_OPTS="${SPRINGBOOT_OPTS} -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector"
SPRINGBOOT_OPTS="-Dlogging.config=${BASE_DIR}/conf/${REGISTRY_APP_NAME}/log4j2.xml  -Dlog4j.configurationFile=${BASE_DIR}/conf/${REGISTRY_APP_NAME}/log4j2.xml"

MEM=8192
HEAP_MAX=$(expr $MEM \* 60 / 100)
HEAP_NEW=$(expr $HEAP_MAX \* 3 / 8)
DIRECT_MEM=$(expr $MEM \* 10 / 100)
JAVA_OPTS="$JAVA_OPTS -server -Xms${HEAP_MAX}m -Xmx${HEAP_MAX}m -Xmn${HEAP_NEW}m -Xss512k -XX:MetaspaceSize=150m -XX:MaxMetaspaceSize=150m "

# gc option
mkdir -p "${BASE_DIR}/logs"
JAVA_OPTS="$JAVA_OPTS -XX:+DisableExplicitGC -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:${BASE_DIR}/logs/registry-${REGISTRY_APP_NAME}-gc.log -verbose:gc"
JAVA_OPTS="$JAVA_OPTS -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${BASE_DIR}/logs -XX:ErrorFile=${BASE_DIR}/logs/registry-${REGISTRY_APP_NAME}-hs_err_pid%p.log"
JAVA_OPTS="$JAVA_OPTS -XX:-OmitStackTraceInFastThrow -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:ParallelGCThreads=4"
JAVA_OPTS="$JAVA_OPTS -XX:+CMSClassUnloadingEnabled -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70"

# start
echo "Command: java ${JAVA_OPTS} -jar ${APP_JAR} ${SPRINGBOOT_OPTS}"
exec java ${JAVA_OPTS} -jar ${APP_JAR} ${SPRINGBOOT_OPTS}