[![CircleCI](https://circleci.com/gh/CMSgov/qpp-conversion-tool.svg?style=shield&circle-token=138a1805ad2eb5e0a97e740abefd217aea014731)](https://circleci.com/gh/CMSgov/qpp-conversion-tool)

# qpp-conversion-tool

* [Installation Instructions](#developer-installation-instructions)
* [User Instructions](#user-instructions)
* [Want to Contribute?](#want-to-contribute)

## Installation Instructions

### Prerequisite Software

Before you can use the qpp-conversion-tool, you must install and configure the following products on your machine:

* [Git](http://git-scm.com) and/or the **GitHub app** (for [Mac](http://mac.github.com) or
  [Windows](http://windows.github.com)); [GitHub's Guide to Installing
  Git](https://help.github.com/articles/set-up-git) is a good source of information.

* [Java Runtime](https://java.com/download), (version `>=1.8`)

  It is important that you have the right version of java on your path.

  ```shell
  # When you run 'java -version', you should get 1.8.XXXXX. For example:
  java -version
  java version "1.8.0_121"
  ...
  ```

  Sometimes the Java Runtime installer doesn't update your path. So you must do it manually. Alternatively, download and install the [Java Development Kit](http://www.oracle.com/technetwork/es/java/javase/downloads/index.html), (version `>=1.8`). The Java Development Kit is better at updating your path.

* [Maven](https://maven.apache.org), (version `>=3.3`) which is used to build the library, manage dependencies,
  run tests, and generate distributable files. After you unzip the Maven archive, you need to add the `bin` directory to your path, as described the [Maven installation instructions](https://maven.apache.org/install.html).

  ```shell
  # When you run 'mvn -v', you should get 3.3.X. For example:
  mvn -v
  Apache Maven 3.3.9
  ...
  ```
* [Docker](https://www.docker.com) is not required but is an alternative to the above requirements if all you need is to run the converter.

### Getting and using the converter

If you are using a Unix-like OS like Linux or macOS, open a terminal and go to the directory you want the converter tool directory to be created in:

```shell
# Clone the GitHub repository:
git clone https://github.com/CMSgov/qpp-conversion-tool.git

# Go to the qpp-conversion-tool directory:
cd qpp-conversion-tool

# There is a convenient bash script wrapper. Make sure it's executable:
chmod +x convert.sh

# Try the convert script. If the JAR isn't built yet, the script will build it for you:
./convert.sh java-conversion-tool/src/test/resources/valid-QRDA-III.xml
```

If you are using Windows, open a command prompt (not PowerShell) and go to the directory you want the converter tool directory to be created in:

```shell
# Clone the GitHub repository:
git clone https://github.com/CMSgov/qpp-conversion-tool.git

# Go to the qpp-conversion-tool directory:
cd qpp-conversion-tool

# Try the convert script. If the JAR isn't built yet, the script will build it for you:
.\convert.bat java-conversion-tool/src/test/resources/valid-QRDA-III.xml
```

If you are using Docker to run the conversion as a ReST API, open a terminal and go to the cloned directory:

```shell
# Clone the GitHub repository:
git clone https://github.com/CMSgov/qpp-conversion-tool.git

# Go to the qpp-conversion-tool directory:
cd qpp-conversion-tool

# Build the Docker image
docker build -t qpp_conversion .

# Run the Docker container
docker run --rm -p 8080:8080 qpp_conversion
```

## User Instructions

### Command Line
For the examples below, make sure you're in the `qpp-conversion-tool` directory.

Note: If you are using Windows, replace `./convert.sh` in the examples below with `.\convert.bat`.

#### Conversion Help
```shell
./convert.sh -h
```

```
usage: convert [-b] [-d] [-h] [-t <scope1,scope2,...>] [-v]
 -b,--bygone                              Signals a historical conversion
 -d,--skipDefaults                        Skip defaulted transformations
 -h,--help                                This help message
 -t,--templateScope <scope1,scope2,...>   Comma delimited scope values to
                                          use for context. Valid values:
                                          [ACI_AGGREGATE_COUNT,
                                          ACI_NUMERATOR, ACI_DENOMINATOR,
                                          ACI_NUMERATOR_DENOMINATOR,
                                          ACI_SECTION, MEASURE_PERFORMED,
                                          IA_MEASURE, IA_SECTION,
                                          DEFAULTS, MEASURE_DATA_CMS_V2,
                                          MEASURE_REFERENCE_RESULTS_CMS_V2
                                          , MEASURE_SECTION_V2,
                                          CLINICAL_DOCUMENT]
 -v,--skipValidation                      Skip validations
```

#### Output
If a QRDA-III XML file is successfully converted, a QPP JSON file is created in the current working directory.
The file name will have the same name as the input file but with the extension `.qpp.json`.
For example, `valid-QRDA-III.qpp.json`.

When an invalid file is provided to the converter, an error JSON file is created in the current working directory.
The file name will be the same as the input file but with the extension `.err.json`.
For example, `not-a-QRDA-III-file.err.json`.  This error file contains descriptions and XPaths that help in identifying the
errors in the provided input file.

#### Convert a valid file.

```shell
./convert.sh qrda-files/valid-QRDA-III.xml
```

#### Convert a valid file but skip inserting default stubs.

```shell
./convert.sh qrda-files/valid-QRDA-III.xml --skip-defaults
```

#### Convert an file without an 'xml' extension.

```shell
./convert.sh qrda-files/valid-QRDA-III-without-xml-extension
```

#### Convert a bunch of QRDA-III files concurrently (multi-threaded).

```shell
./convert.sh qrda-files/multi/*.xml
```

#### Try to convert a QRDA-III file that doesn't contain required measures.

```shell
./convert.sh qrda-files/QRDA-III-without-required-measure.xml
```

#### Try to convert a file that is not a QRDA-III file.

```shell
./convert.sh qrda-files/not-a-QDRA-III-file.xml
```

### ReST API via Docker
The Conversion Tool can be executed through a ReST API.  See [above](#getting-and-using-the-converter) for how to start the API endpoint.
```shell
curl -X POST \
  http://localhost:8080/v1/qrda3 \
  -H 'cache-control: no-cache' \
  -H 'content-type: multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW' \
  -F file=@./qrda-files/valid-QRDA-III.xml
```

The response body will either be the QPP JSON on success or error JSON on an error.
The HTTP Status will be `201 Created` on succes or `422 Unprocessable entity` on an error.

### Simple UI to test converter

visit [http://qrda-conversion-ui.s3-website-us-east-1.amazonaws.com/](http://qrda-conversion-ui.s3-website-us-east-1.amazonaws.com/) 

Use the upload interface to add QRDA XML files to an upload queue. Click `upload` next to the file names to convert the files.

#### Do not send any PII (Personally Identifiable Information) in the QRDA file.  This is not a secure system

## Want to contribute?

Want to file a bug or contribute some code? Read up on our
guidelines for [contributing][contributing], and [developer instructions][developer].

[contributing]: https://github.com/CMSgov/qpp-conversion-tool/blob/master/CONTRIBUTING.md
[developer]: https://github.com/CMSgov/qpp-conversion-tool/blob/master/DEVELOPER.md

## Public Domain
This project is in the public domain within the United States, and copyright and related rights in the work worldwide are waived through the CC0 1.0 Universal public domain dedication.

All contributions to this project will be released under the CC0 dedication. By submitting a pull request, you are agreeing to comply with this waiver of copyright interest.

## SonarQube
[SonarQube](http://sonar.shareddev.flexion.us:9000/dashboard?id=gov.cms.qpp.conversion%3Aqpp-conversion)

