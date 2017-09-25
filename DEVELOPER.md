# Building and Testing the QPP Conversion Tool

This document describes how to set up your development environment to build and test the qpp-conversion-tool. Please read the
[main README][readme] that outlines the initial software required and how to get the source.

* [Prerequisite Software](#additional-prerequisite-software)
* [Building](#building)
* [Running Tests Locally](#running-tests-locally)
* [SonarQube](#sonarqube)

See the [contribution guidelines](https://github.com/CMSgov/qpp-conversion-tool/blob/master/CONTRIBUTING.md)
if you'd like to contribute to qpp-conversion-tool.

## Additional Prerequisite Software

The only additional prerequisite that was not already outlined in the
[main README][readme] is the
[Java Development Kit](http://www.oracle.com/technetwork/java/javase/downloads/index.html) (version `>= 1.8`). The Java Runtime
will not suffice.

## Building

Use Maven to compile. Dependencies will automatically be downloaded.

```shell
# Build qpp-conversion-tool
mvn clean compile
```

## Running Tests Locally

Use Maven to run the tests.

```shell
# Run all the basic tests
mvn clean verify
```

You should run the test suite before submitting a PR. All the tests are executed on our Continuous Integration infrastructure and
a PR can only be merged once the tests pass.

### Performance Tests

The `-DrunLoadTests` flag will enable the load tests as part of the build. If the converter does not meet a threshold of
conversions, the tests will not pass and the build will fail.

```shell
# Run the basic tests and the load tests.
mvn clean verify -DrunLoadTests
```

## SonarQube

We have a [SonarQube](http://sonar.shareddev.flexion.us:9000/dashboard?id=gov.cms.qpp.conversion%3Aqpp-conversion) server to
measure how clean the codebase is.

[readme]: https://github.com/CMSgov/qpp-conversion-tool/blob/master/README.md
