FROM maven:3-jdk-8

RUN mkdir -p /usr/src/app/
RUN mkdir -p /usr/src/run/

COPY ./ /usr/src/app/

WORKDIR /usr/src/app/

RUN cp -r ./tools/docker/docker-artifacts/* /usr/src/run/
RUN mvn install -Dmaven.test.skip -Djacoco.skip=true > /dev/null
RUN cp ./rest-api/target/rest-api.jar /usr/src/run/

WORKDIR /usr/src/run/

EXPOSE 8080
CMD ["/usr/src/run/qppConverter.sh"]
