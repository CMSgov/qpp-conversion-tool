FROM maven:3-jdk-8

RUN mkdir -p /usr/src/app/
RUN mkdir -p /usr/src/run/

COPY ./ /usr/src/app/

WORKDIR /usr/src/app/

RUN mvn install -DskipTests > /dev/null
RUN cp ./rest-api/target/rest-api.jar /usr/src/run/
RUN cp -r ./docker-artifacts/* /usr/src/run/
RUN chmod +x /usr/src/run/qppConverter.sh

WORKDIR /usr/src/run/

EXPOSE 8080
CMD ["/usr/src/run/qppConverter.sh"]
