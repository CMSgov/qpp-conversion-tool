[![Build Status](https://travis-ci.org/angular/angular.svg?branch=master)](https://travis-ci.org/angular/angular)
[![CircleCI](https://circleci.com/gh/angular/angular/tree/master.svg?style=shield)](https://circleci.com/gh/angular/angular/tree/master)
[![Join the chat at https://gitter.im/angular/angular](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/angular/angular?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Issue Stats](http://issuestats.com/github/angular/angular/badge/pr?style=flat)](http://issuestats.com/github/angular/angular)
[![Issue Stats](http://issuestats.com/github/angular/angular/badge/issue?style=flat)](http://issuestats.com/github/angular/angular)
[![npm version](https://badge.fury.io/js/%40angular%2Fcore.svg)](https://badge.fury.io/js/%40angular%2Fcore)

# adele-bpa-qpp-conversion-tool

This text below models what the open souce project would look like. The Design Process we used to build the product is documented in the [Design Process](https://github.com/flexion/adele-bpa-qpp-conversion-tool/blob/master/DESIGN_PROCESS.md) page.

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