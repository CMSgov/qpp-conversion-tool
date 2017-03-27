# Building and Testing QPP Conversion Tool

This document describes how to set up your development environment to build and test qpp-conversion-tool.

* [Prerequisite Software](#prerequisite-software)
* [Getting the Source](#getting-the-source)
* [Building](#building)
* [Running Tests Locally](#running-tests-locally)

See the [contribution guidelines](https://github.com/flexion/adele-bpa-qpp-conversion-tool/blob/master/CONTRIBUTING.md)
if you'd like to contribute to qpp-conversion-tool.

## Prerequisite Software

Before you can build and test qpp-conversion-tool, you must install and configure the
following products on your development machine:

* [Git](http://git-scm.com) and/or the **GitHub app** (for [Mac](http://mac.github.com) or
  [Windows](http://windows.github.com)); [GitHub's Guide to Installing
  Git](https://help.github.com/articles/set-up-git) is a good source of information.

* [Java Development Kit](http://www.oracle.com/technetwork/es/java/javase/downloads/index.html), (version `>=1.8`) which is used
  to develop to core library.

  It is important that you have the right version of java on your path.

  ```shell
  # When you run 'java -version', you should get 1.8.XXXXX. For example:
  java -version
  java version "1.8.0_121"
  ...
  ```

* [Maven](https://maven.apache.org), (version `>=3.3`) which is used to build the library, manage dependencies,
  run tests, and generate distributable files. After you unzip the Maven archive, you need to add the `bin` directory to you path, as described the [Maven installation instructions](https://maven.apache.org/install.html).

  ```shell
  # When you run 'mvn -v', you should get 3.3.X. For example:
  mvn -v
  Apache Maven 3.3.9
  ...
  ```

## Getting the Source

Clone the qpp-conversion-tool repository:

```shell
# Clone your GitHub repository:
git clone https://github.com/flexion/adele-bpa-qpp-conversion-tool.git qpp-conversion-tool

# Go to the qpp-conversion-tool directory:
cd qpp-conversion-tool
```

## Building

Next, build qpp-conversion-tool. Dependencies will automatically be downloaded:

```shell
# Build qpp-conversion-tool
mvn compile
```

## Running Tests Locally

To run tests:

```shell
# Run all qpp-conversion-tool tests
mvn test
```

You should run the test suite before submitting a PR to github.

All the tests are executed on our Continuous Integration infrastructure and a PR can only be merged once the tests pass.
