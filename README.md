# adele-bpa-qpp-conversion-tool

This text below models what the open souce project would look like. The Design Process we used to build the product is documented in the Design Process wiki page.

* [Installation Instructions](#developer-installation-instructions)
* [User Instructions](#user-instructions)
* [Development Instructions](#development-instructions)

## Installation Instructions

### Prerequisites

The following must be installed on your computer:
* Java JDK 8 or higher java_url
* Maven version X or higher maven_url
* Git https://git-scm.com/

### Clone Project

Download project from [Github](https://github.com/flexion/adele-bpa-qpp-conversion-tool), or clone via command line: git clone https://github.com/flexion/adele-bpa-qpp-conversion-tool.git qpp-conversion-tool

### Installation

From the command line, navigate to the directory 'qpp-conversion-tool' and install project dependencies with the command 'mvn install'.

add the script to your path

## User Instructions

### Convert a valid file

convert valid-QRDA-III.xml

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