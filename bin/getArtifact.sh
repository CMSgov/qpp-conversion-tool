#!/bin/bash
token=20700cebe441183f5ff406a8bab7b1c3f5b70e69
JAR_NAME=java-conversion-tool.jar
ENDPOINT=https://circleci.com/api/v1.1/project/github/CMSgov/qpp-conversion-tool/latest/artifacts?circle-token=
artifactURL=$(curl -s ${ENDPOINT}${token} | grep -Eo "https://.*/${JAR_NAME}")
curl ${artifactURL}?circle-token=${token} -o ./${JAR_NAME}
