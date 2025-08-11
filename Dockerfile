FROM eclipse-temurin:17 AS builder

ARG MAVEN_VERSION=3.9.6
ARG USER_HOME_DIR="/root"
ARG SHA=706f01b20dec0305a822ab614d51f32b07ee11d0218175e55450242e49d2156386483b506b3a4e8a03ac8611bae96395fd5eec15f50d3013d5deed6d1ee18224
ARG BASE_URL=https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/

RUN echo "we are in the QPPA-10640 branch"

RUN mkdir -p /usr/share/maven /usr/share/maven/ref \
  && curl -fsSL -o /tmp/apache-maven.tar.gz ${BASE_URL}/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
  && echo "${SHA}  /tmp/apache-maven.tar.gz" | sha512sum -c - \
  && tar -xzf /tmp/apache-maven.tar.gz -C /usr/share/maven --strip-components=1 \
  && rm -f /tmp/apache-maven.tar.gz \
  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

# ENV MAVEN_HOME /usr/share/maven
# ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"

# COPY mvn-entrypoint.sh /usr/local/bin/mvn-entrypoint.sh
# COPY settings-docker.xml /usr/share/maven/ref/

# # Copy src files needed for build
# COPY pom.xml /usr/src/app/
# COPY commons/ /usr/src/app/commons/
# COPY test-commons/ /usr/src/app/test-commons/
# COPY converter/ /usr/src/app/converter/
# COPY commandline/ /usr/src/app/commandline/
# COPY rest-api/ /usr/src/app/rest-api/
# COPY generate/ /usr/src/app/generate/
# COPY test-coverage/ /usr/src/app/test-coverage/
# COPY tools/docker/docker-artifacts/ /usr/src/app/tools/docker/docker-artifacts/
# WORKDIR /usr/src/app/

# RUN /usr/local/bin/mvn-entrypoint.sh mvn install -Dmaven.test.skip -Djacoco.skip=true > /dev/null

# # Final stage
# FROM eclipse-temurin:17-jre

# RUN mkdir -p /usr/src/run/
# COPY --from=builder /usr/src/app/tools/docker/docker-artifacts /usr/src/run/
# COPY --from=builder /usr/src/app/rest-api/target/rest-api.jar /usr/src/run/

# WORKDIR /usr/src/run/

# EXPOSE 8443
# CMD ["/usr/src/run/qppConverter.sh"]
