[![CircleCI](https://circleci.com/gh/CMSgov/qpp-conversion-tool.svg?style=shield&circle-token=138a1805ad2eb5e0a97e740abefd217aea014731)](https://circleci.com/gh/CMSgov/qpp-conversion-tool)

# qpp-conversion-tool

* [Installation Instructions](#installation-instructions)
* [Running the Converter](#running-the-converter)
* [Want to Contribute?](#want-to-contribute)
* [Public Domain](#public-domain)

## Installation Instructions

### Prerequisite Software

Before you can use the qpp-conversion-tool application, you must install and configure the following products on your machine:

* [Git](http://git-scm.com) and/or the [GitHub application](https://desktop.github.com).

  [GitHub's Guide to Installing Git](https://help.github.com/articles/set-up-git) is a good source of information.

* [Java Runtime](https://java.com/download) (version `>= 1.8`).

  It is important that you have the right version of `java` on your path.

  ```shell
  # When you run 'java -version', you should get 1.8.XXXXX. For example:
  java -version
  java version "1.8.0_144"
  ...
  ```

  Sometimes the Java Runtime installer doesn't update your path. So you must do it manually. Alternatively, download and install
  the [Java Development Kit](http://www.oracle.com/technetwork/java/javase/downloads/index.html) (version `>= 1.8`). The JDK is
  better at updating your path.

* [Maven](https://maven.apache.org) (version `>= 3.3`).

  Maven is used to build the application, manage dependencies, and run tests. After you unzip the Maven archive, you need to add
  the `bin` directory to your path, as described the [Maven installation instructions](https://maven.apache.org/install.html).

  ```shell
  # When you run 'mvn -v', you should get 3.3.X. For example:
  mvn -v
  Apache Maven 3.3.9
  ...
  ```
* [Docker](https://www.docker.com).

  Docker is not required but is an alternative to the above requirements if all you need is to run the converter.

### Getting the Converter

Use the GitHub application or the command line to clone this repository.

```shell
# Clone the GitHub repository.
git clone https://github.com/CMSgov/qpp-conversion-tool.git

# Go to the qpp-conversion-tool directory.
cd ./qpp-conversion-tool
```

## Running the Converter

### ReST API via Docker

The Conversion Tool can be executed through a ReST API. Using the ReST API has the added benefit of having the
[QPP validated](#submission-validation) if you so choose. For the examples below, make sure you're in the `qpp-conversion-tool`
directory.

#### Starting the API Endpoint

```shell
# Build the Docker image and run the container using docker-compose.
docker-compose -f ./docker-compose.test.yaml up
```

#### Invoking the Endpoint

```shell
curl -X POST \
  http://localhost:3000 \
  -H 'cache-control: no-cache' \
  -H 'content-type: multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW' \
  -F file=@./qrda-files/valid-QRDA-III-latest.xml
```

The response body will either be the QPP JSON on success or error JSON on an error.
The HTTP Status will be `201 Created` on success or `422 Unprocessable entity` on an error.

#### Submission Validation

*The submissions validation API is currently unavailable.*

While the converter will validate the format of the QRDA-III file and some basic semantics, deeper semantic checks are only
available if you enable the submission validation API. If any errors are found by the submission validation API, error JSON will
be returned from the ReST API like normal.

To enable the submission validation API, modify the `.env` file with `VALIDATION_URL=<URL to the submissions validation API>` and
then [restart the ReST API endpoint](#starting-the-api-endpoint).

### Command Line

For the examples below, make sure you're in the `qpp-conversion-tool` directory.

Note: If you are using Windows, replace `./convert.sh` in the examples below with `.\convert.bat`.

#### Convert a File

```shell
# Try the convert script. If the JAR isn't built yet, the script will build it for you first.
./convert.sh ./qrda-files/valid-QRDA-III-latest.xml
```

If a QRDA-III XML file is successfully converted, a QPP JSON file is created in the current working directory.
The file name will have the same name as the input file but with the extension `.qpp.json`.
For example, `valid-QRDA-III-latest.qpp.json`.

When an invalid file is provided to the converter, an error JSON file is created in the current working directory.
The file name will be the same as the input file but with the extension `.err.json`.
For example, `not-a-QRDA-III-file.err.json`.  This error file contains descriptions and XPaths that help in identifying the
errors in the provided input file.

#### Commandline Help
```shell
./convert.sh -h
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

## Want to Contribute?

Want to file a bug or contribute some code? Read up on our guidelines for [contributing] and
[developer instructions][developer].

[contributing]: https://github.com/CMSgov/qpp-conversion-tool/blob/master/CONTRIBUTING.md
[developer]: https://github.com/CMSgov/qpp-conversion-tool/blob/master/DEVELOPER.md

## Public Domain
This project is in the public domain within the United States, and copyright and related rights in the work worldwide are waived
through the CC0 1.0 Universal public domain dedication.

All contributions to this project will be released under the CC0 dedication. By submitting a pull request, you are agreeing to
comply with this waiver of copyright interest.

See the [formal LICENSE file](https://github.com/CMSgov/qpp-conversion-tool/blob/master/LICENSE).
