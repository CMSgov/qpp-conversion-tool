#!/usr/bin/env bash

DIRECTORY=$(dirname $0)
RESOURCE_DIRECTORY=$DIRECTORY/../resources/
JAR_NAME=java-conversion-tool.jar
ARTIFACT_TOKEN=20700cebe441183f5ff406a8bab7b1c3f5b70e69
LATEST_ARTIFACTS=https://circleci.com/api/v1.1/project/github/CMSgov/qpp-conversion-tool/latest/artifacts?circle-token=
ARTIFACT_URL=$LATEST_ARTIFACTS$ARTIFACT_TOKEN
JAR_URL=$(curl --silent $ARTIFACT_URL | grep -oE "(https.*$JAR_NAME)")

mkdir -p $RESOURCE_DIRECTORY
curl $JAR_URL?circle-token=$ARTIFACT_TOKEN > $RESOURCE_DIRECTORY$JAR_NAME