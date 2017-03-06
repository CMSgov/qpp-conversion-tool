#!/bin/sh
if [ ! -f java-conversion-tool/target/java-conversion-tool.jar ]; then
    echo "Jar not found. Building..."
    cd java-conversion-tool
    mvn package -Dmaven.test.skip=true
    if [ ! -f target/java-conversion-tool.jar ]; then
        echo "Build failed. Aborting."
        cd ..
        exit 1
    fi
    cd ..
fi

java -jar java-conversion-tool/target/java-conversion-tool.jar $1 $2 $3 $4