#!/bin/bash

# build and run the benchmark project
mvn package
echo "executing benchmarks..."
java -jar target/benchmark-jar-with-dependencies.jar $1 $2 >benchmarks.log 2>benchmarks.err
cat benchmarks.dat
