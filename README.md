[[![CircleCI](https://circleci.com/gh/flexion/adele-bpa-qpp-conversion-tool.svg?style=shield&circle-token=7747433694389fbec2a45e697b4952ebd0272cea)](https://circleci.com/gh/flexion/adele-bpa-qpp-conversion-tool)](https://circleci.com/gh/flexion/adele-bpa-qpp-conversion-tool/tree/master)

# adele-bpa-qpp-conversion-tool

This text below models what the open souce project would look like. The process we used to build the converter is documented in the [Design Process](https://github.com/flexion/adele-bpa-qpp-conversion-tool/blob/master/DESIGN_PROCESS.md) page.

* [Installation Instructions](#developer-installation-instructions)
* [User Instructions](#user-instructions)
* [Want to Contribute?](#want-to-contribute)

## Installation Instructions

### Prerequisites

The following must be installed on your computer:
* Java JDK 8 or higher - [Download Java JRE](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

  It is important that you have the right version of java on your path. If you installed the JRE, you may need to update your path manually. Insalling the JDK rather than the JRE should talk care of your path automatically.

  ```shell
  # When you run 'java -version', you should get 1.8.XXXXX. For example:
  java -version
  java version "1.8.0_121"
  ...
  ```

### Getting and using the converter

If you are using a Unix-like OS like Linux or OSX, open a terminal and go to a directory in which you want the the converter tool directory to be created: 

```shell
# Clone the GitHub repository:
git clone https://github.com/flexion/adele-bpa-qpp-conversion-tool.git qpp-conversion-tool

# Go to the qpp-conversion-tool directory:
cd qpp-conversion-tool

# There is a convenient bash script wrapper. Make it executable:
chmod +x convert.sh

# Try the convert script. If the JAR isn't built  yet, it will build it for you:
./convert.sh java-conversion-tool/src/test/resources/valid-QRDA-III.xml 
```

If you are using a Windows OS, open a command prompt (not PowerShell) and go to a directory in which you want the the converter tool directory to be created: 

```shell
# Clone the GitHub repository:
git clone https://github.com/flexion/adele-bpa-qpp-conversion-tool.git qpp-conversion-tool

# Go to the qpp-conversion-tool directory:
cd qpp-conversion-tool

# Try the convert script. If the JAR isn't built  yet, it will build it for you:
convert.bat java-conversion-tool/src/test/resources/valid-QRDA-III.xml 
```

## User Instructions

Note: If you are using Windows, replace `./convert.sh` in the examples below with `.\convert.bat`.

### Convert a valid file.

```shell
./convert.sh src/test/resources/valid-QRDA-III.xml
```

### Convert an file without and 'xml' extension.

```shell
./convert.sh src/test/resources/valid-QRDA-III
```

### Convert a bunch of QRDA-III files concurrently (multi-threaded).

```shell
./convert.sh src/test/resources/qrda/*.**
```

### Try to convert a QRDA-III file that doesn't contain required measures.

```shell
./convert.sh src/test/resources/missing-measure-QRDA-III.xml
```

### Try to convert a file that is not a QRDA-III file.

```shell
./convert.sh src/test/resources/not-a-QRDA-III.xml
```

## Want to contribute?

Want to file a bug or contribute some code? Read up on our
guidelines for [contributing][contributing].

[contributing]: https://github.com/flexion/adele-bpa-qpp-conversion-tool/blob/master/CONTRIBUTING.md