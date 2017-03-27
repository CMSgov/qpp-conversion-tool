#!/bin/sh
if [ ! -f java-conversion-tool/target/java-conversion-tool.jar ]; then
    echo "Jar not found. Building..."
    mvn package -Dmaven.test.skip=true
    if [ ! -f java-conversion-tool/target/java-conversion-tool.jar ]; then
        echo "Build failed. Aborting."
        exit 1
    fi
fi

java -jar java-conversion-tool/target/java-conversion-tool.jar $@
