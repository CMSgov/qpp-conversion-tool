#!/bin/bash

sed -i -e "s/NEWRELIC_API_KEY/$NEWRELIC_API_KEY/g" ./newrelic/newrelic.yml
sed -i -e "s/APP_ENV/$APP_ENV/g" ./newrelic/newrelic.yml
exec java $JAVA_OPTS -javaagent:./newrelic/newrelic.jar -jar ./rest-api.jar