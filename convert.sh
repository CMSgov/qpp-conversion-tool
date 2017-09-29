#!/bin/sh

if [ ! -f commandline/target/commandline.jar ]; then
    echo "Jar not found. Building..."
    mvn package -Dmaven.test.skip=true
    if [ ! -f commandline/target/commandline.jar ]; then
        echo "Build failed. Aborting."
        exit 1
    fi
fi

java -jar commandline/target/commandline.jar $@
