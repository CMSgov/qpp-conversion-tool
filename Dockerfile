FROM maven:3-jdk-8

RUN mkdir -p /usr/src/app/
RUN mkdir -p /usr/src/run/

COPY ./ /usr/src/app/

WORKDIR /usr/src/app/

RUN mvn install -DskipTests

WORKDIR /usr/src/run/

EXPOSE 8080
CMD ["java", "-jar", "../app/rest-api/target/rest-api.jar"]
