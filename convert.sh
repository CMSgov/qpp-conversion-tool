#!/bin/sh
DIRECTORY=$(dirname $0)
LOGBACK=${DIRECTORY}/java-conversion-tool/src/main/resources/logback.xml

if [ ! -f java-conversion-tool/target/java-conversion-tool.jar ]; then
    echo "Jar not found. Building..."
    mvn package -Dmaven.test.skip=true
    if [ ! -f java-conversion-tool/target/java-conversion-tool.jar ]; then
        echo "Build failed. Aborting."
        exit 1
    fi
fi

java -Dlogback.configurationFile=${LOGBACK} -jar java-conversion-tool/target/java-conversion-tool.jar $@
