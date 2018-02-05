# Building and Testing the QPP Conversion Tool

This document describes how to set up your development environment to build and test the qpp-conversion-tool. Please read the
[main README][readme] that outlines the initial software required and how to get the source.

* [Prerequisite Software](#additional-prerequisite-software)
* [Building](#building)
* [Running Tests Locally](#running-tests-locally)
* [Integration Environment](#integration-environment)
* [SonarQube](#sonarqube)
* [Third Party Extensions](#third-party-extensions)

See the [contribution guidelines](/.github/CONTRIBUTING.md)
if you'd like to contribute to qpp-conversion-tool.

## Additional Prerequisite Software

The additional prerequisites that were not already outlined in the
[main README][readme] are...
- [Java Development Kit](http://www.oracle.com/technetwork/java/javase/downloads/index.html) (version `>= 1.8`). The Java Runtime
will not suffice.
- [Maven](https://maven.apache.org).
- Due to using [Localstack](https://localstack.cloud) for testing, the following are also required
  - [Python](https://www.python.org/downloads/) (version `2.x` or `3.x`, but why not use the latest version?) with [pip](https://pip.pypa.io/en/stable/installing/).
  - `make` command line program.
  - `npm` from [Node.js](https://nodejs.org/).

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

In addition, there are some extreme JMeter load tests that execute only when passing in `-DskipJmeterSuite=false`.  To run these
tests, one must fill in the `host`, `path`, `port`, and `cookie` fields in the [benchmark pom.xml](/benchmark/pom.xml) to
correctly connect to the test environment. Make sure you have time, the CPU performance, and network performance before deciding
to run these tests.

## Integration Environment

In addition to converting input files to QPP, the ReST API can do the following...
- Call the submission validation API to validate the QPP JSON.
- Write the input file and output JSON to a S3 bucket.
- Write metadata to a DynamoDB table.

This additional functionality depends on the environment variables set. See the [.env](.env) file for descriptions of what the
environment variables are for.

### Amazon Web Services

The ReST API depends on AWS for some of the additional functionality. Currently, a real account with AWS must be used.
[Localstack](https://github.com/localstack/localstack) is used in tests.

Ensure that credentials are set such that the
[Default Credential Provider Chain](http://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html) can find them.
Also ensure that a region is set such that the
[Default Region Provider Chain](http://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/java-dg-region-selection.html#automatically-determine-the-aws-region-from-the-environment)
can find it.  If not, `us-east-1` is used by default.

#### DynamoDB

The DynamoDB table requires the partition key be named `Uuid` and of type `String`.

## SonarQube

We have a [SonarQube](https://sonarcloud.io/dashboard?id=gov.cms.qpp.conversion%3Aqpp-conversion) server to
measure how clean the codebase is.

## Third Party Extensions

Finished a cool new feature or fix for the conversion tool? Please feel free submit a pull request to [our repository](https://github.com/CMSgov/qpp-conversion-tool) for any changes made. We appreciate and thank you for your patronage!
See the [contribution guidelines](/.github/CONTRIBUTING.md) on how to submit a Pull Request.

[readme]: /README.md
