FROM maven:3-jdk-8

RUN mkdir -p /usr/src/app/
RUN mkdir -p /usr/src/qrda-files/
RUN mkdir -p /usr/src/qpp-files/

COPY ./ /usr/src/app/

WORKDIR /usr/src/app/java-conversion-tool/

RUN mvn install

WORKDIR /usr/src/qpp-files/

VOLUME ["/usr/src/qrda-files/", "/usr/src/qpp-files/"]

CMD ["java", "-jar", "../app/java-conversion-tool/target/java-conversion-tool.jar", "/usr/src/qrda-files/*"]
