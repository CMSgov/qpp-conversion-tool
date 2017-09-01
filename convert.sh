#!/bin/sh

if [ ! -f converter/target/converter.jar ]; then
    echo "Jar not found. Building..."
    mvn package -Dmaven.test.skip=true
    if [ ! -f converter/target/converter.jar ]; then
        echo "Build failed. Aborting."
        exit 1
    fi
fi

java -jar commandline/target/commandline.jar $@
