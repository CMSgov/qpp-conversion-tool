#!/bin/bash

# Configure New Relic
if [ ! -z "$NEWRELIC_API_KEY" ]; then
    sed -i -e "s/NEWRELIC_API_KEY/$NEWRELIC_API_KEY/g" ./newrelic/newrelic.yml
    sed -i -e "s/APP_ENV/$APP_ENV/g" ./newrelic/newrelic.yml
    NEW_RELIC_AGENT="-javaagent:./newrelic/newrelic.jar"
fi

# Datadog configuration with New Relic feature parity
if [ ! -z "$DD_API_KEY" ]; then
    DATADOG_AGENT="-javaagent:./datadog/dd-java-agent.jar"
    # Tracing: enabled with 100% sampling and analytics
    # Profiling: continuous profiling and traceId log injection for correlation
    # HTTP monitoring: error status definitions for server/client
    # JMX metrics: enabled for application monitoring
    # Method tracing: auto-trace Spring web controllers
    # Security: disable principal collection, enable obfuscation
    # Performance: exclude query strings, set async timeout
    DATADOG_OPTS="-Ddd.service.name=${DD_SERVICE:-qpp-conversion-tool} \
        -Ddd.env=${APP_ENV:-test} \
        -Ddd.version=${DD_VERSION:-unknown} \
        -Ddd.trace.enabled=true \
        -Ddd.trace.sample.rate=1.0 \
        -Ddd.trace.analytics.enabled=true \
        -Ddd.profiling.enabled=true \
        -Ddd.logs.injection=true \
        -Ddd.trace.http.server.error.statuses=500-599 \
        -Ddd.trace.http.client.error.statuses=500-599 \
        -Ddd.jmxfetch.enabled=true \
        -Ddd.trace.methods=org.springframework.web.bind.annotation.*[*] \
        -Ddd.trace.servlet.principal.enabled=false \
        -Ddd.trace.obfuscation.enabled=true \
        -Ddd.trace.http.client.tag.query-string=false \
        -Ddd.trace.servlet.async-timeout=30000"
fi

# Start application with both agents during transition
exec java $JAVA_OPTS $NEW_RELIC_AGENT $DATADOG_AGENT $DATADOG_OPTS \
    -jar ./rest-api.jar