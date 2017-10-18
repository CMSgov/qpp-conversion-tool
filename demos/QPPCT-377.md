# QPPCT-377

`git checkout 18bcea3e912cad3747ff532c707be8864f5e2d90`

## Set-up
1. Add host to `host` field in [benchmark's pom.xml](../benchmark/pom.xml).
1. Add path to `path` field in [benchmark's pom.xml](../benchmark/pom.xml).
1. Add port to `port` field in [benchmark's pom.xml](../benchmark/pom.xml).
1. Add the cookie value to `cookie` field in [benchmark's pom.xml](../benchmark/pom.xml).

## Steps
1. Show JIRA. https://jira.cms.gov/browse/QPPCT-377
1. `cd ./benchmark/`.
1. `mvn jmeter:jmeter -DskipJmeterSuite=false`.
1. Watch the insanity.
