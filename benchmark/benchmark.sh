#!/bin/bash

# build and run the benchmark project
mvn package
java -jar target/benchmarks.jar >benchmarks.log 2>benchmarks.err
