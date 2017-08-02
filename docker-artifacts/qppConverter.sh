#!/bin/bash

sed -i -e "s/NEWRELIC_API_KEY/$NEWRELIC_API_KEY/g" ./newrelic/newrelic.yml
java -javaagent:./newrelic/newrelic.jar -jar ./rest-api.jar