[[![CircleCI](https://circleci.com/gh/flexion/adele-bpa-qpp-conversion-tool.svg?style=shield&circle-token=7747433694389fbec2a45e697b4952ebd0272cea)](https://circleci.com/gh/flexion/adele-bpa-qpp-conversion-tool)](https://circleci.com/gh/flexion/adele-bpa-qpp-conversion-tool/tree/master)

# adele-bpa-qpp-conversion-tool

This text below models what the open souce project would look like. The process we used to build the converter is documented in the [Design Process](https://github.com/flexion/adele-bpa-qpp-conversion-tool/blob/master/DESIGN_PROCESS.md) page.

* [Installation Instructions](#developer-installation-instructions)
* [User Instructions](#user-instructions)
* [Development Instructions](#development-instructions)

## Installation Instructions

### Prerequisites

The following must be installed on your computer:
* Java JDK 8 or higher - [Download Java JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

  It is important that you have the right version of java on your path. Insalling the JDK rather than the JRE should talk care of your path automatically.

  ```shell
  # When you run 'java -version', you should get 1.8.XXXXX. For example:
  java -version
  java version "1.8.0_121"
  ...
  ```

* Maven version 3.3 or higher - [Download Maven](https://maven.apache.org/)

  ```shell
  # When you run 'mvn -v', you should get 1.3.X. For example:
  mvn -v
  Apache Maven 3.3.9
  ...
  ```

* Git - [Download Git](https://git-scm.com/)

### Installation

```shell
# Clone the GitHub repository:
git clone https://github.com/flexion/adele-bpa-qpp-conversion-tool.git qpp-conversion-tool

# Go to the qpp-conversion-tool directory:
cd qpp-conversion-tool

# Run Maven test to build and run tests locally:
mvn test
```

### Run the 'convert' script to verify everything's working.

If you're on linux or OSX, run the shell script

```shell
# Run the 'convert' shell script, passing the valid-QRDA-III.xml file as a parameter:
./convert.sh valid-QRDA-III.xml

...

created valid-QRDA-III.qpp.xml
```
If you're on Windows, run the .ps1 file.

```shell
# Run the 'convert' Powershell script, passing the valid-QRDA-III.xml file as a parameter:
./convert.ps1 valid-QRDA-III.xml

...

created valid-QRDA-III.qpp.xml
```

## User Instructions

### Convert a valid file

./convert valid-QRDA-III.xml

### Try to convert a QRDA-III file that doesn't contain required measures

convert invalidv-QRDA-III.xml

### Convert an file without and 'xml' extension

convert valid-QRDA-III

### Try to convert a file that is not a QRDA-III file

convert non-qrda-III-file.xml

### Try to convert a file that is not a QRDA-III file

convert non-qrda-III-file.xml

## Development Instructions

The application is built with the Java 8

## Development Tasks


### Build

mvn clean compile

### Running unit tests

mvn test