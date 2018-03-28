FROM maven:3-jdk-8

RUN mkdir -p /usr/src/app/
RUN mkdir -p /usr/src/run/
RUN apt-get update && apt-get install dos2unix

COPY ./ /usr/src/app/

WORKDIR /usr/src/app/

RUN mvn install -Dmaven.test.skip -Djacoco.skip=true > /dev/null
RUN cp ./rest-api/target/rest-api.jar /usr/src/run/
RUN cp -r ./tools/docker/docker-test-artifacts/* /usr/src/run/
RUN dos2unix /usr/src/run/qppConverterTest.sh

WORKDIR /usr/src/run/

EXPOSE 8080
ENTRYPOINT ["/usr/src/run/qppConverterTest.sh"]
