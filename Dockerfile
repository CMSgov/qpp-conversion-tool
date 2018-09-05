FROM maven:3-jdk-8

RUN mkdir -p /usr/src/app/
RUN mkdir -p /usr/src/run/

COPY ./ /usr/src/app/

WORKDIR /usr/src/app/

RUN echo $NEXUS_CREDS $NEXUS_HOST > ~/nexus_env
RUN env > ~/all_env
RUN cp -r ./tools/docker/docker-artifacts/* /usr/src/run/
# override default maven settings
RUN /usr/src/run/nexus/export_template.sh /usr/share/maven/ref/settings.xml
RUN mvn install -Dmaven.test.skip -Djacoco.skip=true > /dev/null
RUN cp ./rest-api/target/rest-api.jar /usr/src/run/

WORKDIR /usr/src/run/

EXPOSE 8080
CMD ["/usr/src/run/qppConverter.sh"]
