#!/bin/bash

# build and run the benchmark project
mvn package
java -jar target/benchmark-jar-with-dependencies.jar >benchmarks.log 2>benchmarks.err
