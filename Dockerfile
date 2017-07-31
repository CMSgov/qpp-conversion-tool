FROM maven:3-jdk-8

RUN mkdir -p /usr/src/app/
RUN mkdir -p /usr/src/run/

COPY ./ /usr/src/app/

WORKDIR /usr/src/app/

RUN mvn install -DskipTests > /dev/null
RUN cp ./rest-api/target/rest-api.jar /usr/src/run/
RUN cp -r ./docker-artifacts/newrelic /usr/src/run/newrelic
RUN sed -i -e "s/NEWRELIC_API_KEY/$NEWRELIC_API_KEY/g" /usr/src/run/newrelic/newrelic.yml

WORKDIR /usr/src/run/

EXPOSE 8080
CMD ["java", "-javaagent:/usr/src/run/newrelic/newrelic.jar", "-jar", "./rest-api.jar"]
