[[![CircleCI](https://circleci.com/gh/flexion/adele-bpa-qpp-conversion-tool.svg?style=shield&circle-token=7747433694389fbec2a45e697b4952ebd0272cea)](https://circleci.com/gh/flexion/adele-bpa-qpp-conversion-tool)](https://circleci.com/gh/flexion/adele-bpa-qpp-conversion-tool/tree/master)

**CMS REVIEWER NOTE: This text below models what an open souce project might look like. The process we used to build the converter is documented in the [Design Process](https://github.com/flexion/adele-bpa-qpp-conversion-tool/blob/master/DESIGN_PROCESS.md) page. Links to developer instructions are at [the bottom](#want-to-contribute) of this page**

# adele-bpa-qpp-conversion-tool

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

### Getting and using the converter

If you are using a Unix-like OS like Linux or OSX, open a terminal and go to the directory you want the converter tool directory to be created in:

```shell
# Clone the GitHub repository:
git clone https://github.com/flexion/adele-bpa-qpp-conversion-tool.git qpp-conversion-tool

# Go to the qpp-conversion-tool directory:
cd qpp-conversion-tool

# There is a convenient bash script wrapper. Make sure it's executable:
chmod +x convert.sh

# Try the convert script. If the JAR isn't built yet, the script will build it for you:
./convert.sh java-conversion-tool/src/test/resources/valid-QRDA-III.xml
```

If you are using Windows, open a command prompt (not PowerShell) and go to the directory you want the the converter tool directory to be created in:

```shell
# Clone the GitHub repository:
git clone https://github.com/flexion/adele-bpa-qpp-conversion-tool.git qpp-conversion-tool

# Go to the qpp-conversion-tool directory:
cd qpp-conversion-tool

# Try the convert script. If the JAR isn't built yet, the script will build it for you:
.\convert.bat java-conversion-tool/src/test/resources/valid-QRDA-III.xml
```

## User Instructions
For the examples below, make sure you're in the `qpp-conversion-tool` directory.

Note: If you are using Windows, replace `./convert.sh` in the examples below with `.\convert.bat`.

### Convert a valid file.

```shell
./convert.sh qrda-files/valid-QRDA-III.xml
```

### Convert a valid file but skip inserting default stubs.

```shell
./convert.sh qrda-files/valid-QRDA-III.xml --skip-defaults
```

### Convert an file without an 'xml' extension.

```shell
./convert.sh qrda-files/valid-QRDA-III-without-xml-extension
```

### Convert a bunch of QRDA-III files concurrently (multi-threaded).

```shell
./convert.sh qrda-files/multi/*.xml
```

### Try to convert a QRDA-III file that doesn't contain required measures.

```shell
./convert.sh qrda-files/QRDA-III-without-required-measure.xml
```

### Try to convert a file that is not a QRDA-III file.

```shell
./convert.sh qrda-files/not-a-QDRA-III-file.xml
```

## Want to contribute?

Want to file a bug or contribute some code? Read up on our
guidelines for [contributing][contributing], and [developer instructions][developer]

[contributing]: https://github.com/flexion/adele-bpa-qpp-conversion-tool/blob/master/CONTRIBUTING.md
[developer]: https://github.com/flexion/adele-bpa-qpp-conversion-tool/blob/master/DEVELOPER.md
